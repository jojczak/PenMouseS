package pl.jojczak.penmouses.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import pl.jojczak.penmouses.ui.theme.pad_m
import pl.jojczak.penmouses.ui.theme.pad_xl

val DIALOG_ELEVATION = 3.dp

@Composable
fun MoreInfoDialog(
    @StringRes titleResId: Int,
    showDialog: Boolean,
    dialogId: Int,
    changeDialogState: (Int, Boolean) -> Unit = { _, _ -> },
    content: @Composable () -> Unit = {},
    buttons: @Composable () -> Unit = {}
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = { changeDialogState(dialogId, false) },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            )
        ) {
            Surface(
                tonalElevation = DIALOG_ELEVATION,
                shape = RoundedCornerShape(pad_xl),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(pad_xl)
            ) {
                Column {
                    Text(
                        text = stringResource(titleResId),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(pad_xl)
                    )
                    DialogHorizontalLine()
                    Box(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .padding(horizontal = pad_xl)
                    ) {
                        ScrollComponent {
                            content()
                        }
                    }
                    DialogHorizontalLine()
                    Box(
                        modifier = Modifier
                            .padding(horizontal = pad_xl, vertical = pad_m)
                    ) {
                        buttons()
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogHorizontalLine() {
    Spacer(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .height(1.dp)
            .fillMaxWidth()
    )
}