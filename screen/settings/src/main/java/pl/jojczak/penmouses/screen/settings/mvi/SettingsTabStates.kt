package pl.jojczak.penmouses.screen.settings.mvi

import pl.jojczak.penmouses.core.common.utils.PrefKey

internal abstract class SettingsTabState

internal open class SettingsViewAction {
    data object RefreshData : SettingsViewAction()

    data class UpdatePreference<Key>(
        val key: PrefKey<Key>,
        val value: Key
    ) : SettingsViewAction()

    data class SavePreference<Key>(
        val key: PrefKey<Key>,
        val value: Key
    ) : SettingsViewAction()
}
