package pl.jojczak.penmouses.ui.home.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import pl.jojczak.penmouses.R
import pl.jojczak.penmouses.ui.common.MoreInfoDialog
import pl.jojczak.penmouses.ui.theme.pad_xl

private const val URL = "file:///android_asset/dialog_contents/UnsupportedDevice.html"

@Composable
fun UnsupportedDeviceDialog(
    showDialog: Boolean,
    changeDialogState: (Int, Boolean) -> Unit = { _, _ -> },
) {
    MoreInfoDialog(
        titleResId = R.string.unsupported_device_title,
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
            textResId = R.string.unsupported_device_button,
            changeDialogState = { changeDialogState(4, false) }
        )
    }
}