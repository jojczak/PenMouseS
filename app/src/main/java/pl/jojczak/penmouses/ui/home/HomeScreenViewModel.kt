package pl.jojczak.penmouses.ui.home

import android.content.Context
import android.provider.Settings
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pl.jojczak.penmouses.service.AppToServiceEvent
import pl.jojczak.penmouses.service.MouseService
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state: MutableStateFlow<HomeScreenState> = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    fun sendStartSignalToService(toggle: Boolean) {
        AppToServiceEvent.event.tryEmit(toggle)
    }

    fun onLifecycleEvent(lifecycleState: Lifecycle.State) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> checkAccessibilityPermission()
            else -> {}
        }
    }

    fun changeSPenFeaturesState(state: Boolean) {
        _state.update {
            it.copy(
                areSPenFeaturesDisabled = state
            )
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