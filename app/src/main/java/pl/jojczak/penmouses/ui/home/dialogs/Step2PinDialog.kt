package pl.jojczak.penmouses.ui.home.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import pl.jojczak.penmouses.R
import pl.jojczak.penmouses.ui.common.MoreInfoDialog
import pl.jojczak.penmouses.ui.theme.pad_xl

private const val URL = "file:///android_asset/dialog_contents/Step2PinApp.html"
private const val STEP_2_VIDEO = "asset:///manual_videos/step2.mp4"

@Composable
fun Step2PinDialog(
    showDialog: Boolean,
    changeDialogState: (Int, Boolean) -> Unit = { _, _ -> },
) {
    MoreInfoDialog(
        titleResId = R.string.home_steps_2_dialog_title,
        showDialog = showDialog,
        dialogId = 2,
        changeDialogState = changeDialogState,
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(pad_xl)
            ) {
                WebView(URL)
                VideoPlayer(STEP_2_VIDEO.toUri())
            }
        }
    ) {
        DialogDismissButton(
            textResId = R.string.home_steps_dialog_dismiss,
            changeDialogState = { changeDialogState(2, false) }
        )
    }
}