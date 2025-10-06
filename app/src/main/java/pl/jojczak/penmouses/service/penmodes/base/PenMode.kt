package pl.jojczak.penmouses.service.penmodes.base

import android.accessibilityservice.AccessibilityService.GestureResultCallback
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import pl.jojczak.penmouses.notifications.NotificationsManager
import pl.jojczak.penmouses.service.SPenManager

abstract class PenMode(
    protected val dispatchGesture: (GestureDescription, GestureResultCallback?, Handler?) -> Unit,
    protected val sPenManager: SPenManager,
    protected val context: Context
) {
    protected val mainHandler = Handler(Looper.getMainLooper())
    protected var canStartJobs = true

    open fun start() = Unit

    open fun stop() {
        canStartJobs = false
        mainHandler.removeCallbacksAndMessages(null)
        NotificationsManager.cancelStatusNotifications(context)
    }

    open fun updateSize() {
        Log.i(tagName, "Update size not implemented")
    }

    open fun updateBitmap() {
        Log.i(tagName, "Update bitmap not implemented")
    }

    open fun updateSensitivity() {
        Log.i(tagName, "Update sensitivity not implemented")
    }

    open fun updateHideDelay() {
        Log.i(tagName, "Update hide delay not implemented")
    }

    open fun updateSleepEnabled() {
        Log.i(tagName, "Update sleep enabled not implemented")
    }

    protected val tagName by lazy { this::class.simpleName }
}