package pl.jojczak.penmouses.screen.settings.tab.mouse

import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent
import pl.jojczak.penmouses.core.common.utils.CursorType
import pl.jojczak.penmouses.core.common.utils.PrefKey
import pl.jojczak.penmouses.core.common.utils.PrefKeys
import pl.jojczak.penmouses.core.ui.theme.PenMouseSDevicePreview
import pl.jojczak.penmouses.core.ui.theme.pad_m
import pl.jojczak.penmouses.core.ui.theme.radius_m
import pl.jojczak.penmouses.screen.settings.R
import pl.jojczak.penmouses.screen.settings.components.cursorIconComponent
import pl.jojczak.penmouses.screen.settings.components.horizontalDivider
import pl.jojczak.penmouses.screen.settings.components.settingsSlider
import pl.jojczak.penmouses.screen.settings.mvi.SettingsCursorViewAction
import pl.jojczak.penmouses.screen.settings.mvi.SettingsViewAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsMouseTab(
    contentPadding: PaddingValues,
    refreshDataTrigger: Boolean,
    viewModel: SettingsMouseViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(refreshDataTrigger) {
        if (!refreshDataTrigger) viewModel.onViewAction(SettingsViewAction.RefreshData)
    }

    // @formatter:off
    SettingsMouseTabContent(
        viewState = viewState,
        contentPadding = contentPadding,
        onPrefChanging = { key, value -> viewModel.onViewAction(SettingsViewAction.UpdatePreference(key, value)) },
        onPrefChanged = { key, value -> viewModel.onViewAction(SettingsViewAction.SavePreference(key, value)) },
        onCursorTypeChange = { viewModel.onViewAction(SettingsCursorViewAction.CursorTypeChanged(it)) },
        onCustomCursorFileSelected = { viewModel.onViewAction(SettingsCursorViewAction.CustomCursorFileSelected(it))},
        onSPenSleepEnabledChange = { viewModel.onViewAction(SettingsMouseViewAction.SPenSleepEnabled(it)) }
    )
    // @formatter:on
}

@Composable
private fun SettingsMouseTabContent(
    viewState: SettingsMouseState,
    contentPadding: PaddingValues,
    onPrefChanging: (PrefKey<Float>, Float) -> Unit = { _, _ -> },
    onPrefChanged: (PrefKey<Float>, Float) -> Unit = { _, _ -> },
    onCursorTypeChange: (CursorType) -> Unit = {},
    onCustomCursorFileSelected: (Uri) -> Unit = {},
    onSPenSleepEnabledChange: (Boolean) -> Unit = {}
) = LazyColumn(
    contentPadding = contentPadding,
    modifier = Modifier.fillMaxSize()
) {
    cursorIconComponent(
        cursorType = viewState.cursorType,
        penMode = AppToServiceEvent.PenMode.Mouse,
        onCursorTypeChange = onCursorTypeChange,
        onCustomCursorFileSelected = onCustomCursorFileSelected
    )

    horizontalDivider()

    settingsSlider(
        text = R.string.settings_cursor_alpha,
        value = viewState.cursorAlpha,
        valueDisplay = { (it * 100).toInt().toString() },
        prefKey = PrefKeys.MOUSE_CURSOR_ALPHA,
        onPrefChanging = onPrefChanging,
        onPrefChanged = onPrefChanged
    )

    horizontalDivider()

    settingsSlider(
        text = R.string.settings_cursor_size_slider_label,
        value = viewState.cursorSize,
        prefKey = PrefKeys.MOUSE_CURSOR_SIZE,
        onPrefChanging = onPrefChanging,
        onPrefChanged = onPrefChanged
    )

    horizontalDivider()

    settingsSlider(
        text = R.string.settings_s_pen_sensitivity_slider_label,
        value = viewState.sPenSensitivity,
        prefKey = PrefKeys.MOUSE_SENSITIVITY,
        onPrefChanging = onPrefChanging,
        onPrefChanged = onPrefChanged
    )

    horizontalDivider()

    settingsSlider(
        text = R.string.settings_cursor_hide_delay,
        textOnLastValue = R.string.settings_cursor_hide_delay_indefinite,
        value = viewState.cursorHideDelay,
        prefKey = PrefKeys.MOUSE_CURSOR_HIDE_DELAY,
        onPrefChanging = onPrefChanging,
        onPrefChanged = onPrefChanged
    )

    horizontalDivider()

    sPenSleepCheckBox(
        sPenSleepEnabled = viewState.sPenSleepEnabled,
        onSPenSleepEnabledChange = onSPenSleepEnabledChange
    )
}

private fun LazyListScope.sPenSleepCheckBox(
    sPenSleepEnabled: Boolean,
    onSPenSleepEnabledChange: (Boolean) -> Unit
) = item {
    Row(
        modifier = Modifier
            .padding(pad_m)
            .clip(RoundedCornerShape(radius_m))
            .clickable {
                onSPenSleepEnabledChange(!sPenSleepEnabled)
            }
    ) {
        Checkbox(
            checked = sPenSleepEnabled,
            onCheckedChange = onSPenSleepEnabledChange
        )
        Column {
            Text(
                text = stringResource(R.string.settings_s_pen_sleep_info)
            )
            Crossfade(sPenSleepEnabled) {
                if (it) {
                    Text(
                        text = stringResource(R.string.settings_s_pen_sleep_enabled_info),
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.bodySmall,
                    )
                } else {
                    Text(
                        text = stringResource(R.string.settings_s_pen_sleep_disabled_warning),
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun SettingsMouseTabPreview() {
    PenMouseSDevicePreview {
        SettingsMouseTabContent(
            viewState = SettingsMouseState(),
            contentPadding = it
        )
    }
}
