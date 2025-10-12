package pl.jojczak.penmouses.mousemode.mouse

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.graphics.Point
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import com.samsung.android.sdk.penremote.ButtonEvent
import pl.jojczak.penmouses.core.common.notifications.NotificationsManager
import pl.jojczak.penmouses.core.common.spen.SPenManager
import pl.jojczak.penmouses.core.common.spen.listener.ButtonAction
import pl.jojczak.penmouses.core.common.spen.listener.ConnectionResultCallback
import pl.jojczak.penmouses.core.common.utils.PrefKeys
import pl.jojczak.penmouses.core.common.utils.PreferencesManager
import pl.jojczak.penmouses.core.common.utils.getDisplaySize
import pl.jojczak.penmouses.mousemode.base.PenConst
import pl.jojczak.penmouses.mousemode.base.PenRunnable
import pl.jojczak.penmouses.mousemode.basecursor.CursorMode
import kotlin.math.pow
import kotlin.math.sqrt

class MouseMode(
    dispatchGesture: (GestureDescription, AccessibilityService.GestureResultCallback?, Handler?) -> Unit,
    notificationsManager: NotificationsManager,
    preferences: PreferencesManager,
    sPenManager: SPenManager,
    context: Context,
) : CursorMode<MouseAnimator>(
    notificationsManager = notificationsManager,
    dispatchGesture = dispatchGesture,
    preferences = preferences,
    sPenManager = sPenManager,
    context = context,
    animatorFactory = { view -> MouseAnimator(view) }
) {
    private var prefSensitivity = cursorPreferences.getSensitivity()
    private var prefHideDelay = cursorPreferences.getHideDelay()
    private var prefIsSleepEnabled = cursorPreferences.isSleepEnabled()

    private var path = Path()
    private var pathStartTime: Long = 0
    private var cursorStartPos = Point()

    private var isHidden = false
    private var isSleeping = false


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
        if (type == ButtonEvent.ACTION_DOWN) {
            showCursor()
            val cursorPosition = getCursorPos()
            pathStartTime = timeStamp
            cursorStartPos = Point(cursorPosition)
            cursorAnimator.clickCursor()

            path = Path()
            path.moveTo(cursorPosition.x.toFloat(), cursorPosition.y.toFloat())

            mainHandler.post(updateStrokePathJob)
        } else if (type == ButtonEvent.ACTION_UP) {
            hideCursor()
            cursorAnimator.releaseCursor()

            mainHandler.removeCallbacks(updateStrokePathJob)

            performGesture(duration = timeStamp - pathStartTime)
        }
    }

    private fun onAirMotionEvent(deltaX: Float, deltaY: Float, timeStamp: Long) {
        Log.d(tagName, "Motion event: X: $deltaX, Y: $deltaY")
        pingCursor()

        val (screenWidth, screenHeight) = getDisplaySize(getDisplay())

        updateCursorLayoutParams {
            x = (x + (deltaX * prefSensitivity * S_PEN_SENSITIVITY_MULTIPLIER).toInt()).coerceIn(0, screenWidth)
            y = (y + (-deltaY * prefSensitivity * S_PEN_SENSITIVITY_MULTIPLIER).toInt()).coerceIn(0, screenHeight)
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

    private fun pingCursor() {
        showCursor()
        hideCursor()
    }

    private fun showCursor() {
        mainHandler.removeCallbacks(fadeOutCursorJob)
        mainHandler.removeCallbacks(sleepJob)
        if (isHidden || isSleeping) {
            isHidden = false
            notificationsManager.showIdleNotification(context)
            cursorAnimator.fadeInCursor()
        }
        if (isSleeping) {
            isSleeping = false
            sPenManager.registerAirMotionEventListener(::onAirMotionEvent)
            return
        }
    }

    private fun hideCursor() {
        if (prefHideDelay == PrefKeys.CURSOR_HIDE_DELAY.range.endInclusive.toLong()) {
            if (prefIsSleepEnabled) {
                mainHandler.postDelayed(sleepJob, prefHideDelay + SLEEP_DELAY_MS_HIDING_DISABLED)
            }
        } else {
            mainHandler.postDelayed(fadeOutCursorJob, prefHideDelay)
            if (prefIsSleepEnabled) {
                mainHandler.postDelayed(sleepJob, prefHideDelay + SLEEP_DELAY_MS)
            }
        }
    }

    private val fadeOutCursorJob = PenRunnable.create(canStartJobs) {
        isHidden = true
        cursorAnimator.fadeOutCursor()
        notificationsManager.showMouseHiddenNotification(context)
    }

    private val sleepJob = PenRunnable.create(canStartJobs) {
        isSleeping = true
        cursorAnimator.fadeOutCursor()
        sPenManager.unregisterAirMotionEventListener()
        notificationsManager.showMouseSleepNotification(context)
    }

    override fun updateBitmap() {
        super.updateBitmap()
        pingCursor()
    }

    override fun updateSize(){
        super.updateSize()
        pingCursor()
    }

    override fun updateHideDelay() {
        prefHideDelay = cursorPreferences.getHideDelay()
        pingCursor()
    }

    override fun updateSensitivity() {
        prefSensitivity = cursorPreferences.getSensitivity()
        pingCursor()
    }

    override fun updateSleepEnabled() {
        prefIsSleepEnabled = cursorPreferences.isSleepEnabled()
        pingCursor()
    }

    companion object {
        private const val S_PEN_SENSITIVITY_MULTIPLIER = 20
        private const val CURSOR_MOVE_THRESHOLD_PX = 50
        private const val SLEEP_DELAY_MS = 60000L // 60s
        private const val SLEEP_DELAY_MS_HIDING_DISABLED = 300000L // 5min
    }
}