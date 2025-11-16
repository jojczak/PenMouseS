package pl.jojczak.penmouses.screen.settings.mvi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent.ModeStatus
import pl.jojczak.penmouses.core.common.utils.PreferencesManager

internal abstract class SettingsTabViewModel<StateType: SettingsTabState>(
    protected val preferencesManager: PreferencesManager,
    startState: StateType
): ViewModel() {
    private val _state: MutableStateFlow<StateType> = MutableStateFlow(startState)
    val state: StateFlow<StateType> = _state.asStateFlow()

    init {
        refreshSettingsData()
    }

    abstract fun onViewAction(action: SettingsViewAction)
    protected abstract fun refreshSettingsData()
    protected open fun updatePreference(action: SettingsViewAction.UpdatePreference<*>) = Unit

    protected fun <Key> savePreference(action: SettingsViewAction.SavePreference<Key>) {
        preferencesManager.put(action.key, action.value)
        tryToPingService()
    }

    protected fun updateState(updater: (StateType) -> StateType) {
        _state.value = updater(_state.value)
    }

    protected fun tryToPingService() {
        if (AppToServiceEvent.serviceStatus.value != ModeStatus.Off) {
            AppToServiceEvent.event.tryEmit(AppToServiceEvent.Event.PreferencesUpdated)
        }
    }
}