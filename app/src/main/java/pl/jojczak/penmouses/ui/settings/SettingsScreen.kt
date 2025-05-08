package pl.jojczak.penmouses.ui.settings

import android.annotation.SuppressLint
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import pl.jojczak.penmouses.R
import pl.jojczak.penmouses.ui.common.ScrollComponent
import pl.jojczak.penmouses.ui.settings.components.AppVersionComponent
import pl.jojczak.penmouses.ui.settings.components.BirdHuntBanner
import pl.jojczak.penmouses.ui.settings.components.CursorIconComponent
import pl.jojczak.penmouses.ui.settings.components.DonateComponent
import pl.jojczak.penmouses.ui.settings.components.NotificationsComponent
import pl.jojczak.penmouses.ui.theme.PenMouseSTheme
import pl.jojczak.penmouses.ui.theme.elevation_1
import pl.jojczak.penmouses.ui.theme.pad_m
import pl.jojczak.penmouses.ui.theme.pad_s
import pl.jojczak.penmouses.ui.theme.pad_xl
import pl.jojczak.penmouses.ui.theme.radius_m
import pl.jojczak.penmouses.utils.CursorType
import pl.jojczak.penmouses.utils.PrefKey
import pl.jojczak.penmouses.utils.PrefKeys
import kotlin.math.round

@Composable
fun SettingsScreen(
    paddingValues: PaddingValues = PaddingValues(),
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    SettingsScreenContent(
        state = state,
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
    paddingValues: PaddingValues = PaddingValues(),
    onValueChange: (PrefKey<Float>, Float) -> Unit = { _, _ -> },
    onValueChangeFinished: (PrefKey<Float>, Float) -> Unit = { _, _ -> },
    onSPenSleepEnabledChange: (Boolean) -> Unit = {},
    onCursorTypeChange: (CursorType) -> Unit = {},
    onCustomCursorFileSelected: (Uri) -> Unit = {},
    toggleSettingsResetDialog: (Boolean) -> Unit = {},
    resetSettings: () -> Unit = {}
) {
    Column {
        TopAppBar(
            title = { Text(text = stringResource(R.string.screen_settings)) },
            colors = TopAppBarDefaults.topAppBarColors().copy(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(elevation_1)
            ),
            actions = {
                IconButton(
                    onClick = {
                        toggleSettingsResetDialog(true)
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.reset_settings_24px),
                        contentDescription = stringResource(R.string.settings_reset_to_defaults)
                    )
                }
            },
        )
        ScrollComponent(
            showDivider = false,
            paddingValues = paddingValues,
            shadowColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(paddingValues)
        ) {
            Column {
                SettingsSlider(
                    text = R.string.settings_s_pen_sensitivity_slider_label,
                    value = state.sPenSensitivity,
                    prefKey = PrefKeys.SPEN_SENSITIVITY,
                    onValueChange = onValueChange,
                    onValueChangeFinished = onValueChangeFinished
                )
                HorizontalDivider()
                SettingsSlider(
                    text = R.string.settings_cursor_hide_delay,
                    textOnLastValue = R.string.settings_cursor_hide_delay_indefinite,
                    value = state.cursorHideDelay,
                    prefKey = PrefKeys.CURSOR_HIDE_DELAY,
                    onValueChange = onValueChange,
                    onValueChangeFinished = onValueChangeFinished
                )
                HorizontalDivider()
                SPenSleepCheckBox(
                    sPenSleepEnabled = state.sPenSleepEnabled,
                    onSPenSleepEnabledChange = onSPenSleepEnabledChange
                )
                HorizontalDivider()
                SettingsSlider(
                    text = R.string.settings_cursor_size_slider_label,
                    value = state.cursorSize,
                    prefKey = PrefKeys.CURSOR_SIZE,
                    onValueChange = onValueChange,
                    onValueChangeFinished = onValueChangeFinished
                )
                HorizontalDivider()
                CursorIconComponent(
                    cursorType = state.cursorType,
                    onCursorTypeChange = onCursorTypeChange,
                    onCustomCursorFileSelected = onCustomCursorFileSelected
                )
                HorizontalDivider()
                NotificationsComponent()
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                HorizontalDivider()
                DonateComponent()
                HorizontalDivider()
                BirdHuntBanner()
                HorizontalDivider()
                AppVersionComponent()
            }
        }
    }

    if (state.showSettingsResetDialog) {
        ResetSettingsDialog(
            toggleSettingsResetDialog = toggleSettingsResetDialog,
            resetSettings = resetSettings
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
private fun SettingsSlider(
    @StringRes text: Int,
    @StringRes textOnLastValue: Int? = null,
    value: Float,
    prefKey: PrefKey<Float>,
    onValueChange: (PrefKey<Float>, Float) -> Unit = { _, _ -> },
    onValueChangeFinished: (PrefKey<Float>, Float) -> Unit = { _, _ -> },
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(pad_s),
        modifier = Modifier.padding(pad_xl)
    ) {
        Text(
            stringResource(
                textOnLastValue.takeIf { it != null && value == prefKey.range.endInclusive }
                    ?: text,
                round(value).toInt()
            )
        )
        var sliderValue by remember(value) { mutableFloatStateOf(value) }
        Slider(
            value = sliderValue,
            onValueChange = {
                val roundedValue = (round(it / prefKey.step)) * prefKey.step
                sliderValue = roundedValue
                onValueChange(prefKey, roundedValue)
            },
            valueRange = prefKey.range,
            onValueChangeFinished = { onValueChangeFinished(prefKey, sliderValue) },
        )
    }
}

@Composable
private fun SPenSleepCheckBox(
    sPenSleepEnabled: Boolean,
    onSPenSleepEnabledChange: (Boolean) -> Unit
) {
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

@Composable
private fun ResetSettingsDialog(
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
                painter = painterResource(R.drawable.reset_settings_24px),
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
            )
        }
    }
}