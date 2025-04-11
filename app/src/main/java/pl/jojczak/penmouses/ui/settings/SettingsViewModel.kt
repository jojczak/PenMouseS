package pl.jojczak.penmouses.ui.settings

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.jojczak.penmouses.di.SharedPreferencesModule.PREF_KEY_SPEN_SENSITIVITY
import pl.jojczak.penmouses.service.AppToServiceEvent
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _state: MutableStateFlow<SettingsScreenState> = MutableStateFlow(SettingsScreenState())
    val state: StateFlow<SettingsScreenState> = _state.asStateFlow()

    fun updateSPenSensitivity(value: Float) {
        _state.value = _state.value.copy(sPenSensitivity = value)
    }

    fun saveSPenSensitivity() {
        sharedPreferences.edit {
            putFloat(
                PREF_KEY_SPEN_SENSITIVITY,
                state.value.sPenSensitivity
            )
            apply()
        }
        AppToServiceEvent.event.tryEmit(AppToServiceEvent.Event.UpdateSensitivity(state.value.sPenSensitivity))
    }
}