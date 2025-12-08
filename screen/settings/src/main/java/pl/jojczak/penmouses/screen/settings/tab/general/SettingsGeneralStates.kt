package pl.jojczak.penmouses.screen.settings.tab.general

import pl.jojczak.penmouses.screen.settings.mvi.SettingsTabState

internal data class SettingsGeneralState(
    val analyticsEnabled: Boolean = false,
) : SettingsTabState()
