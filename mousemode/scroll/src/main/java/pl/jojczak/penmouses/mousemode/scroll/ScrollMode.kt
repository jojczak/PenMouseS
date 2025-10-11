package pl.jojczak.penmouses.mousemode.scroll

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.os.Handler
import pl.jojczak.penmouses.core.common.notifications.NotificationsManager
import pl.jojczak.penmouses.core.common.spen.SPenManager
import pl.jojczak.penmouses.mousemode.base.BaseMode

class ScrollMode(
    dispatchGesture: (GestureDescription, AccessibilityService.GestureResultCallback?, Handler?) -> Unit,
    notificationsManager: NotificationsManager,
    sPenManager: SPenManager,
    context: Context
) : BaseMode(
    dispatchGesture = dispatchGesture,
    notificationsManager = notificationsManager,
    sPenManager = sPenManager,
    context = context
) {
}