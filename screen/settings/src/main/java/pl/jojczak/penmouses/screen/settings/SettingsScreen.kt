package pl.jojczak.penmouses.screen.settings

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults.TopAppBarExpandedHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import pl.jojczak.penmouses.core.common.utils.CursorType
import pl.jojczak.penmouses.core.common.utils.PrefKey
import pl.jojczak.penmouses.core.common.utils.PrefKeys
import pl.jojczak.penmouses.core.ui.theme.PenMouseSTheme
import pl.jojczak.penmouses.core.ui.theme.hazeUltraThinSurface
import pl.jojczak.penmouses.screen.settings.components.SettingsTopAppBar
import pl.jojczak.penmouses.screen.settings.components.appVersionComponent
import pl.jojczak.penmouses.screen.settings.components.birdHuntBanner
import pl.jojczak.penmouses.screen.settings.components.cursorIconComponent
import pl.jojczak.penmouses.screen.settings.components.donateComponent
import pl.jojczak.penmouses.screen.settings.components.notificationsComponent
import pl.jojczak.penmouses.screen.settings.components.sPenSleepCheckBox
import pl.jojczak.penmouses.screen.settings.components.settingsSlider
import pl.jojczak.penmouses.core.ui.R as coreR

@Composable
fun SettingsScreen(
    paddingValues: PaddingValues = PaddingValues(),
    hazeState: HazeState,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreenContent(
        state = state,
        hazeState = hazeState,
        paddingValues = paddingValues,
        onValueChange = viewModel::updatePreference,
        onValueChangeFinished = viewModel::savePreference,
        onSPenSleepEnabledChange = viewModel::onSPenSleepEnabledChange,
        onCursorTypeChange = viewModel::onCursorTypeChange,
        onCustomCursorFileSelected = viewModel::loadCustomCursorImage,
        toggleSettingsResetDialog = viewModel::toggleSettingsResetDialog,
        resetSettings = viewModel::resetSettings
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    state: SettingsScreenState,
    hazeState: HazeState,
    paddingValues: PaddingValues = PaddingValues(),
    onValueChange: (PrefKey<Float>, Float) -> Unit = { _, _ -> },
    onValueChangeFinished: (PrefKey<Float>, Float) -> Unit = { _, _ -> },
    onSPenSleepEnabledChange: (Boolean) -> Unit = {},
    onCursorTypeChange: (CursorType) -> Unit = {},
    onCustomCursorFileSelected: (Uri) -> Unit = {},
    toggleSettingsResetDialog: (Boolean) -> Unit = {},
    resetSettings: () -> Unit = {}
) {
    val localDensity = LocalDensity.current
    var topAppBarHeight by remember { mutableStateOf(TopAppBarExpandedHeight) }

    Box {
        LazyColumn(
            contentPadding = PaddingValues(
                start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                top = topAppBarHeight,
                end = paddingValues.calculateEndPadding(LocalLayoutDirection.current),
                bottom = paddingValues.calculateBottomPadding()
            ),
            modifier = Modifier.hazeSource(state = hazeState)
        ) {
            settingsSlider(
                text = coreR.string.settings_s_pen_sensitivity_slider_label,
                value = state.sPenSensitivity,
                prefKey = PrefKeys.SPEN_SENSITIVITY,
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished
            )

            horizontalDivider()

            settingsSlider(
                text = coreR.string.settings_cursor_hide_delay,
                textOnLastValue = coreR.string.settings_cursor_hide_delay_indefinite,
                value = state.cursorHideDelay,
                prefKey = PrefKeys.CURSOR_HIDE_DELAY,
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished
            )

            horizontalDivider()

            sPenSleepCheckBox(
                sPenSleepEnabled = state.sPenSleepEnabled,
                onSPenSleepEnabledChange = onSPenSleepEnabledChange
            )

            horizontalDivider()

            settingsSlider(
                text = coreR.string.settings_cursor_size_slider_label,
                value = state.cursorSize,
                prefKey = PrefKeys.CURSOR_SIZE,
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished
            )

            horizontalDivider()

            cursorIconComponent(
                cursorType = state.cursorType,
                onCursorTypeChange = onCursorTypeChange,
                onCustomCursorFileSelected = onCustomCursorFileSelected
            )

            horizontalDivider()
            notificationsComponent()

            horizontalDivider()
            donateComponent()

            horizontalDivider()
            birdHuntBanner()

            horizontalDivider()
            appVersionComponent()
        }
        SettingsTopAppBar(
            toggleSettingsResetDialog = toggleSettingsResetDialog,
            modifier = Modifier
                .hazeEffect(
                    state = hazeState,
                    style = hazeUltraThinSurface()
                )
                .onGloballyPositioned {
                    with(localDensity) { topAppBarHeight = it.size.height.toDp() }
                },
        )
    }

    if (state.showSettingsResetDialog) {
        ResetSettingsDialog(
            toggleSettingsResetDialog = toggleSettingsResetDialog,
            resetSettings = resetSettings
        )
    }
}

private fun LazyListScope.horizontalDivider() = item {
    HorizontalDivider()
}

@Composable
private fun ResetSettingsDialog(
    toggleSettingsResetDialog: (Boolean) -> Unit = {},
    resetSettings: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = {
            toggleSettingsResetDialog(false)
        },
        title = { Text(stringResource(coreR.string.settings_reset_dialog_title)) },
        text = { Text(stringResource(coreR.string.settings_reset_dialog_desc)) },
        icon = {
            Icon(
                painter = painterResource(coreR.drawable.reset_settings_24px),
                contentDescription = stringResource(coreR.string.settings_reset_to_defaults)
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    resetSettings()
                    toggleSettingsResetDialog(false)
                }
            ) {
                Text(stringResource(coreR.string.settings_reset_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    toggleSettingsResetDialog(false)
                }
            ) {
                Text(stringResource(coreR.string.settings_reset_cancel))
            }
        }
    )
}

@Suppress("unused")
private const val TAG = "SettingsScreen"

@Preview(device = "spec:width=1080px,height=3000px,dpi=440")
@Composable
private fun SettingsScreenPreview() {
    PenMouseSTheme {
        Surface {
            SettingsScreenContent(
                state = SettingsScreenState(
                    cursorType = CursorType.LIGHT
                ),
                hazeState = rememberHazeState()
            )
        }
    }
}