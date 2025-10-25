package pl.jojczak.penmouses.screen.settings.tab.scroll

import pl.jojczak.penmouses.screen.settings.mvi.SettingsTabState
import pl.jojczak.penmouses.screen.settings.mvi.SettingsViewAction

internal data class SettingsScrollState(
    val experimentalModeEnabled: Boolean = false
): SettingsTabState()

internal sealed class SettingsScrollViewAction: SettingsViewAction() {
    data class ExperimentalModeUpdate(val enabled: Boolean): SettingsScrollViewAction()
}