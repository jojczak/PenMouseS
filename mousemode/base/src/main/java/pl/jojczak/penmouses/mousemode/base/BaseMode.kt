package pl.jojczak.penmouses.mousemode.base

import android.accessibilityservice.AccessibilityService.GestureResultCallback
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.os.Handler
import android.os.Looper
import pl.jojczak.penmouses.core.common.notifications.NotificationsManager
import pl.jojczak.penmouses.core.common.spen.SPenManager

abstract class BaseMode(
    protected val notificationsManager: NotificationsManager,
    protected val dispatchGesture: (GestureDescription, GestureResultCallback?, Handler?) -> Unit,
    protected val sPenManager: SPenManager,
    protected val context: Context,
) {
    protected val mainHandler = Handler(Looper.getMainLooper())

    protected var canStartJobs = true

    open fun start() = Unit

    open fun stop() {
        canStartJobs = false
        mainHandler.removeCallbacksAndMessages(null)
        notificationsManager.cancelStatusNotifications(context)
    }

    open fun preferencesUpdated() = Unit

    protected val tagName by lazy { this::class.simpleName }
}