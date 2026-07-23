package pl.jojczak.penmouses.mousemode.point

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Path
import android.graphics.Point
import android.os.Handler
import android.os.SystemClock
import android.view.MotionEvent
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import android.widget.ImageView
import com.samsung.android.sdk.penremote.ButtonEvent
import pl.jojczak.penmouses.core.common.notifications.NotificationsManager
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent.ModeStatus
import pl.jojczak.penmouses.core.common.spen.SPenManager
import pl.jojczak.penmouses.core.common.spen.listener.ButtonAction
import pl.jojczak.penmouses.core.common.spen.listener.ConnectionResultCallback
import pl.jojczak.penmouses.core.common.utils.PreferencesManager
import pl.jojczak.penmouses.mousemode.base.PenConst
import pl.jojczak.penmouses.mousemode.base.PenRunnable
import pl.jojczak.penmouses.mousemode.basecursor.CursorMode

class PointMode(
    dispatchGesture: (GestureDescription, AccessibilityService.GestureResultCallback?, Handler?) -> Unit,
    notificationsManager: NotificationsManager,
    preferences: PreferencesManager,
    sPenManager: SPenManager,
    stopService: () -> Unit,
    context: Context,
) : CursorMode<PointAnimator>(
    notificationsManager = notificationsManager,
    dispatchGesture = dispatchGesture,
    preferences = preferences,
    sPenManager = sPenManager,
    stopService = stopService,
    context = context,
    modeStatus = ModeStatus.Point,
    animatorFactory = { view -> PointAnimator(view) }
) {
    private var clickStartTime: Long = 0

    override fun start() {
        super.start()

        view.post {
            view.setupDraggableCursor()
        }

        sPenManager.connect(object : ConnectionResultCallback() {
            override fun onSuccess() {
                mainHandler.postDelayed({
                    sPenManager.registerButtonEventListener(
                        ::onButtonEvent,
                        ::showErrorAndStopService
                    )
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
            clickStartTime = timeStamp
            cursorAnimator.clickCursor()
            mainHandler.post(checkClickTime)
        } else if (type == ButtonEvent.ACTION_UP) {
            cursorAnimator.releaseCursor()
            performGesture(duration = timeStamp - clickStartTime)
        }
    }

    private val checkClickTime = object : PenRunnable(canStartJobs) {
        override fun callback() {
            if (!sPenManager.isSPenButtonDown) return

            val duration = SystemClock.elapsedRealtime() - clickStartTime
            if (duration >= PenConst.MAX_CLICK_TIME_MS) {
                sPenManager.isSPenButtonDown = false
                cursorAnimator.releaseCursor()
                performGesture(duration)
                return
            }

            mainHandler.postDelayed(this, PenConst.CLICK_PEN_UPDATE_INTERVAL_MS)
        }
    }

    private fun performGesture(duration: Long) {
        val cursorPos = getCursorPos()
        val path = Path()
        path.moveTo(cursorPos.x.toFloat() - 1, cursorPos.y.toFloat() - 1)

        val stroke = GestureDescription.StrokeDescription(path, 0, duration)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()

        dispatchGesture(gesture, null, null)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun ImageView.setupDraggableCursor() {
        val initial = Point()
        val initialTouch = Point()

        updateCursorLayoutParams {
            flags = PenConst.OVERLAY_FLAGS and FLAG_NOT_TOUCHABLE.inv()
        }

        setOnTouchListener { view, event ->
            val lp = layoutParams as WindowManager.LayoutParams

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initial.x = lp.x
                    initial.y = lp.y
                    initialTouch.x = event.rawX.toInt()
                    initialTouch.y = event.rawY.toInt()

                    return@setOnTouchListener true
                }

                MotionEvent.ACTION_MOVE -> {
                    lp.x = initial.x + (event.rawX - initialTouch.x).toInt()
                    lp.y = initial.y + (event.rawY - initialTouch.y).toInt()

                    windowManager.updateViewLayout(view, lp)

                    return@setOnTouchListener true
                }
            }
            false
        }
    }
}