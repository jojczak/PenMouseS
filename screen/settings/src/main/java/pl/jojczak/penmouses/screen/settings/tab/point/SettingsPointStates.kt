package pl.jojczak.penmouses.screen.settings.tab.point

import pl.jojczak.penmouses.core.common.utils.CursorType
import pl.jojczak.penmouses.screen.settings.mvi.SettingsTabCursorState

internal data class SettingsPointState(
    override val cursorType: CursorType = CursorType.Light,
    val cursorAlpha: Float = 1f,
    val cursorSize: Float = 0f
) : SettingsTabCursorState()
