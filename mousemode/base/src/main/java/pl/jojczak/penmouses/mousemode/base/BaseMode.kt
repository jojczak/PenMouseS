package pl.jojczak.penmouses.mousemode.base

import android.accessibilityservice.AccessibilityService.GestureResultCallback
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import pl.jojczak.penmouses.core.common.notifications.NotificationsManager
import pl.jojczak.penmouses.core.common.spen.SPenManager
import pl.jojczak.penmouses.core.common.utils.PreferencesManager
import pl.jojczak.penmouses.core.ui.R as coreR

abstract class BaseMode(
    protected val notificationsManager: NotificationsManager,
    protected val dispatchGesture: (GestureDescription, GestureResultCallback?, Handler?) -> Unit,
    protected val preferences: PreferencesManager,
    protected val sPenManager: SPenManager,
    protected val stopService: () -> Unit,
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

    protected fun showErrorAndStopService(errorCode: Int) {
        when (errorCode) {
            ERROR_UNSUPPORTED_DEVICE -> {
                Toast.makeText(
                    context,
                    context.getString(coreR.string.connection_unsupported_device),
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> {
                Toast.makeText(
                    context,
                    context.getString(coreR.string.connection_unknown_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        stopService()
    }

    protected val tagName by lazy { this::class.simpleName }

    companion object {
        private const val ERROR_UNSUPPORTED_DEVICE: Int = -1
    }
}