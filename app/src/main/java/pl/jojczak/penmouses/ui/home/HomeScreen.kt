package pl.jojczak.penmouses.ui.home

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import pl.jojczak.penmouses.R
import pl.jojczak.penmouses.service.AppToServiceEvent
import pl.jojczak.penmouses.ui.theme.LINK_ICON_SIZE
import pl.jojczak.penmouses.ui.theme.PenMouseSThemePreview
import pl.jojczak.penmouses.ui.theme.clickable_text_corner
import pl.jojczak.penmouses.ui.theme.elevation_2
import pl.jojczak.penmouses.ui.theme.getRedButtonColors
import pl.jojczak.penmouses.ui.theme.pad_l
import pl.jojczak.penmouses.ui.theme.pad_m
import pl.jojczak.penmouses.ui.theme.pad_s
import pl.jojczak.penmouses.ui.theme.pad_xs
import pl.jojczak.penmouses.ui.theme.pad_xxl

private const val STEP_TITLE_ALPHA = 0.7f
private const val STEP_MORE_ALPHA = 0.5f

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(lifecycleState) {
        viewModel.onLifecycleEvent(lifecycleState)
    }

    HomeScreenContent(
        state = state,
        changeSPenFeaturesState = viewModel::changeSPenFeaturesState,
        changeDialogState = viewModel::changeDialogState,
        toggleService = viewModel::sendSignalToService
    )
}

@Composable
private fun HomeScreenContent(
    state: HomeScreenState,
    changeSPenFeaturesState: (Boolean) -> Unit = { },
    changeDialogState: (Int, Boolean) -> Unit = { _, _ -> },
    toggleService: (AppToServiceEvent.Event) -> Unit = { }
) {
    HomeScreenContentPlacer(
        modifier = Modifier.padding(
            top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        )
    ) {
        StepsContainer(
            areSPenFeaturesDisabled = state.areSPenFeaturesDisabled,
            isAccessibilityEnabled = state.isAccessibilityEnabled,
            serviceStatus = state.serviceStatus,
            changeSPenFeaturesState = changeSPenFeaturesState,
            changeDialogState = changeDialogState,
            toggleService = toggleService
        )
    }

    Step1Dialog(
        showDialog = state.showStep1Dialog,
        changeDialogState = changeDialogState
    )

    Step2Dialog(
        showDialog = state.showStep2Dialog,
        changeDialogState = changeDialogState
    )
}

@Composable
private fun HomeScreenContentPlacer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
    ) {
        if (maxHeight > maxWidth) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = pad_l)
                    .verticalScroll(rememberScrollState())
            ) {
                AppLogo()
                content()
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = pad_l),
            ) {
                Box(modifier = Modifier.weight(0.382f)) {
                    AppLogo()
                }
                Box(
                    modifier = Modifier
                        .weight(0.618f)
                        .verticalScroll(rememberScrollState())
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun AppLogo() {
    Image(
        painter = painterResource(R.drawable.logo_light),
        contentDescription = stringResource(R.string.home_logo_alt),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
        modifier = Modifier.padding(pad_xxl)
    )
}

@Composable
private fun StepsContainer(
    areSPenFeaturesDisabled: Boolean,
    isAccessibilityEnabled: Boolean,
    serviceStatus: AppToServiceEvent.ServiceStatus,
    changeSPenFeaturesState: (Boolean) -> Unit = { },
    changeDialogState: (Int, Boolean) -> Unit = { _, _ -> },
    toggleService: (AppToServiceEvent.Event) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(pad_s),
        modifier = Modifier.padding(vertical = pad_m)
    ) {
        StepContainer(
            stepTextResId = R.string.home_steps_1,
            stepSettingsResId = R.string.home_steps_1_settings,
            blocked = false,
            onMoreClick = { changeDialogState(1, true) },
            settingsCallback = { openSettings(it) }
        ) {
            StepSwitch(
                stepDescResId = R.string.home_steps_1_des,
                checked = areSPenFeaturesDisabled,
                onCheckedChange = { changeSPenFeaturesState(!areSPenFeaturesDisabled) }
            )
        }
        StepContainer(
            stepTextResId = R.string.home_steps_2,
            stepSettingsResId = R.string.home_steps_2_settings,
            blocked = !areSPenFeaturesDisabled,
            onMoreClick = { changeDialogState(2, true) },
            settingsCallback = { openAccessibilitySettings(it) }
        ) {
            StepSwitch(
                stepDescResId = R.string.home_steps_2_des,
                checked = isAccessibilityEnabled,
                enabled = !isAccessibilityEnabled,
                onCheckedChange = {
                    if (it) changeDialogState(2, true)
                }
            )
        }
        StepContainer(
            stepTextResId = R.string.home_steps_3,
            blocked = !areSPenFeaturesDisabled || !isAccessibilityEnabled,
        ) {
            StepButton(
                serviceStatus = serviceStatus,
                toggleService = toggleService
            )
        }
    }
}

@Composable
private fun StepContainer(
    @StringRes stepTextResId: Int,
    @StringRes stepSettingsResId: Int? = null,
    blocked: Boolean,
    onMoreClick: (() -> Unit)? = null,
    settingsCallback: (Context) -> Unit = { },
    content: @Composable () -> Unit
) {
    Surface(
        tonalElevation = elevation_2,
        shape = RoundedCornerShape(pad_s),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {
            Column {
                StepHeader(
                    stepTextResId = stepTextResId,
                    stepSettingsResId = stepSettingsResId,
                    onMoreClick = onMoreClick,
                    settingsCallback = settingsCallback
                )
                content()
            }
            if (blocked) {
                Box(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceColorAtElevation(elevation_2)
                                .copy(alpha = 0.7f)
                        )
                        .matchParentSize()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {}
                        )
                )
            }
        }
    }
}

