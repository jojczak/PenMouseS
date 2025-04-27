package pl.jojczak.penmouses.ui.settings

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pl.jojczak.penmouses.R
import pl.jojczak.penmouses.ui.theme.PenMouseSThemePreview
import pl.jojczak.penmouses.ui.theme.elevation_1
import pl.jojczak.penmouses.ui.theme.elevation_2
import pl.jojczak.penmouses.ui.theme.pad_l
import pl.jojczak.penmouses.ui.theme.pad_m
import pl.jojczak.penmouses.ui.theme.pad_s
import pl.jojczak.penmouses.ui.theme.pad_xl
import pl.jojczak.penmouses.ui.theme.pad_xs
import pl.jojczak.penmouses.ui.theme.radius_m
import pl.jojczak.penmouses.utils.CursorType
import pl.jojczak.penmouses.utils.PrefKey
import pl.jojczak.penmouses.utils.PrefKeys
import pl.jojczak.penmouses.utils.getCursorBitmap
import pl.jojczak.penmouses.utils.openUrl
import kotlin.math.round

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    SettingsScreenContent(
        state = state,
        onValueChange = viewModel::updatePreference,
        onValueChangeFinished = viewModel::savePreference,
        onSPenSleepEnabledChange = viewModel::onSPenSleepEnabledChange,
        onCursorTypeChange = viewModel::onCursorTypeChange,
        onCustomCursorFileSelected = viewModel::loadCustomCursorImage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    state: SettingsScreenState,
    onValueChange: (PrefKey<Float>, Float) -> Unit = { _, _ -> },
    onValueChangeFinished: (PrefKey<Float>, Float) -> Unit = { _, _ -> },
    onSPenSleepEnabledChange: (Boolean) -> Unit = {},
    onCursorTypeChange: (CursorType) -> Unit = {},
    onCustomCursorFileSelected: (Uri) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = { Text(text = stringResource(R.string.screen_settings)) },
            colors = TopAppBarDefaults.topAppBarColors().copy(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(elevation_1)
            ),
            actions = {
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        painter = painterResource(R.drawable.reset_settings_24px),
                        contentDescription = stringResource(R.string.settings_reset_to_defaults)
                    )
                }
            }
        )
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
        SettingsChangeCursor(
            cursorType = state.cursorType,
            onCursorTypeChange = onCursorTypeChange,
            onCustomCursorFileSelected = onCustomCursorFileSelected
        )
        Spacer(
            modifier = Modifier.weight(1f)
        )
        HorizontalDivider()
        BirdHuntBanner()
        HorizontalDivider()
        AppVersion()
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
                val roundedValue = (round(it  / prefKey.step)) * prefKey.step
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
        modifier = Modifier.padding(pad_m)
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
private fun SettingsChangeCursor(
    cursorType: CursorType,
    onCursorTypeChange: (CursorType) -> Unit = {},
    onCustomCursorFileSelected: (Uri) -> Unit = {}
) {
    val radioButtonsHeight = remember { mutableIntStateOf(0) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(pad_l),
        modifier = Modifier
            .padding(pad_xl)
            .fillMaxWidth()
    ) {
        CursorPreview(
            cursorType = cursorType,
            radioButtonsHeight = radioButtonsHeight.intValue,
            onCustomCursorFileSelected = onCustomCursorFileSelected
        )
        CursorTypeSelector(
            cursorType = cursorType,
            onCursorTypeChange = onCursorTypeChange,
            radioButtonsHeight = radioButtonsHeight
        )
    }
}

@Composable
private fun RowScope.CursorPreview(
    cursorType: CursorType,
    radioButtonsHeight: Int,
    onCustomCursorFileSelected: (Uri) -> Unit = {}
) {
    val context = LocalContext.current
    var cursorBitmap by remember(cursorType) {
        mutableStateOf(getCursorBitmap(context, cursorType))
    }

    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            onCustomCursorFileSelected(uri)
            cursorBitmap = getCursorBitmap(context, cursorType)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    val previewCorner by animateDpAsState(
        targetValue = if (cursorType == CursorType.CUSTOM) 0.dp else radius_m,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )

    Column(
        modifier = Modifier.weight(1f)
    ) {
        Surface(
            tonalElevation = elevation_2,
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = radius_m,
                        topEnd = radius_m,
                        bottomStart = previewCorner,
                        bottomEnd = previewCorner,
                    )
                )
                .height(with(LocalDensity.current) { radioButtonsHeight.toDp() })

        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(pad_l)
                    .fillMaxSize()
            ) {
                cursorBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = stringResource(R.string.settings_cursor_preview),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
        CustomCursorButton(
            cursorType = cursorType,
            pickImage = pickImage
        )
    }
}

