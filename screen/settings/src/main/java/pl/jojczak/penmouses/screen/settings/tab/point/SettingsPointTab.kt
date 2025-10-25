package pl.jojczak.penmouses.screen.settings.tab.point

import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent
import pl.jojczak.penmouses.core.common.utils.CursorType
import pl.jojczak.penmouses.core.common.utils.PrefKey
import pl.jojczak.penmouses.core.common.utils.PrefKeys
import pl.jojczak.penmouses.core.ui.theme.PenMouseSDevicePreview
import pl.jojczak.penmouses.screen.settings.R
import pl.jojczak.penmouses.screen.settings.components.horizontalDivider
import pl.jojczak.penmouses.screen.settings.components.cursorIconComponent
import pl.jojczak.penmouses.screen.settings.components.settingsSlider
import pl.jojczak.penmouses.screen.settings.mvi.SettingsCursorViewAction
import pl.jojczak.penmouses.screen.settings.mvi.SettingsViewAction

@Composable
internal fun SettingsPointTab(
    contentPadding: PaddingValues,
    refreshDataTrigger: Boolean,
    viewModel: SettingsPointViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(refreshDataTrigger) {
        if (!refreshDataTrigger) viewModel.onViewAction(SettingsViewAction.RefreshData)
    }

    // @formatter:off
    SettingsPointTabContent(
        viewState = viewState,
        contentPadding = contentPadding,
        onPrefChanging = { key, value -> viewModel.onViewAction(SettingsViewAction.UpdatePreference(key, value)) },
        onPrefChanged = { key, value -> viewModel.onViewAction(SettingsViewAction.SavePreference(key, value)) },
        onCursorTypeChange = { viewModel.onViewAction(SettingsCursorViewAction.CursorTypeChanged(it)) },
        onCustomCursorFileSelected = { viewModel.onViewAction(SettingsCursorViewAction.CustomCursorFileSelected(it)) }
    )
    // @formatter:on
}

@Composable
private fun SettingsPointTabContent(
    viewState: SettingsPointState,
    contentPadding: PaddingValues,
    onPrefChanging: (PrefKey<Float>, Float) -> Unit = { _, _ -> },
    onPrefChanged: (PrefKey<Float>, Float) -> Unit = { _, _ -> },
    onCursorTypeChange: (CursorType) -> Unit = {},
    onCustomCursorFileSelected: (Uri) -> Unit = {}
) = LazyColumn(
    contentPadding = contentPadding,
    modifier = Modifier.fillMaxSize()
) {
    cursorIconComponent(
        cursorType = viewState.cursorType,
        penMode = AppToServiceEvent.PenMode.Point,
        onCursorTypeChange = onCursorTypeChange,
        onCustomCursorFileSelected = onCustomCursorFileSelected
    )

    horizontalDivider()

    settingsSlider(
        text = R.string.settings_cursor_alpha,
        value = viewState.cursorAlpha,
        valueDisplay = { (it * 100).toInt().toString() },
        prefKey = PrefKeys.POINT_CURSOR_ALPHA,
        onPrefChanging = onPrefChanging,
        onPrefChanged = onPrefChanged
    )

    horizontalDivider()

    settingsSlider(
        text = R.string.settings_cursor_size_slider_label,
        value = viewState.cursorSize,
        prefKey = PrefKeys.POINT_CURSOR_SIZE,
        onPrefChanging = onPrefChanging,
        onPrefChanged = onPrefChanged
    )

    horizontalDivider()
}

@Preview
@Composable
private fun SettingsMouseTabPreview() {
    PenMouseSDevicePreview {
        SettingsPointTabContent(
            viewState = SettingsPointState(),
            contentPadding = it
        )
    }
}
