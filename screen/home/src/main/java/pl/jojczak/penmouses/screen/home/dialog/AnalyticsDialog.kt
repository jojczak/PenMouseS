package pl.jojczak.penmouses.screen.home.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import pl.jojczak.penmouses.screen.home.R
import pl.jojczak.penmouses.core.ui.R as coreR

@Composable
internal fun AnalyticsDialog(
    onConfirm: () -> Unit,
    onDecline: () -> Unit,
) = AlertDialog(
    icon = {
        Icon(
            painter = painterResource(coreR.drawable.ic_analytics),
            contentDescription = stringResource(R.string.dialog_analytics_icon)
        )
    },
    title = {
        Text(text = stringResource(R.string.dialog_analytics_title))
    },
    text = {
        Text(
            text = stringResource(R.string.dialog_analytics),
            textAlign = TextAlign.Justify
        )
    },
    confirmButton = {
        Button(
            onClick = onConfirm
        ) {
            Text(text = stringResource(R.string.dialog_analytics_confirm))
        }
    },
    dismissButton = {
        TextButton(
            onClick = onDecline
        ) {
            Text(text = stringResource(R.string.dialog_analytics_decline))
        }
    },
    properties = DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false,
    ),
    onDismissRequest = {}
)