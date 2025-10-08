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
import pl.jojczak.penmouses.service.AppToServiceEvent.Event
import pl.jojczak.penmouses.service.penmodes.PointMode
import pl.jojczak.penmouses.service.penmodes.base.PenMode
import pl.jojczak.penmouses.service.penmodes.mouse.MouseMode
import pl.jojczak.penmouses.utils.PreferencesManager
import javax.inject.Inject
import kotlin.reflect.KClass

@AndroidEntryPoint
class MouseService : AccessibilityService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var eventCollectorJob: Job? = null
    private var penMode: PenMode? = null

    @Inject
    lateinit var preferences: PreferencesManager

    @Inject
    lateinit var sPenManager: SPenManager

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
            is Event.UpdateCursorSize -> penMode?.updateSize()
            is Event.UpdateCursorBitmap -> penMode?.updateBitmap()
            is Event.UpdateSensitivity -> penMode?.updateSensitivity()
            is Event.UpdateHideDelay -> penMode?.updateHideDelay()
            is Event.UpdateSPenSleepEnabled -> penMode?.updateSleepEnabled()
            is Event.Stop -> stopMode()
        }
    }

    private fun stopCurrentStartNew(newMode: KClass<out PenMode>?) = serviceScope.launch {
        stopMode()
        if (newMode == null) return@launch

        delay(DELAY_BETWEEN_MODES)
        penMode = getNewMode(newMode)
        penMode?.start()
        AppToServiceEvent.serviceStatus.tryEmit(newMode)
    }

    private fun getNewMode(newMode: KClass<out PenMode>) = when (newMode) {
        MouseMode::class -> MouseMode(
            dispatchGesture = ::dispatchGesture,
            sPenManager = sPenManager,
            context = this,
            preferences = preferences
        )

        PointMode::class -> PointMode(
            dispatchGesture = ::dispatchGesture,
            sPenManager = sPenManager,
            context = this,
            preferences = preferences
        )

        else -> null
    }

    private fun stopMode() {
        val currentModeName = penMode?.let { it::class.simpleName } ?: "null"
        Log.d(TAG, "Stopping current mode ($currentModeName)")
        penMode?.stop()
        penMode = null
        AppToServiceEvent.serviceStatus.tryEmit(null)
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