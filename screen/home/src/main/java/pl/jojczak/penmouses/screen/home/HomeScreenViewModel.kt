package pl.jojczak.penmouses.screen.home

import android.content.Context
import android.provider.Settings
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent.ModeStatus
import pl.jojczak.penmouses.core.common.spen.SPenManager
import pl.jojczak.penmouses.core.common.utils.PrefKeys
import pl.jojczak.penmouses.core.common.utils.PreferencesManager
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val preferencesManager: PreferencesManager,
) : ViewModel() {

    private val _events = MutableSharedFlow<HomeScreenEvent>()
    val events = _events.asSharedFlow()

    private val _state: MutableStateFlow<HomeScreenState> = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    init {
        checkIfLaunchedFirstTime()
        checkAccessibilityPermission()
        checkSPenSupportAndShowDialogIfNot()
        collectServiceStatus()
    }

    //@formatter:off
    fun onViewAction(viewAction: HomeViewAction) = when (viewAction) {
        is HomeViewAction.LifecycleEvent -> onLifecycleEvent(viewAction.state)
        is HomeViewAction.ToggleUnsupportedDeviceDialog -> _state.update { it.copy(unsupportedDeviceDialogEnabled = viewAction.enabled) }
        is HomeViewAction.ToggleFirstRunDialog -> _state.update { it.copy(firstRunDialogEnabled = viewAction.enabled) }
        is HomeViewAction.SendEventToService -> sendEventToService(viewAction.event)
    }
    //@formatter:on

    private fun onLifecycleEvent(lifecycleState: Lifecycle.State) = when (lifecycleState) {
        Lifecycle.State.RESUMED -> {
            checkAccessibilityPermission()
        }

        else -> {}
    }

    private fun collectServiceStatus() = viewModelScope.launch {
        AppToServiceEvent.serviceStatus.collect {
            _state.update { state ->
                state.copy(serviceStatus = it)
            }
        }
    }

    private fun sendEventToService(event: AppToServiceEvent.Event) {
        if (checkSPenSupportAndShowDialogIfNot()) {
            if (event is AppToServiceEvent.Event.Start && (event.mode != ModeStatus.Off)) {
                checkLaunchCount()
            }
            AppToServiceEvent.event.tryEmit(event)
        }
    }

    private fun checkAccessibilityPermission() {
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        val isAccessibilityEnabled = enabledServices?.contains(MOUSE_SERVICE_NAME) == true

        _state.update { it.copy(isAccessibilityEnabled = isAccessibilityEnabled) }
    }

    private fun checkSPenSupportAndShowDialogIfNot() = SPenManager.isSPenSupported().also {
        if (!it) onViewAction(HomeViewAction.ToggleUnsupportedDeviceDialog(true))
    }

    private fun checkIfLaunchedFirstTime() {
        if (preferencesManager.get(PrefKeys.FIRST_RUN)) {
            _state.update { it.copy(firstRunDialogEnabled = true) }
            preferencesManager.put(PrefKeys.FIRST_RUN, false)
        }
    }

    private fun checkLaunchCount() {
        val launchCount = preferencesManager.get(PrefKeys.LAUNCH_COUNT) + 1
        preferencesManager.put(PrefKeys.LAUNCH_COUNT, launchCount)
        if (launchCount % 5 == 0) viewModelScope.launch {
            _events.emit(HomeScreenEvent.TryToShowReviewDialog)
        }
    }

    companion object {
        private const val MOUSE_SERVICE_NAME = "pl.jojczak.penmouses.service.MouseService"
    }
}