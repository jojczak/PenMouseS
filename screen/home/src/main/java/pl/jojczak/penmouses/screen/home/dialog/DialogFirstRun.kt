package pl.jojczak.penmouses.screen.home.dialog

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import pl.jojczak.penmouses.screen.home.R
import pl.jojczak.penmouses.screen.home.components.MarkdownDialog

@Composable
internal fun DialogFirstRun(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) = MarkdownDialog(
    iconId = R.drawable.ic_sentiment_very_satisfied,
    titleId = R.string.dialog_first_run_title,
    markdownTextId = R.raw.dialog_first_run,
    dismissButton = {
        TextButton(
            onClick = onDismiss
        ) {
            Text(text = stringResource(R.string.dialog_first_dismiss_button))
        }
    },
    confirmButton = {
        TextButton(
            onClick = onConfirm
        ) {
            Text(text = stringResource(R.string.dialog_first_manual_button))
        }
    },
    onDismissRequest = onDismiss
)