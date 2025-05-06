package pl.jojczak.penmouses.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.os.Handler
import android.os.Looper
import android.os.UserManager
import android.util.Log
import android.view.Display
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
import android.view.WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
import android.view.WindowManager.LayoutParams.WRAP_CONTENT
import android.view.accessibility.AccessibilityEvent
import android.widget.ImageView
import androidx.core.view.isGone
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import pl.jojczak.penmouses.notifications.NotificationsManager
import pl.jojczak.penmouses.utils.CursorType
import pl.jojczak.penmouses.utils.PrefKeys
import pl.jojczak.penmouses.utils.PreferencesManager
import pl.jojczak.penmouses.utils.SPenManager
import pl.jojczak.penmouses.utils.getCursorBitmap
import pl.jojczak.penmouses.utils.getDisplaySize
import javax.inject.Inject

@AndroidEntryPoint
class MouseService : AccessibilityService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var appToServiceEventObserver: Job? = null

    private val mainHandler = Handler(Looper.getMainLooper())
    private val hideHandler = Handler(Looper.getMainLooper())
    private val gyroSleepHandler = Handler(Looper.getMainLooper())
    private var windowManager: WindowManager? = null
    private var cursorView: ImageView? = null
    private var display: Display? = null
    private var params: WindowManager.LayoutParams? = null
    private var cursorImageBitmap: Bitmap? = null
    private var isAirMouseRunning = false

    private var hideCursorDelay = PrefKeys.CURSOR_HIDE_DELAY.default.toLong()
        set(value) {
            field = value * 1000L
        }
    private var gyroSleepEnabled = PrefKeys.SPEN_SLEEP_ENABLED.default

    @Inject
    lateinit var preferences: PreferencesManager

    @Inject
    lateinit var sPenManager: SPenManager

    @Inject
    @ApplicationContext
    lateinit var context: Context

    override fun onServiceConnected() {
        super.onServiceConnected()
        val userManager = getSystemService(Context.USER_SERVICE) as UserManager
        userManager.isUserAGoat
        registerReceiver()
    }

    private fun registerReceiver() {
        cancelAppToServiceEventObserver()
        appToServiceEventObserver = serviceScope.launch {
            AppToServiceEvent.event.collect { event ->
                when (event) {
                    AppToServiceEvent.Event.Start -> startAirMouse()
                    AppToServiceEvent.Event.Stop -> stopAirMouse(true)
                    AppToServiceEvent.Event.StopOnDestroy -> stopAirMouse(false)
                    AppToServiceEvent.Event.UpdateSensitivity -> sPenManager.updateSPenSensitivity()
                    AppToServiceEvent.Event.UpdateCursorSize -> mainHandler.post { setupParams() }
                    AppToServiceEvent.Event.UpdateCursorType -> mainHandler.post { setupCursorImage(); setupParams() }
                    AppToServiceEvent.Event.UpdateHideDelay,
                    AppToServiceEvent.Event.UpdateSPenSleepEnabled -> mainHandler.post { updateHideDelay() }
                }
            }
        }
    }

    // Step 1: Check if AirMouse is already running, if not, start it
    private fun startAirMouse() {
        if (isAirMouseRunning) {
            Log.w(TAG, "AirMouse is already running")
            return
        }
        isAirMouseRunning = true
        NotificationsManager.showIdleNotification(this)

        Log.d(TAG, "Starting AirMouse")
        mainHandler.post {
            setupWindowManagerAndDisplay()
            setupOverlayParams()
            setupCursorImage()
            setupParams()
            setupCursorView()
            setupSPen()
            updateHideDelay()
        }
    }

    // Stopping air mouse, clearing all variables
    private fun stopAirMouse(withSPenManager: Boolean) {
        Log.d(TAG, "Stopping AirMouse")
        isAirMouseRunning = false
        hideHandler.removeCallbacks(hideCursorRunnable)
        gyroSleepHandler.removeCallbacks(gyroSleepRunnable)
        mainHandler.post {
            windowManager?.removeView(cursorView)
            sPenManager.disconnectFromSPen()
            windowManager = null
            cursorView = null
            params = null
            display = null
            cursorImageBitmap?.recycle()
            cursorImageBitmap = null
            NotificationsManager.cancelStatusNotifications(this)
        }
    }

    // Step 2: Set up WindowManager and Display
    private fun setupWindowManagerAndDisplay() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val displayManager = getSystemService(DISPLAY_SERVICE) as DisplayManager
        display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
    }

    // Step 3: Set up WindowManager.LayoutParams
    private fun setupOverlayParams() {
        val overlayFlags = FLAG_NOT_FOCUSABLE or
                FLAG_LAYOUT_IN_SCREEN or
                FLAG_NOT_TOUCHABLE or
                FLAG_LAYOUT_NO_LIMITS

        params = WindowManager.LayoutParams(
            WRAP_CONTENT,
            WRAP_CONTENT,
            TYPE_ACCESSIBILITY_OVERLAY,
            overlayFlags,
            PixelFormat.TRANSLUCENT
        ).apply {
            layoutInDisplayCutoutMode = LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
            gravity = Gravity.TOP or Gravity.START
        }
    }

    // Step 4: Setting up cursor image | Updating cursor image when running
    private fun setupCursorImage() {
        val cursorType = preferences.get(PrefKeys.CURSOR_TYPE)
        cursorImageBitmap =
            getCursorBitmap(context, cursorType) ?: getCursorBitmap(context, CursorType.LIGHT)
        cursorView?.setImageBitmap(cursorImageBitmap)
    }

    // Step 5: Setting up cursor size | Updating cursor size when running
    private fun setupParams() {
        val prefSize = preferences.get(PrefKeys.CURSOR_SIZE)
        val density = context.resources.displayMetrics.density
        val cursorSize = (prefSize * density).toInt()

        cursorImageBitmap?.let { imageBitmap ->
            val ratio = imageBitmap.width.toFloat() / imageBitmap.height.toFloat()

            params?.width = (cursorSize * ratio).toInt()
            params?.height = cursorSize

            cursorView?.let { cV ->
                windowManager?.updateViewLayout(cV, params)
            }
        }
    }

    // Step 6: Set up cursor view
    private fun setupCursorView() {
        cursorImageBitmap?.let { imageBitmap ->
            cursorView = ImageView(this).apply {
                setImageBitmap(imageBitmap)

                getDisplaySize(this@MouseService.display) { screenWidth, screenHeight ->
                    val startX = (screenWidth - width) / 2
                    val startY = (screenHeight - height) / 2

                    params?.x = startX
                    params?.y = startY
                    sPenManager.currentPos.x = startX
                    sPenManager.currentPos.y = startY
                }
            }
            windowManager?.addView(cursorView, params)
        }
    }

    // Step 7: Set up S-Pen event listeners
    private fun setupSPen() {
        sPenManager.connectToSPen(
            performTouch = ::performTouch,
            updateLayout = ::updateLayout
        )
    }

    // Layout update when S-Pen moves
    private fun updateLayout(pos: Point) {
        mainHandler.post {
            showCursorIfHidden()
            params?.x = pos.x
            params?.y = pos.y
            windowManager?.updateViewLayout(cursorView, params)
            manageCursorHiding()
        }
    }

    //region Gesture
    private val gestureCallback = object : GestureResultCallback() {
        override fun onCompleted(gestureDescription: GestureDescription?) {
            if (isAirMouseRunning) {
                cursorView?.colorFilter = null
                cursorView?.scaleY = 1f
            }
        }
    }

    // Simulate touch event via AccessibilityService when S-Pen button is clicked
    private fun performTouch(sPenPath: Path, pressTime: Long) {
        Log.d(TAG, "performTouch")

        if (!sPenManager.airMotionEnabled) {
            Log.d(TAG, "Air motion not enabled, registering and ignoring path")
            manageCursorHiding()
            return
        } else {
            manageCursorHiding()
        }

        val gestureStroke = GestureDescription.StrokeDescription(sPenPath, 0, pressTime / 2)
        val gestureBuilder = GestureDescription.Builder().apply {
            addStroke(gestureStroke)
        }

        cursorView?.setColorFilter(Color.valueOf(0f, 0f, 0f, 0.4f).toArgb())
        cursorView?.scaleY = 0.85f
        dispatchGesture(gestureBuilder.build(), gestureCallback, null)
    }
    //endregion

    //region Cursor hiding functions
    private fun updateHideDelay() {
        showCursorIfHidden()
        hideCursorDelay = preferences.get(PrefKeys.CURSOR_HIDE_DELAY).toLong()
        gyroSleepEnabled = preferences.get(PrefKeys.SPEN_SLEEP_ENABLED)
        manageCursorHiding()
    }

    private fun showCursorIfHidden() {
        NotificationsManager.showIdleNotification(this)
        cursorView?.apply {
            if (isGone && alpha == 0f) {
                animate()
                    .alpha(1f)
                    .setDuration(100)
                    .withStartAction {
                        visibility = View.VISIBLE
                    }
                    .start()
            }
        }
    }

    private fun manageCursorHiding() {
        hideHandler.removeCallbacks(hideCursorRunnable)
        gyroSleepHandler.removeCallbacks(gyroSleepRunnable)
        if (!sPenManager.airMotionEnabled) sPenManager.registerAirMotionEventListener()
        showCursorIfHidden()
        if (hideCursorDelay > HIDE_DELAY_INDEFINITELY) {
            Log.d(TAG, "Cursor hiding disabled because hideCursorDelay: $hideCursorDelay")
            tryToEnableGyroSleepTimer()
            return
        }
        hideHandler.postDelayed(
            hideCursorRunnable,
            hideCursorDelay
        )
    }

    private val hideCursorRunnable = Runnable {
        Log.d(TAG, "Hiding cursor")
        NotificationsManager.showMouseHiddenNotification(this)
        tryToEnableGyroSleepTimer()
        cursorView?.apply {
            animate()
                .alpha(0f)
                .setDuration(500)
                .withEndAction {
                    visibility = View.GONE
                }
                .start()
        }
    }

    private fun tryToEnableGyroSleepTimer() {
        if (gyroSleepEnabled) {
            gyroSleepHandler.postDelayed(gyroSleepRunnable, S_PEN_GYRO_SLEEP_TIME)
        } else {
            Log.d(TAG, "Gyro sleep disabled because gyroSleepEnabled: false")
        }
    }

    private val gyroSleepRunnable = Runnable {
        Log.d(TAG, "Unregistering AirMotionEventListener")
        NotificationsManager.showMouseSleepNotification(this)
        sPenManager.unregisterAirMotionEventListener()
    }
    //endregion

    private fun cancelAppToServiceEventObserver() {
        appToServiceEventObserver?.cancel()
        appToServiceEventObserver = null
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        stopAirMouse(true)
        cancelAppToServiceEventObserver()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    override fun onInterrupt() {
        Log.d(TAG, "onInterrupt")
        NotificationsManager.showErrorNotification(this)
        stopAirMouse(true)
    }

    companion object {
        private const val TAG = "MouseService"

        private const val S_PEN_GYRO_SLEEP_TIME = 60000L
        private const val HIDE_DELAY_INDEFINITELY = 300000L
    }
}