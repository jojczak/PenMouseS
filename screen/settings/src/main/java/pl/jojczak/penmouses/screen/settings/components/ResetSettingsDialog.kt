package pl.jojczak.penmouses.screen.settings.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import pl.jojczak.penmouses.screen.settings.R

@Composable
internal fun ResetSettingsDialog(
    toggleSettingsResetDialog: (Boolean) -> Unit = {},
    resetSettings: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = {
            toggleSettingsResetDialog(false)
        },
        title = { Text(stringResource(R.string.settings_reset_dialog_title)) },
        text = { Text(stringResource(R.string.settings_reset_dialog_desc)) },
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_reset_settings),
                contentDescription = stringResource(R.string.settings_reset_to_defaults)
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    resetSettings()
                    toggleSettingsResetDialog(false)
                }
            ) {
                Text(stringResource(R.string.settings_reset_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    toggleSettingsResetDialog(false)
                }
            ) {
                Text(stringResource(R.string.settings_reset_cancel))
            }
        }
    )
}