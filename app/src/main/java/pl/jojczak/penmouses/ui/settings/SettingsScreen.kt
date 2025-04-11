package pl.jojczak.penmouses.ui.settings

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pl.jojczak.penmouses.R
import pl.jojczak.penmouses.ui.theme.PenMouseSThemePreview
import pl.jojczak.penmouses.ui.theme.elevation_1
import pl.jojczak.penmouses.ui.theme.pad_s
import pl.jojczak.penmouses.ui.theme.pad_xl
import kotlin.math.round

private const val SLIDER_MIN = 1f
private const val SLIDER_MAX = 100f

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    SettingsScreenContent(
        state = state,
        updateSPenSensitivity = viewModel::updateSPenSensitivity,
        saveSPenSensitivity = viewModel::saveSPenSensitivity
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    state: SettingsScreenState,
    updateSPenSensitivity: (Float) -> Unit = { },
    saveSPenSensitivity: () -> Unit = { }
) {
    Column {
        TopAppBar(
            title = { Text(text = stringResource(R.string.screen_settings)) },
            colors = TopAppBarDefaults.topAppBarColors().copy(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(elevation_1)
            )
        )
        SPenSensitivitySlider(
            sPenSensitivity = state.sPenSensitivity,
            updateSPenSensitivity = updateSPenSensitivity,
            saveSPenSensitivity = saveSPenSensitivity
        )
        Spacer(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .height(1.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun SPenSensitivitySlider(
    sPenSensitivity: Float,
    updateSPenSensitivity: (Float) -> Unit = { },
    saveSPenSensitivity: () -> Unit = { }
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(pad_s),
        modifier = Modifier.padding(pad_xl)
    ) {
        Text(
            stringResource(
                R.string.settings_s_pen_sensitivity_slider_label,
                round(sPenSensitivity).toInt()
            )
        )
        Slider(
            value = sPenSensitivity,
            onValueChange = { updateSPenSensitivity(it) },
            valueRange = SLIDER_MIN..SLIDER_MAX,
            onValueChangeFinished = { saveSPenSensitivity() }
        )
    }
}

private const val TAG = "SettingsScreen"

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun SettingsScreenPreview() {
    PenMouseSThemePreview {
        SettingsScreenContent(
            state = SettingsScreenState()
        )
    }
}