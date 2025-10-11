package pl.jojczak.penmouses.screen.home.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import pl.jojczak.penmouses.core.ui.R as coreR
import pl.jojczak.penmouses.core.ui.components.MoreInfoDialog
import pl.jojczak.penmouses.core.ui.theme.pad_xl

private const val URL = "file:///android_asset/dialog_contents/UnsupportedDevice.html"

@Composable
fun UnsupportedDeviceDialog(
    showDialog: Boolean,
    changeDialogState: (Int, Boolean) -> Unit = { _, _ -> },
) {
    MoreInfoDialog(
        titleResId = coreR.string.unsupported_device_title,
        showDialog = showDialog,
        dialogId = 4,
        changeDialogState = changeDialogState,
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(pad_xl)
            ) {
                WebView(URL)
            }
        }
    ) {
        DialogDismissButton(
            textResId = coreR.string.unsupported_device_button,
            changeDialogState = { changeDialogState(4, false) }
        )
    }
}