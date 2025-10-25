package pl.jojczak.penmouses.screen.home.dialogs

import androidx.compose.runtime.Composable
import pl.jojczak.penmouses.core.ui.R as coreR
import pl.jojczak.penmouses.core.ui.components.MoreInfoDialog
import pl.jojczak.penmouses.screen.home.R

private const val URL = "file:///android_asset/dialog_contents/FirstRun.html"

@Composable
fun FirstRunDialog(
    showDialog: Boolean,
    changeDialogState: (Int, Boolean) -> Unit = { _, _ -> },
) {
    MoreInfoDialog(
        titleResId = coreR.string.app_name,
        showDialog = showDialog,
        dialogId = 6,
        changeDialogState = changeDialogState,
        content = {
            WebView(URL)
        }
    ) {
        DialogDismissButton(
            textResId = R.string.home_steps_dialog_dismiss,
            changeDialogState = { changeDialogState(6, false) }
        )
    }
}