@Composable
private fun CustomCursorButton(
    cursorType: CursorType,
    pickImage: ManagedActivityResultLauncher<String, Uri?>
) {
    AnimatedVisibility(
        visible = cursorType == CursorType.CUSTOM,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            FilledTonalButton(
                onClick = {
                    pickImage.launch("image/*")
                },
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = radius_m,
                    bottomEnd = radius_m
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                Text(stringResource(R.string.settings_change_cursor_button))
            }
        }
    }
}

@Composable
private fun RowScope.CursorTypeSelector(
    cursorType: CursorType,
    onCursorTypeChange: (CursorType) -> Unit = {},
    radioButtonsHeight: MutableIntState
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(pad_xs),
        modifier = Modifier
            .selectableGroup()
            .weight(2f)
            .onGloballyPositioned { coordinates ->
                radioButtonsHeight.intValue = coordinates.size.height
            }
    ) {
        CursorType.entries.forEach {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(CircleShape)
                    .fillMaxWidth()
                    .selectable(
                        selected = cursorType == it,
                        onClick = { onCursorTypeChange(it) },
                        role = Role.RadioButton
                    )
                    .padding(pad_xs)
            ) {
                RadioButton(
                    selected = cursorType == it,
                    onClick = null
                )
                Text(
                    text = stringResource(it.label),
                    modifier = Modifier.padding(start = pad_s)
                )
            }
        }
    }
}

@Composable
private fun BirdHuntBanner() {
    val context = LocalContext.current
    var bannerSize by remember { mutableIntStateOf(0) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .onGloballyPositioned { bannerSize = it.size.height }
            .openUrl(context, R.string.settings_bird_hunt_url)
    ) {
        Row(
            modifier = Modifier.padding(start = pad_xl, top = pad_m, bottom = pad_m)
        ) {
            Text(
                text = stringResource(R.string.settings_bird_hunt_check),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = pad_xs)
            )
            Column {
                Text(
                    text = stringResource(R.string.settings_bird_hunt),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(R.string.settings_bird_hunt_desc),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
        Image(
            painter = painterResource(R.drawable.birdhunt_banner),
            contentDescription = "Bird Hunt",
            contentScale = ContentScale.Fit,
            modifier = Modifier.height(with(LocalDensity.current) { bannerSize.toDp() })
        )
    }
}

@Composable
private fun AppVersion() {
    val context = LocalContext.current

    val versionName = if (LocalInspectionMode.current) {
        stringResource(R.string.settings_app_info_version_unknown)
    } else {
        context.packageManager.getPackageInfo(
            context.packageName,
            0
        ).versionName ?: stringResource(R.string.settings_app_info_version_unknown)
    }

    val appInfoText = stringResource(
        R.string.settings_app_info,
        stringResource(R.string.app_name),
        versionName
    )

    Column (
        horizontalAlignment = Alignment.End,
        modifier = Modifier.fillMaxWidth().padding(pad_l)
    ) {
        Row {
            Text(
                text = appInfoText,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = APP_INFO_TEXT_ALPHA),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(R.string.settings_app_info_author),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = APP_INFO_TEXT_ALPHA),
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.openUrl(context, R.string.settings_app_info_author_url)
            )
        }
        Text(
            text = stringResource(R.string.settings_app_info_github),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = APP_INFO_TEXT_ALPHA),
            textDecoration = TextDecoration.Underline,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.openUrl(context, R.string.settings_app_info_author_url)
        )
    }
}

@Suppress("unused")
private const val TAG = "SettingsScreen"
private const val APP_INFO_TEXT_ALPHA = 0.6f

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun SettingsScreenPreview() {
    PenMouseSThemePreview {
        SettingsScreenContent(
            state = SettingsScreenState(
                cursorType = CursorType.LIGHT
            )
        )
    }
}