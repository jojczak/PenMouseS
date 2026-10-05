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
            experimentalModeEnabled = preferencesManager.get(PrefKeys.SCROLL_EXPERIMENTAL_MODE),
            scrollDistance = preferencesManager.get(PrefKeys.SCROLL_DISTANCE),
            scrollDuration = preferencesManager.get(PrefKeys.SCROLL_DURATION),
            scrollDownOnly = preferencesManager.get(PrefKeys.SCROLL_DOWN_ONLY),
        )
    }

    override fun onViewAction(action: SettingsViewAction) = when(action) {
        is SettingsViewAction.RefreshData -> refreshSettingsData()
        is SettingsViewAction.SavePreference<*> -> savePreference(action)
        is SettingsViewAction.UpdatePreference<*> -> updatePreference(action)
        is SettingsScrollViewAction.ExperimentalModeUpdate -> experimentalModeUpdate(action.enabled)
        is SettingsScrollViewAction.ScrollDownOnlyUpdate -> scrollDownOnlyUpdate(action.enabled)
        else -> Unit
    }

    override fun updatePreference(action: SettingsViewAction.UpdatePreference<*>) = when(action.key) {
        PrefKeys.SCROLL_DISTANCE -> {
            updateState { it.copy(scrollDistance = action.value as Float) }
        }

        PrefKeys.SCROLL_DURATION -> {
            updateState { it.copy(scrollDuration = action.value as Float) }
        }

        else -> {}
    }

    private fun experimentalModeUpdate(enabled: Boolean) {
        preferencesManager.put(PrefKeys.SCROLL_EXPERIMENTAL_MODE, enabled)
        updateState { it.copy(experimentalModeEnabled = enabled) }
        tryToPingService()
    }

    private fun scrollDownOnlyUpdate(enabled: Boolean) {
        preferencesManager.put(PrefKeys.SCROLL_DOWN_ONLY, enabled)
        updateState { it.copy(scrollDownOnly = enabled) }
        tryToPingService()
    }
}