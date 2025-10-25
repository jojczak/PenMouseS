package pl.jojczak.penmouses.mousemode.scroll

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.os.Handler
import android.util.Log
import com.samsung.android.sdk.penremote.ButtonEvent
import pl.jojczak.penmouses.core.common.notifications.NotificationsManager
import pl.jojczak.penmouses.core.common.spen.SPenManager
import pl.jojczak.penmouses.core.common.spen.listener.ButtonAction
import pl.jojczak.penmouses.core.common.spen.listener.ConnectionResultCallback
import pl.jojczak.penmouses.mousemode.base.BaseMode
import pl.jojczak.penmouses.mousemode.base.PenConst

class ScrollMode(
    dispatchGesture: (GestureDescription, AccessibilityService.GestureResultCallback?, Handler?) -> Unit,
    notificationsManager: NotificationsManager,
    sPenManager: SPenManager,
    context: Context,
) : BaseMode(
    notificationsManager = notificationsManager,
    dispatchGesture = dispatchGesture,
    sPenManager = sPenManager,
    context = context,
) {
    var yOffset = 0f

    override fun start() {
        super.start()

        sPenManager.connect(object : ConnectionResultCallback() {
            override fun onSuccess() {
                mainHandler.postDelayed({
                    sPenManager.registerButtonEventListener(::onButtonEvent)
                }, PenConst.DELAY_TO_EVENT_REGISTER_MS)
            }
        })
    }

    private fun onButtonEvent(@ButtonAction type: Int, timeStamp: Long) {
        Log.d(tagName, "Button action: $type")
        if (type == ButtonEvent.ACTION_DOWN) {
            sPenManager.registerAirMotionEventListener(::onAirMotionEvent)
        } else if (type == ButtonEvent.ACTION_UP) {
            sPenManager.unregisterAirMotionEventListener()
            Log.d(tagName, "Y offset: $yOffset")
        }
    }

    private fun onAirMotionEvent(deltaX: Float, deltaY: Float, timeStamp: Long) {
        Log.d(tagName, "Motion event: X: $deltaX, Y: $deltaY")
        yOffset += deltaY
    }
}