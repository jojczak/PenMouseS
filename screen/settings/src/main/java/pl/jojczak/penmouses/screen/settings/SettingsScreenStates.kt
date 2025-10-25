package pl.jojczak.penmouses.screen.settings

import androidx.annotation.StringRes

data class SettingsScreenState(
    val showSettingsResetDialog: Boolean = false
)

sealed class SettingsViewAction {
    data object ResetSettings : SettingsViewAction()
    data class ToggleResetDialog(val show: Boolean) : SettingsViewAction()
}

internal enum class SettingTabs(
    @param:StringRes val tabNameId: Int
) {
    Mouse(R.string.settings_mouse_tab),
    Point(R.string.settings_point_tab),
    Scroll(R.string.settings_scroll_tab),
    General(R.string.settings_general_tab),
}