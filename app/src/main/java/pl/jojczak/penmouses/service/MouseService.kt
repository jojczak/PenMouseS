package pl.jojczak.penmouses.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.jojczak.penmouses.core.common.notifications.NotificationsManager
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent.Event
import pl.jojczak.penmouses.core.common.utils.PreferencesManager
import pl.jojczak.penmouses.core.common.spen.SPenManager
import pl.jojczak.penmouses.mousemode.base.BaseMode
import pl.jojczak.penmouses.mousemode.mouse.MouseMode
import pl.jojczak.penmouses.mousemode.point.PointMode
import javax.inject.Inject

@AndroidEntryPoint
class MouseService : AccessibilityService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var eventCollectorJob: Job? = null
    private var baseMode: BaseMode? = null

    @Inject
    lateinit var preferences: PreferencesManager

    @Inject
    lateinit var sPenManager: SPenManager

    @Inject
    lateinit var notificationsManager: NotificationsManager

    override fun onServiceConnected() {
        super.onServiceConnected()
        registerReceiver()
    }

    private fun registerReceiver() {
        cancelAppToServiceEventObserver()
        eventCollectorJob = serviceScope.launch {
            AppToServiceEvent.event.collect(::eventManager)
        }
    }

    private fun eventManager(event: Event) {
        Log.d(TAG, "Received event: $event")
        when (event) {
            is Event.Start -> stopCurrentStartNew(newMode = event.mode)
            is Event.UpdateCursorSize -> baseMode?.updateSize()
            is Event.UpdateCursorBitmap -> baseMode?.updateBitmap()
            is Event.UpdateSensitivity -> baseMode?.updateSensitivity()
            is Event.UpdateHideDelay -> baseMode?.updateHideDelay()
            is Event.UpdateSPenSleepEnabled -> baseMode?.updateSleepEnabled()
            is Event.Stop -> stopMode()
        }
    }

    private fun stopCurrentStartNew(newMode: AppToServiceEvent.PenMode) = serviceScope.launch {
        stopMode()
        delay(DELAY_BETWEEN_MODES)
        baseMode = getNewMode(newMode)
        baseMode?.start()
        AppToServiceEvent.serviceStatus.tryEmit(newMode)
    }

    private fun getNewMode(newMode: AppToServiceEvent.PenMode) = when (newMode) {
        AppToServiceEvent.PenMode.Mouse -> MouseMode(
            dispatchGesture = ::dispatchGesture,
            notificationsManager = notificationsManager,
            sPenManager = sPenManager,
            context = this,
            preferences = preferences
        )

        AppToServiceEvent.PenMode.Point -> PointMode(
            dispatchGesture = ::dispatchGesture,
            notificationsManager = notificationsManager,
            sPenManager = sPenManager,
            context = this,
            preferences = preferences
        )

        else -> null
    }

    private fun stopMode() {
        val currentModeName = baseMode?.let { it::class.simpleName } ?: "null"
        Log.d(TAG, "Stopping current mode ($currentModeName)")
        baseMode?.stop()
        baseMode = null
        AppToServiceEvent.serviceStatus.tryEmit(AppToServiceEvent.PenMode.Off)
    }

    private fun cancelAppToServiceEventObserver() {
        eventCollectorJob?.cancel()
        eventCollectorJob = null
    }

    override fun onInterrupt() {
        Log.e(TAG, "Accessibility service interrupted")
        stopMode()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    companion object {
        private const val TAG = "MouseService"
        private const val DELAY_BETWEEN_MODES = 2000L
    }
}