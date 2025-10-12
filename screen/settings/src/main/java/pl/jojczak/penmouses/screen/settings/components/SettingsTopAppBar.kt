package pl.jojczak.penmouses.screen.settings.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import pl.jojczak.penmouses.core.ui.theme.PenMouseSTheme
import pl.jojczak.penmouses.core.ui.R as coreR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsTopAppBar(
    modifier: Modifier = Modifier,
    toggleSettingsResetDialog: (Boolean) -> Unit = {}
) {
    TopAppBar(
        title = { Text(text = stringResource(coreR.string.screen_settings)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        actions = {
            IconButton(
                onClick = {
                    toggleSettingsResetDialog(true)
                }
            ) {
                Icon(
                    painter = painterResource(coreR.drawable.reset_settings_24px),
                    contentDescription = stringResource(coreR.string.settings_reset_to_defaults)
                )
            }
        },
        modifier = modifier
    )
}

@Preview
@Composable
private fun PreviewSettingsAppTopBar() {
    PenMouseSTheme {
        Surface {
            SettingsTopAppBar { }
        }
    }
}
