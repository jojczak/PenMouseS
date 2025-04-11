package pl.jojczak.penmouses.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import androidx.core.content.edit
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
import pl.jojczak.penmouses.di.SharedPreferencesModule.PREF_KEY_SPEN_FEATURES_DISABLED
import pl.jojczak.penmouses.service.AppToServiceEvent
import pl.jojczak.penmouses.service.MouseService
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _state: MutableStateFlow<HomeScreenState> = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    init {
        checkAccessibilityPermission()
        checkSPenFeaturesState()

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
                checkSPenFeaturesState()
            }

            else -> {}
        }
    }

    fun sendSignalToService(event: AppToServiceEvent.Event) {
        AppToServiceEvent.event.tryEmit(event)
    }


    fun changeSPenFeaturesState(state: Boolean) {
        _state.update {
            it.copy(
                areSPenFeaturesDisabled = state
            )
        }

        sharedPreferences.edit {
            putBoolean(PREF_KEY_SPEN_FEATURES_DISABLED, state)
            apply()
        }
    }

    fun changeDialogState(step: Int, state: Boolean) {
        _state.update {
            when (step) {
                1 -> it.copy(showStep1Dialog = state)
                2 -> it.copy(showStep2Dialog = state)
                else -> it
            }
        }
    }

    private fun checkSPenFeaturesState() {
        _state.update {
            it.copy(
                areSPenFeaturesDisabled = sharedPreferences.getBoolean(
                    PREF_KEY_SPEN_FEATURES_DISABLED,
                    false
                )
            )
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
}