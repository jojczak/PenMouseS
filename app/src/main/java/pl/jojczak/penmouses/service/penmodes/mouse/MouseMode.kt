package pl.jojczak.penmouses.service.penmodes.mouse

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.graphics.Point
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import com.samsung.android.sdk.penremote.ButtonEvent
import pl.jojczak.penmouses.notifications.NotificationsManager
import pl.jojczak.penmouses.service.SPenManager
import pl.jojczak.penmouses.service.listeners.ButtonAction
import pl.jojczak.penmouses.service.listeners.ConnectionResultCallback
import pl.jojczak.penmouses.service.penmodes.base.PenConst
import pl.jojczak.penmouses.service.penmodes.base.PenRunnable
import pl.jojczak.penmouses.service.penmodes.base.cursor.CursorMode
import pl.jojczak.penmouses.utils.PreferencesManager
import pl.jojczak.penmouses.utils.getDisplaySize
import kotlin.math.pow
import kotlin.math.sqrt

class MouseMode(
    dispatchGesture: (GestureDescription, AccessibilityService.GestureResultCallback?, Handler?) -> Unit,
    sPenManager: SPenManager,
    context: Context,
    preferences: PreferencesManager
) : CursorMode(
    dispatchGesture = dispatchGesture,
    sPenManager = sPenManager,
    context = context,
    preferences = preferences
) {
    private var sensitivity = cursorPreferences.getSensitivity()
    private var hideDelay = cursorPreferences.getHideDelay()
    private var isSleepEnabled = cursorPreferences.isSleepEnabled()
    private var path = Path()
    private var pathStartTime: Long = 0
    private var cursorStartPos = Point()

    override val cursorAnimator = MouseAnimator(
        cursorState = cursorState,
        mainHandler = mainHandler,
        windowManager = windowManager,
        onCursorHidden = ::onCursorHidden,
        onCursorShown = ::onCursorShown,
        onCursorSleep = ::onCursorSleep,
        onCursorWakeup = ::onCursorWakeup,
        getCanStartJobs = { canStartJobs }
    )

    override fun start() {
        super.start()

        sPenManager.connect(object : ConnectionResultCallback() {
            override fun onSuccess() {
                mainHandler.postDelayed({
                    sPenManager.registerButtonEventListener(::onButtonEvent)
                    sPenManager.registerAirMotionEventListener(::onAirMotionEvent)
                }, PenConst.DELAY_TO_EVENT_REGISTER_MS)
            }
        })
    }

    override fun stop() {
        super.stop()
        sPenManager.disconnect()
    }

    private fun onButtonEvent(@ButtonAction type: Int, timeStamp: Long) {
        cursorAnimator.showCursor(hideDelay)

        if (type == ButtonEvent.ACTION_DOWN) {
            val cursorPosition = getCursorPos()
            pathStartTime = timeStamp
            cursorStartPos = Point(cursorPosition)
            cursorAnimator.clickCursor()

            path = Path()
            path.moveTo(cursorPosition.x.toFloat(), cursorPosition.y.toFloat())

            mainHandler.post(updateStrokePathJob)
        } else if (type == ButtonEvent.ACTION_UP) {
            cursorAnimator.releaseCursor()

            mainHandler.removeCallbacks(updateStrokePathJob)

            performGesture(duration = timeStamp - pathStartTime)
        }
    }

    private fun onAirMotionEvent(deltaX: Float, deltaY: Float, timeStamp: Long) {
        Log.d(tagName, "Motion event: X: $deltaX, Y: $deltaY")

        cursorAnimator.showCursor(hideDelay)

        val (screenWidth, screenHeight) = getDisplaySize(getDisplay())

        updateCursorLayoutParams {
            x = (x + (deltaX * sensitivity * S_PEN_SENSITIVITY_MULTIPLIER).toInt()).coerceIn(0, screenWidth)
            y = (y + (-deltaY * sensitivity * S_PEN_SENSITIVITY_MULTIPLIER).toInt()).coerceIn(0, screenHeight)
        }
    }

    private val updateStrokePathJob = object : PenRunnable(canStartJobs) {
        override fun callback() {
            if (!sPenManager.isSPenButtonDown) return

            val duration = SystemClock.elapsedRealtime() - pathStartTime
            if (duration >= PenConst.MAX_CLICK_TIME_MS) {
                sPenManager.isSPenButtonDown = false
                cursorAnimator.releaseCursor()
                performGesture( PenConst.MAX_CLICK_TIME_MS)
                return
            }

            val cursorPosition = getCursorPos()
            path.lineTo(cursorPosition.x.toFloat(), cursorPosition.y.toFloat())
            mainHandler.postDelayed(this,  PenConst.CLICK_PEN_UPDATE_INTERVAL_MS)
        }
    }

    private fun performGesture(duration: Long) {
        val cursorPosition = getCursorPos()
        val dx = (cursorPosition.x - cursorStartPos.x).toFloat()
        val dy = (cursorPosition.y - cursorStartPos.y).toFloat()
        val distance = sqrt(dx.pow(2) + dy.pow(2))

        val (mDuration, mPath) = if (distance < CURSOR_MOVE_THRESHOLD_PX) {
            val staticPath = Path()
            staticPath.moveTo(cursorPosition.x.toFloat(), cursorPosition.y.toFloat())

            duration to staticPath
        } else {
            (duration / 2) to path
        }

        val stroke = GestureDescription.StrokeDescription(mPath, 0, mDuration)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        dispatchGesture(gesture, null, null)
    }

    override fun updateSensitivity() {
        sensitivity = cursorPreferences.getSensitivity()
    }

    override fun updateHideDelay() {
        hideDelay = cursorPreferences.getHideDelay()
    }

    override fun updateSleepEnabled() {
        isSleepEnabled = cursorPreferences.isSleepEnabled()
    }

    fun onCursorHidden() {
        NotificationsManager.showMouseHiddenNotification(context)
    }

    fun onCursorShown() {
        NotificationsManager.showIdleNotification(context)
    }

    fun onCursorSleep() {
        NotificationsManager.showMouseSleepNotification(context)
        sPenManager.unregisterAirMotionEventListener()
    }

    fun onCursorWakeup() {
        NotificationsManager.showIdleNotification(context)
        sPenManager.registerAirMotionEventListener(::onAirMotionEvent)
    }

    companion object {
        private const val S_PEN_SENSITIVITY_MULTIPLIER = 20
        private const val CURSOR_MOVE_THRESHOLD_PX = 50
    }
}