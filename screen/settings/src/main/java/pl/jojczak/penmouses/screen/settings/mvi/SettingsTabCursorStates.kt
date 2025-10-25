package pl.jojczak.penmouses.screen.settings.mvi

import android.net.Uri
import pl.jojczak.penmouses.core.common.utils.CursorType

internal abstract class SettingsTabCursorState() : SettingsTabState() {
    abstract val cursorType: CursorType
}

internal open class SettingsCursorViewAction : SettingsViewAction() {
    data class CursorTypeChanged(val cursorType: CursorType) : SettingsCursorViewAction()
    data class CustomCursorFileSelected(val uri: Uri) : SettingsCursorViewAction()
}