@Composable
private fun StepHeader(
    @StringRes stepTextResId: Int,
    @StringRes stepSettingsResId: Int? = null,
    onMoreClick: (() -> Unit)? = null,
    settingsCallback: (Context) -> Unit = { },
) {
    Row(verticalAlignment = Alignment.Top) {
        Row {
            Text(
                text = stringResource(stepTextResId),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = STEP_TITLE_ALPHA),
                modifier = Modifier.padding(top = pad_m, start = pad_m)
            )
            onMoreClick?.let {
                Text(
                    text = stringResource(R.string.home_steps_bullet),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = STEP_TITLE_ALPHA),
                    modifier = Modifier.padding(top = pad_m)
                )
                Box(
                    modifier = Modifier.padding(top = pad_s)
                ) {
                    Text(
                        text = stringResource(R.string.home_steps_more),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = STEP_TITLE_ALPHA),
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .clip(RoundedCornerShape(clickable_text_corner))
                            .clickable { onMoreClick() }
                            .padding(pad_xs)
                    )
                }
            }
        }
        if (stepSettingsResId != null) {
            SettingsLink(
                stepSettingsResId = stepSettingsResId,
                settingsCallback = settingsCallback
            )
        }
    }
}

@Composable
private fun RowScope.SettingsLink(
    @StringRes stepSettingsResId: Int,
    settingsCallback: (Context) -> Unit = { },
) {
    val context = LocalContext.current

    Spacer(
        modifier = Modifier.weight(1f)
    )
    Box(
        modifier = Modifier.padding(pad_s)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(pad_xs),
            modifier = Modifier
                .clip(RoundedCornerShape(clickable_text_corner))
                .clickable { settingsCallback(context) }
                .padding(pad_xs)
        ) {
            Text(
                text = stringResource(stepSettingsResId),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = STEP_TITLE_ALPHA),
                style = MaterialTheme.typography.bodySmall
            )
            Icon(
                painter = painterResource(R.drawable.open_in_new_24px),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = STEP_TITLE_ALPHA),
                contentDescription = null,
                modifier = Modifier.size(LINK_ICON_SIZE)
            )
        }
    }
}

@Composable
private fun StepButton(
    serviceStatus: AppToServiceEvent.ServiceStatus,
    toggleService: (AppToServiceEvent.Event) -> Unit = { }
) {
    Text(
        text = stringResource(R.string.home_steps_3_more),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = STEP_MORE_ALPHA),
        fontStyle = FontStyle.Italic,
        textAlign = TextAlign.Justify,
        modifier = Modifier.padding(vertical = pad_s, horizontal = pad_m)
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = pad_m)
    ) {
        when (serviceStatus) {
            AppToServiceEvent.ServiceStatus.ON -> {
                Button(
                    onClick = { toggleService(AppToServiceEvent.Event.Stop) },
                    colors = getRedButtonColors()
                ) {
                    Text(
                        text = stringResource(R.string.home_steps_3_turn_off),
                    )
                }
            }

            AppToServiceEvent.ServiceStatus.OFF -> {
                Button(
                    onClick = { toggleService(AppToServiceEvent.Event.Start) }
                ) {
                    Text(
                        text = stringResource(R.string.home_steps_3_turn_on),
                    )
                }
            }
        }
    }
}

@Composable
private fun StepSwitch(
    @StringRes stepDescResId: Int,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit = { }
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(pad_l)
    ) {
        Text(
            text = stringResource(stepDescResId),
            modifier = Modifier
                .weight(1f)
                .padding(start = pad_m, bottom = pad_m)
        )

        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(end = pad_m, bottom = pad_xs)
        )
    }
}

@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun HomeScreenNightPreview() {
    PenMouseSThemePreview {
        HomeScreenContent(
            state = HomeScreenState(
                serviceStatus = AppToServiceEvent.ServiceStatus.ON,
                areSPenFeaturesDisabled = true,
                isAccessibilityEnabled = true
            )
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenDayPreview() {
    PenMouseSThemePreview {
        HomeScreenContent(
            state = HomeScreenState(
                serviceStatus = AppToServiceEvent.ServiceStatus.ON,
                areSPenFeaturesDisabled = true,
                isAccessibilityEnabled = true
            )
        )
    }
}


