package pl.jojczak.penmouses.screen.settings

import pl.jojczak.penmouses.core.common.utils.CursorType

data class SettingsScreenState(
    val sPenSensitivity: Float = 0.0f,
    val cursorHideDelay: Float = 0f,
    val sPenSleepEnabled: Boolean = false,
    val cursorSize: Float = 0f,
    val cursorType: CursorType = CursorType.LIGHT,
    val showSettingsResetDialog: Boolean = false
)