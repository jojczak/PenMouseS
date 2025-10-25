package pl.jojczak.penmouses.screen.settings.tab.point

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
internal class SettingsPointViewModel @Inject constructor(
    preferencesManager: PreferencesManager,
    @ApplicationContext context: Context,
): SettingsTabCursorViewModel<SettingsPointState>(
    preferencesManager = preferencesManager,
    startState = SettingsPointState(),
    penMode = AppToServiceEvent.PenMode.Point,
    context = context
) {

    override fun refreshSettingsData() = updateState {
        SettingsPointState(
            cursorAlpha = preferencesManager.get(PrefKeys.POINT_CURSOR_ALPHA),
            cursorType = preferencesManager.get(PrefKeys.POINT_CURSOR_TYPE),
            cursorSize = preferencesManager.get(PrefKeys.POINT_CURSOR_SIZE),
        )
    }

    override fun onViewAction(action: SettingsViewAction) = when (action) {
        is SettingsViewAction.RefreshData -> refreshSettingsData()
        is SettingsViewAction.SavePreference<*> -> savePreference(action)
        is SettingsViewAction.UpdatePreference<*> -> updatePreference(action)
        is SettingsCursorViewAction.CursorTypeChanged -> onCursorTypeChange(action.cursorType)
        is SettingsCursorViewAction.CustomCursorFileSelected -> loadCustomCursorImage(action.uri)
        else -> {}
    }

    override fun updatePreference(action: SettingsViewAction.UpdatePreference<*>) = when(action.key) {
        PrefKeys.POINT_CURSOR_ALPHA -> {
            updateState { it.copy(cursorAlpha = action.value as Float) }
        }

        PrefKeys.POINT_CURSOR_SIZE -> {
            updateState { it.copy(cursorSize = action.value as Float) }
        }

        else -> {}
    }

    override fun onCursorTypeChange(cursorType: CursorType) {
        updateState { it.copy(cursorType = cursorType) }
        super.onCursorTypeChange(cursorType)
    }
}