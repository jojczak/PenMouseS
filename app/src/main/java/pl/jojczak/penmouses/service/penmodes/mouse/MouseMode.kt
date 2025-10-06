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

    private val mouseAnimator = MouseAnimator(
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
                }, DELAY_TO_EVENT_REGISTER_MS)

                mainHandler.post(mouseAnimator.updateCursorViewPosition)
            }
        })
    }

    override fun stop() {
        super.stop()
        sPenManager.disconnect()
    }

    private fun onButtonEvent(@ButtonAction type: Int, timeStamp: Long) {
        mouseAnimator.showCursor(hideDelay)

        if (type == ButtonEvent.ACTION_DOWN) {
            pathStartTime = timeStamp
            cursorStartPos = Point(cursorState.position)
            mouseAnimator.clickCursor()

            path = Path()
            path.moveTo(cursorState.position.x.toFloat(), cursorState.position.y.toFloat())

            mainHandler.post(updateStrokePathJob)
        } else if (type == ButtonEvent.ACTION_UP) {
            mouseAnimator.releaseCursor()

            mainHandler.removeCallbacks(updateStrokePathJob)

            val duration = timeStamp - pathStartTime
            performGesture(duration)
        }
    }

    private fun onAirMotionEvent(deltaX: Float, deltaY: Float, timeStamp: Long) {
        Log.d(tagName, "Motion event: X: $deltaX, Y: $deltaY")

        mouseAnimator.showCursor(hideDelay)

        val (screenWidth, screenHeight) = getDisplaySize(getDisplay())

        cursorState.position.x =
            (cursorState.position.x + (deltaX * sensitivity * S_PEN_SENSITIVITY_MULTIPLIER).toInt())
                .coerceIn(0, screenWidth)
        cursorState.position.y =
            (cursorState.position.y + (-deltaY * sensitivity * S_PEN_SENSITIVITY_MULTIPLIER).toInt())
                .coerceIn(0, screenHeight)
    }

    private val updateStrokePathJob = object : PenRunnable(canStartJobs) {
        override fun callback() {
            if (!sPenManager.isSPenButtonDown) return

            val duration = SystemClock.elapsedRealtime() - pathStartTime
            if (duration > MAX_DOWN_TIME_MS) {
                sPenManager.isSPenButtonDown = false
                mouseAnimator.releaseCursor()
                performGesture(MAX_DOWN_TIME_MS)
                return
            }

            path.lineTo(cursorState.position.x.toFloat(), cursorState.position.y.toFloat())
            mainHandler.postDelayed(this, PATH_UPDATE_INTERVAL_MS)
        }
    }

    private fun performGesture(duration: Long) {
        val dx = (cursorState.position.x - cursorStartPos.x).toFloat()
        val dy = (cursorState.position.y - cursorStartPos.y).toFloat()
        val distance = sqrt(dx.pow(2) + dy.pow(2))

        val (mDuration, mPath) = if (distance < CURSOR_MOVE_THRESHOLD_PX) {
            val staticPath = Path()
            staticPath.moveTo(cursorState.position.x.toFloat(), cursorState.position.y.toFloat())

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
        private const val DELAY_TO_EVENT_REGISTER_MS = 1000L
        private const val PATH_UPDATE_INTERVAL_MS = 35L
        private const val MAX_DOWN_TIME_MS = 1000L
        private const val CURSOR_MOVE_THRESHOLD_PX = 50
    }
}