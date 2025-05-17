package pl.jojczak.penmouses.utils

import android.accessibilityservice.AccessibilityService.DISPLAY_SERVICE
import android.app.Activity
import android.content.Context
import android.graphics.Path
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.Display
import com.samsung.android.sdk.penremote.AirMotionEvent
import com.samsung.android.sdk.penremote.ButtonEvent
import com.samsung.android.sdk.penremote.SpenEvent
import com.samsung.android.sdk.penremote.SpenEventListener
import com.samsung.android.sdk.penremote.SpenRemote
import com.samsung.android.sdk.penremote.SpenRemote.Error.CONNECTION_FAILED
import com.samsung.android.sdk.penremote.SpenRemote.Error.UNSUPPORTED_DEVICE
import com.samsung.android.sdk.penremote.SpenUnit
import com.samsung.android.sdk.penremote.SpenUnitManager
import kotlinx.coroutines.flow.update
import pl.jojczak.penmouses.di.ActivityProvider
import pl.jojczak.penmouses.service.AppToServiceEvent
import kotlin.math.hypot

class SPenManager(
    private val activityProvider: ActivityProvider,
    private val preferences: PreferencesManager
) {
    private var sPenUnitManager: SpenUnitManager? = null
    private var display: Display? = null
    private var performTouch: ((Path, Long) -> Unit)? = null
    private var updateLayout: ((Point) -> Unit)? = null

    private var isSPenPressed = false
    private var pressedTime = 0L
    private var sPenPath = Path()
    private val handler = Handler(Looper.getMainLooper())

    private var sPenSensitivity = 0f
    private val lastPathPos = Point(0, 0)
    private var cursorMoveThreshold = 0f
    val currentPos = Point(0, 0)

    var airMotionEnabled = false
        private set

    // Step 1: Getting activity, setting up event listeners and calling other init methods
    fun connectToSPen(
        context: Context,
        performTouch: (Path, Long) -> Unit,
        updateLayout: (Point) -> Unit
    ) {
        Log.i(TAG, "Connecting to S-Pen...")

        val activity = activityProvider.getActivity() ?: run {
            Log.e(TAG, "Activity is null, cannot connect to S-Pen")
            return
        }

        this.performTouch = performTouch
        this.updateLayout = updateLayout

        cursorMoveThreshold = context.resources?.displayMetrics?.let { dM ->
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CURSOR_MOVE_THRESHOLD, dM)
        } ?: 0f

        setupDisplay(context)
        setupConnection(activity)
        updateSPenSensitivity()
    }

    // Step 2: Set up Display
    private fun setupDisplay(context: Context) {
        val displayManager = context.getSystemService(DISPLAY_SERVICE) as DisplayManager
        display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
    }

    // Step 3: Set up Connection
    private fun setupConnection(activity: Activity) {
        if (isSPenSupported()) {
            val sPenRemote = SpenRemote.getInstance()

            if (!sPenRemote.isConnected) {
                sPenRemote.connect(activity, ConnectionResultCallback())
            } else {
                AppToServiceEvent.serviceStatus.update { AppToServiceEvent.ServiceStatus.ON }
            }
        } else {
            Log.w(TAG, "S-Pen is not supported, cannot connect")
            AppToServiceEvent.event.tryEmit(AppToServiceEvent.Event.Stop)
        }
    }

    private inner class ConnectionResultCallback : SpenRemote.ConnectionResultCallback {
        override fun onSuccess(sPenUnitManager: SpenUnitManager?) {
            Log.i(TAG, "Connection successful")
            this@SPenManager.sPenUnitManager = sPenUnitManager
            AppToServiceEvent.serviceStatus.update { AppToServiceEvent.ServiceStatus.ON }

            Handler(Looper.getMainLooper()).postDelayed({
                registerButtonEventListener()
                registerAirMotionEventListener()
            }, DELAY_TO_EVENT_REGISTER)
        }

        override fun onFailure(errorCode: Int) {
            Log.e(TAG, "Connection failed: ${mapConnectionError(errorCode)}")
            AppToServiceEvent.serviceStatus.update { AppToServiceEvent.ServiceStatus.OFF }
        }
    }

    fun registerButtonEventListener() {
        sPenUnitManager?.let {
            val buttonUnit = it.getUnit(SpenUnit.TYPE_BUTTON)
            it.registerSpenEventListener(ButtonEventListener(), buttonUnit)

            Log.i(TAG, "Button event listener registered")
        }
    }

    private inner class ButtonEventListener : SpenEventListener {
        override fun onEvent(event: SpenEvent) {
            val mEvent = ButtonEvent(event)

            if (mEvent.action == ButtonEvent.ACTION_DOWN) {
                isSPenPressed = true
                pressedTime = 0
                sPenPath.reset()
                sPenPath.moveTo(currentPos.x.toFloat(), currentPos.y.toFloat())
                handler.post(runnable)
            } else {
                isSPenPressed = false
            }
        }
    }

    private val runnable = object : Runnable {
        override fun run() {
            val dx = currentPos.x.toFloat() - lastPathPos.x
            val dy = currentPos.y.toFloat() - lastPathPos.y
            val distance = hypot(dx, dy)

            if (distance >= cursorMoveThreshold) {
                sPenPath.lineTo(currentPos.x.toFloat(), currentPos.y.toFloat())
                lastPathPos.set(currentPos.x, currentPos.y)
            }
            pressedTime += TICK_TIME

            if (isSPenPressed && pressedTime < MAX_BUTTON_DOWN_TIME) {
                handler.postDelayed(this, TICK_TIME)
            } else {
                performTouch?.invoke(sPenPath, pressedTime)
            }
        }
    }

    fun registerAirMotionEventListener() {
        if (airMotionEnabled) {
            Log.w(TAG, "Air motion event listener already registered")
            return
        }
        sPenUnitManager?.let {
            val airMotionUnit = it.getUnit(SpenUnit.TYPE_AIR_MOTION)
            it.registerSpenEventListener(AirMotionEventListener(), airMotionUnit)
            airMotionEnabled = true
            Log.i(TAG, "Air motion event listener registered")
        }
    }

    fun unregisterAirMotionEventListener() {
        sPenUnitManager?.let {
            val airMotionUnit = it.getUnit(SpenUnit.TYPE_AIR_MOTION)
            it.unregisterSpenEventListener(airMotionUnit)
            airMotionEnabled = false
            Log.i(TAG, "Air motion event listener unregistered")
        } ?: run {
            Log.w(TAG, "sPenUnitManager is null, cannot unregister AirMotionEventListener")
        }
    }

    private inner class AirMotionEventListener : SpenEventListener {
        override fun onEvent(event: SpenEvent) {
            getDisplaySize(display) { screenWidth, screenHeight ->
                val mEvent = AirMotionEvent(event)

                currentPos.x += (mEvent.deltaX * (sPenSensitivity * S_PEN_SENSITIVITY_MULTIPLIER)).toInt()
                currentPos.y -= (mEvent.deltaY * (sPenSensitivity * S_PEN_SENSITIVITY_MULTIPLIER)).toInt()

                currentPos.x = currentPos.x.coerceIn(0, screenWidth)
                currentPos.y = currentPos.y.coerceIn(0, screenHeight)

                updateLayout?.invoke(currentPos)
            }
        }
    }

    fun disconnectFromSPen() {
        Log.d(TAG, "Disconnecting from S-Pen...")

        sPenUnitManager?.let {
            val buttonUnit = it.getUnit(SpenUnit.TYPE_BUTTON)
            val airMotionUnit = it.getUnit(SpenUnit.TYPE_AIR_MOTION)
            it.unregisterSpenEventListener(buttonUnit)
            it.unregisterSpenEventListener(airMotionUnit)
        }
        sPenUnitManager = null
        try {
            SpenRemote.getInstance().disconnect(activityProvider.getActivity())
        } catch (e: Exception) {
            Log.e(TAG, "Error disconnecting from S-Pen, probably activity destroyed before disconnect", e)
        }

        performTouch = null
        updateLayout = null
        display = null
        airMotionEnabled = false
        AppToServiceEvent.serviceStatus.update { AppToServiceEvent.ServiceStatus.OFF }
    }

    private fun mapConnectionError(errorCode: Int) = when (errorCode) {
        UNSUPPORTED_DEVICE -> "Unsupported device"
        CONNECTION_FAILED -> "Connection failed"
        else -> "Unknown error"
    }

    fun updateSPenSensitivity() {
        sPenSensitivity = preferences.get(PrefKeys.SPEN_SENSITIVITY)
    }

    companion object {
        private const val TAG = "SPenManager"

        private const val DELAY_TO_EVENT_REGISTER = 750L
        private const val MAX_BUTTON_DOWN_TIME = 1501
        private const val TICK_TIME = 25L
        private const val S_PEN_SENSITIVITY_MULTIPLIER = 20
        private const val CURSOR_MOVE_THRESHOLD = 25f

        fun isSPenSupported() = try {
            val sPenClass = Class.forName("com.samsung.android.sdk.penremote.SpenRemote")
            val sPenRemote = sPenClass.getMethod("getInstance").invoke(null)

            val isButtonEnabled = sPenClass.getMethod("isFeatureEnabled", Int::class.java)
                .invoke(sPenRemote, SpenRemote.FEATURE_TYPE_BUTTON) as Boolean
            val isAirMotionEnabled = sPenClass.getMethod("isFeatureEnabled", Int::class.java)
                .invoke(sPenRemote, SpenRemote.FEATURE_TYPE_AIR_MOTION) as Boolean

            if (isButtonEnabled && isAirMotionEnabled) {
                Log.i(TAG, "S-Pen is supported")
                true
            } else {
                Log.w(TAG, "S-Pen is not supported")
                false
            }
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "SpenRemote class not found, S-Pen features unavailable", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if S-Pen is supported", e)
            false
        }
    }
}
