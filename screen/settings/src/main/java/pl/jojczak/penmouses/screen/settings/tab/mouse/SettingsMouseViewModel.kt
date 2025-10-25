package pl.jojczak.penmouses.screen.settings.tab.mouse

import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent
import pl.jojczak.penmouses.core.common.utils.CursorType
import pl.jojczak.penmouses.core.common.utils.PrefKeys
import pl.jojczak.penmouses.core.common.utils.PreferencesManager
import pl.jojczak.penmouses.screen.settings.mvi.SettingsCursorViewAction
import pl.jojczak.penmouses.screen.settings.mvi.SettingsTabCursorViewModel
import pl.jojczak.penmouses.screen.settings.mvi.SettingsViewAction
import javax.inject.Inject

@HiltViewModel
internal class SettingsMouseViewModel @Inject constructor(
    preferencesManager: PreferencesManager,
    @ApplicationContext context: Context,
): SettingsTabCursorViewModel<SettingsMouseState>(
    preferencesManager = preferencesManager,
    startState = SettingsMouseState(),
    penMode = AppToServiceEvent.PenMode.Mouse,
    context = context
) {
    override fun refreshSettingsData() = updateState {
        SettingsMouseState(
            cursorType = preferencesManager.get(PrefKeys.MOUSE_CURSOR_TYPE),
            sPenSensitivity = preferencesManager.get(PrefKeys.MOUSE_SENSITIVITY),
            cursorHideDelay = preferencesManager.get(PrefKeys.MOUSE_CURSOR_HIDE_DELAY),
            sPenSleepEnabled = preferencesManager.get(PrefKeys.MOUSE_SLEEP_ENABLED),
            cursorSize = preferencesManager.get(PrefKeys.MOUSE_CURSOR_SIZE),
            cursorAlpha = preferencesManager.get(PrefKeys.MOUSE_CURSOR_ALPHA)
        )
    }

    override fun onViewAction(action: SettingsViewAction) = when (action) {
        is SettingsViewAction.RefreshData -> refreshSettingsData()
        is SettingsViewAction.SavePreference<*> -> savePreference(action)
        is SettingsViewAction.UpdatePreference<*> -> updatePreference(action)
        is SettingsCursorViewAction.CursorTypeChanged -> onCursorTypeChange(action.cursorType)
        is SettingsCursorViewAction.CustomCursorFileSelected -> loadCustomCursorImage(action.uri)
        is SettingsMouseViewAction.SPenSleepEnabled -> onSPenSleepEnabledChange(action.enabled)
        else -> {}
    }

    override fun updatePreference(action: SettingsViewAction.UpdatePreference<*>) = when(action.key) {
        PrefKeys.MOUSE_SENSITIVITY -> {
            updateState { it.copy(sPenSensitivity = action.value as Float) }
        }

        PrefKeys.MOUSE_CURSOR_HIDE_DELAY -> {
            updateState { it.copy(cursorHideDelay = action.value as Float) }
        }

        PrefKeys.MOUSE_CURSOR_SIZE -> {
            updateState { it.copy(cursorSize = action.value as Float) }
        }

        PrefKeys.MOUSE_CURSOR_ALPHA -> {
            updateState { it.copy(cursorAlpha = action.value as Float) }
        }

        else -> {}
    }

    override fun onCursorTypeChange(cursorType: CursorType) {
        updateState { it.copy(cursorType = cursorType) }
        super.onCursorTypeChange(cursorType)
    }

    private fun onSPenSleepEnabledChange(enabled: Boolean) {
        updateState { it.copy(sPenSleepEnabled = enabled) }
        preferencesManager.put(PrefKeys.MOUSE_SLEEP_ENABLED, enabled)
        tryToPingService()
    }
}