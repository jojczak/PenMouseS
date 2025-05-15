package pl.jojczak.penmouses.ui.home

import android.content.Context
import android.provider.Settings
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.jojczak.penmouses.service.AppToServiceEvent
import pl.jojczak.penmouses.service.MouseService
import pl.jojczak.penmouses.utils.PrefKey
import pl.jojczak.penmouses.utils.PrefKeys
import pl.jojczak.penmouses.utils.PreferencesManager
import pl.jojczak.penmouses.utils.SPenManager
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesManager: PreferencesManager,
) : ViewModel() {

    private val _state: MutableStateFlow<HomeScreenState> = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    init {
        checkAccessibilityPermission()
        checkIfSPenFeaturesSupported()

        preferencesManager.get(PrefKeys.FIRST_RUN).takeIf { it }?.let {
            _state.update { it.copy(showFirstRunDialog = true) }
            preferencesManager.put(PrefKeys.FIRST_RUN, false)
        }

        viewModelScope.launch {
            AppToServiceEvent.serviceStatus.collect {
                _state.update { state ->
                    state.copy(serviceStatus = it)
                }
            }
        }
    }

    fun onLifecycleEvent(lifecycleState: Lifecycle.State) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                checkAccessibilityPermission()
            }

            else -> {}
        }
    }

    fun sendSignalToService(event: AppToServiceEvent.Event) {
        if (SPenManager.isSPenSupported()) {
            AppToServiceEvent.event.tryEmit(event)
        } else {
            changeDialogState(4, true)
        }
    }

    fun changeDialogState(step: Int, state: Boolean) {
        _state.update {
            when (step) {
                1 -> it.copy(showStep1Dialog = state)
                2 -> it.copy(showStep2Dialog = state)
                3 -> it.copy(showStep3Dialog = state)
                4 -> it.copy(showUnsupportedSPenDialog = state)
                5 -> it.copy(showTroubleshootingDialog = state)
                6 -> it.copy(showFirstRunDialog = state)
                else -> it
            }
        }
    }

    fun togglePermissionNotification(state: Boolean) {
        _state.update {
            it.copy(showNotificationPermission = state)
        }
    }

    private fun checkAccessibilityPermission() {
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        val isAccessibilityEnabled =
            enabledServices?.contains(MouseService::class.java.name) == true

        _state.update {
            it.copy(
                isAccessibilityEnabled = isAccessibilityEnabled
            )
        }
    }

    private fun checkIfSPenFeaturesSupported() {
        changeDialogState(4, !SPenManager.isSPenSupported())
    }
}