package pl.jojczak.penmouses.core.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import pl.jojczak.penmouses.core.ui.R

@Composable
fun AccessibilitySettingsDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) = MarkdownDialog(
    iconId = R.drawable.ic_mobile_alert,
    titleId = R.string.accessibility_dialog_title,
    markdownTextId = R.raw.accessibility_dialog_content,
    dismissButton = {
        TextButton(
            onClick = onDismiss
        ) {
            Text(text = stringResource(R.string.accessibility_dialog_cancel))
        }
    },
    confirmButton = {
        Button(
            onClick = onConfirm
        ) {
            Text(text = stringResource(R.string.accessibility_dialog_accept))
        }
    },
    onDismissRequest = onDismiss
)