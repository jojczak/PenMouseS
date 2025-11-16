package pl.jojczak.penmouses.screen.settings.tab.scroll

import dagger.hilt.android.lifecycle.HiltViewModel
import pl.jojczak.penmouses.core.common.utils.PrefKeys
import pl.jojczak.penmouses.core.common.utils.PreferencesManager
import pl.jojczak.penmouses.screen.settings.mvi.SettingsTabViewModel
import pl.jojczak.penmouses.screen.settings.mvi.SettingsViewAction
import javax.inject.Inject

@HiltViewModel
internal class SettingsScrollViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : SettingsTabViewModel<SettingsScrollState>(
    preferencesManager,
    SettingsScrollState()
) {
    override fun refreshSettingsData() = updateState {
        SettingsScrollState(
            experimentalModeEnabled = preferencesManager.get(PrefKeys.SCROLL_EXPERIMENTAL_MODE)
        )
    }

    override fun onViewAction(action: SettingsViewAction) = when(action) {
        is SettingsViewAction.RefreshData -> refreshSettingsData()
        is SettingsScrollViewAction.ExperimentalModeUpdate -> experimentalModeUpdate(action.enabled)
        else -> Unit
    }

    private fun experimentalModeUpdate(enabled: Boolean) {
        preferencesManager.put(PrefKeys.SCROLL_EXPERIMENTAL_MODE, enabled)
        updateState { it.copy(experimentalModeEnabled = enabled) }
        tryToPingService()
    }
}