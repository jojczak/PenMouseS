package pl.jojczak.penmouses.ui.home.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import pl.jojczak.penmouses.R
import pl.jojczak.penmouses.ui.common.MoreInfoDialog
import pl.jojczak.penmouses.ui.home.openAccessibilitySettings
import pl.jojczak.penmouses.ui.theme.LINK_ICON_SIZE
import pl.jojczak.penmouses.ui.theme.pad_xl

private const val URL = "file:///android_asset/dialog_contents/Step3Accessibility.html"
private const val STEP_3_VIDEO = "asset:///manual_videos/step3.mp4"

@Composable
fun Step3AccessibilityServicesDialog(
    showDialog: Boolean,
    changeDialogState: (Int, Boolean) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current

    MoreInfoDialog(
        titleResId = R.string.home_steps_3_des,
        showDialog = showDialog,
        dialogId = 3,
        changeDialogState = changeDialogState,
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(pad_xl)
            ) {
                WebView(URL)
                VideoPlayer(uri = STEP_3_VIDEO.toUri())
            }
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = { changeDialogState(3, false) }
            ) {
                Text(
                    text = stringResource(R.string.home_steps_dialog_dismiss)
                )
            }
            TextButton(
                onClick = {
                    openAccessibilitySettings(context)
                    changeDialogState(3, false)
                },
            ) {
                Text(
                    text = stringResource(R.string.home_steps_3_settings) + " "
                )
                Icon(
                    painter = painterResource(R.drawable.open_in_new_24px),
                    contentDescription = null,
                    modifier = Modifier.size(LINK_ICON_SIZE)
                )
            }
        }
    }
}