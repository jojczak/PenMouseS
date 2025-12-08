package pl.jojczak.penmouses.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
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
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent.ModeStatus
import pl.jojczak.penmouses.core.common.spen.SPenManager
import pl.jojczak.penmouses.core.common.utils.Analytics
import pl.jojczak.penmouses.core.common.utils.PreferencesManager
import pl.jojczak.penmouses.mousemode.base.BaseMode
import pl.jojczak.penmouses.mousemode.mouse.MouseMode
import pl.jojczak.penmouses.mousemode.point.PointMode
import pl.jojczak.penmouses.mousemode.scroll.ScrollMode
import javax.inject.Inject

@AndroidEntryPoint
class MouseService : AccessibilityService() {

    private val serviceScope = CoroutineScope(context = SupervisorJob() + Dispatchers.Default)
    private var eventCollectorJob: Job? = null
    private var currentMode: BaseMode? = null

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
            AppToServiceEvent.event.collect(collector = ::eventManager)
        }
    }

    private fun eventManager(event: Event) {
        Log.d(TAG, "Received event: $event")
        when (event) {
            is Event.Start -> stopCurrentStartNew(newMode = event.mode)
            is Event.Stop -> stopMode()
            is Event.PreferencesUpdated -> currentMode?.preferencesUpdated()
        }
    }

    private fun stopCurrentStartNew(newMode: ModeStatus) = serviceScope.launch {
        if (newMode != ModeStatus.Off) {
            Firebase.analytics.logEvent(Analytics.EVENT_MODE_STARTED) {
                param(FirebaseAnalytics.Param.ITEM_NAME, newMode.name)
            }
        }

        stopMode(stopMode = ModeStatus.Loading)
        delay(timeMillis = DELAY_BETWEEN_MODES)
        currentMode = getNewMode(newMode = newMode)
        currentMode?.start()
        AppToServiceEvent.serviceStatus.tryEmit(value = newMode)
    }

    private fun getNewMode(newMode: ModeStatus) = when (newMode) {
        ModeStatus.Mouse -> MouseMode(
            notificationsManager = notificationsManager,
            dispatchGesture = ::dispatchGesture,
            preferences = preferences,
            sPenManager = sPenManager,
            stopService = ::stopMode,
            context = this,
        )

        ModeStatus.Point -> PointMode(
            notificationsManager = notificationsManager,
            dispatchGesture = ::dispatchGesture,
            preferences = preferences,
            sPenManager = sPenManager,
            stopService = ::stopMode,
            context = this,
        )

        ModeStatus.Scroll -> ScrollMode(
            notificationsManager = notificationsManager,
            dispatchGesture = ::dispatchGesture,
            preferences = preferences,
            sPenManager = sPenManager,
            stopService = ::stopMode,
            context = this,
        )

        else -> null
    }

    private fun stopMode(stopMode: ModeStatus = ModeStatus.Off) {
        val currentModeName = currentMode?.let { it::class.simpleName } ?: "null"
        Log.d(TAG, "Stopping current mode ($currentModeName)")
        currentMode?.stop()
        currentMode = null
        AppToServiceEvent.serviceStatus.tryEmit(value = stopMode)
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
        private const val DELAY_BETWEEN_MODES = 1000L
    }
}