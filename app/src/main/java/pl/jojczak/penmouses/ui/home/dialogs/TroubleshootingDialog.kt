package pl.jojczak.penmouses.ui.home.dialogs

import androidx.compose.runtime.Composable
import pl.jojczak.penmouses.R
import pl.jojczak.penmouses.ui.common.MoreInfoDialog

private const val URL = "file:///android_asset/dialog_contents/Troubleshooting.html"

@Composable
fun TroubleshootingDialog(
    showDialog: Boolean,
    changeDialogState: (Int, Boolean) -> Unit = { _, _ -> },
) {
    MoreInfoDialog(
        titleResId = R.string.home_troubleshooting_button,
        showDialog = showDialog,
        dialogId = 5,
        changeDialogState = changeDialogState,
        content = {
            WebView(URL)
        }
    ) {
        DialogDismissButton(
            textResId = R.string.home_steps_dialog_dismiss,
            changeDialogState = { changeDialogState(5, false) }
        )
    }
}