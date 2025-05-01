package pl.jojczak.penmouses.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import pl.jojczak.penmouses.R
import pl.jojczak.penmouses.ui.common.MoreInfoDialog
import pl.jojczak.penmouses.ui.theme.LINK_ICON_SIZE
import pl.jojczak.penmouses.ui.theme.PenMouseSTheme

@Composable
fun Step1Dialog(
    showDialog: Boolean,
    changeDialogState: (Int, Boolean) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current

    MoreInfoDialog(
        titleResId = R.string.home_steps_1_des,
        showDialog = showDialog,
        dialogId = 1,
        changeDialogState = changeDialogState,
        content = {
            Text(
                text = stringResource(R.string.home_steps_1_dialog_content),
                textAlign = TextAlign.Justify
            )
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = { changeDialogState(1, false) }
            ) {
                Text(
                    text = stringResource(R.string.home_steps_dialog_dismiss)
                )
            }
            TextButton(
                onClick = {
                    openSettings(context)
                    changeDialogState(1, false)
                },
            ) {
                Text(
                    text = stringResource(R.string.home_steps_1_settings) + " "
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

@Composable
fun Step2Dialog(
    showDialog: Boolean,
    changeDialogState: (Int, Boolean) -> Unit = { _, _ -> },
) {
    MoreInfoDialog(
        titleResId = R.string.home_steps_2_dialog_title,
        showDialog = showDialog,
        dialogId = 2,
        changeDialogState = changeDialogState,
        content = {
            Text(
                text = stringResource(R.string.home_steps_2_dialog_content),
                textAlign = TextAlign.Justify
            )
        }
    ) {
        TextButton(
            onClick = { changeDialogState(2, false) }
        ) {
            Text(
                text = stringResource(R.string.home_steps_dialog_dismiss)
            )
        }
    }
}

@Composable
fun Step3Dialog(
    showDialog: Boolean,
    changeDialogState: (Int, Boolean) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current

    MoreInfoDialog(
        titleResId = R.string.home_steps_3_des,
        showDialog = showDialog,
        dialogId = 2,
        changeDialogState = changeDialogState,
        content = {
            Text(
                text = stringResource(R.string.home_steps_3_dialog_content),
                textAlign = TextAlign.Justify
            )
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

@Composable
@Preview
private fun Step2DialogPreview() {
    PenMouseSTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Step3Dialog(
                showDialog = true
            )
        }
    }
}

@Composable
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
private fun MoreInfoDialogPreview() {
    PenMouseSTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Step1Dialog(
                showDialog = true
            )
        }
    }
}