package pl.jojczak.penmouses.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.core.content.ContextCompat
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
    paddingValues: PaddingValues = PaddingValues(),
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(lifecycleState) {
        viewModel.onLifecycleEvent(lifecycleState)
    }

    HomeScreenContent(
        state = state,
        paddingValues = paddingValues,
        changeDialogState = viewModel::changeDialogState,
        toggleService = viewModel::sendSignalToService,
        togglePermissionNotification = viewModel::togglePermissionNotification
    )
}

@Composable
private fun HomeScreenContent(
    state: HomeScreenState,
    paddingValues: PaddingValues = PaddingValues(),
    changeDialogState: (Int, Boolean) -> Unit = { _, _ -> },
    toggleService: (AppToServiceEvent.Event) -> Unit = { },
    togglePermissionNotification: (Boolean) -> Unit = { }
) {
    HomeScreenContentPlacer(
        paddingValues = paddingValues,
        modifier = Modifier.padding(
            top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        )
    ) {
        StepsContainer(
            isAccessibilityEnabled = state.isAccessibilityEnabled,
            serviceStatus = state.serviceStatus,
            showNotificationPermission = state.showNotificationPermission,
            changeDialogState = changeDialogState,
            toggleService = toggleService,
            togglePermissionNotification = togglePermissionNotification
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

    Step3Dialog(
        showDialog = state.showStep3Dialog,
        changeDialogState = changeDialogState
    )
}

@Composable
private fun HomeScreenContentPlacer(
    paddingValues: PaddingValues,
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
                Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
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
                Column(
                    modifier = Modifier
                        .weight(0.618f)
                        .verticalScroll(rememberScrollState())
                ) {
                    content()
                    Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
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
    isAccessibilityEnabled: Boolean,
    serviceStatus: AppToServiceEvent.ServiceStatus,
    showNotificationPermission: Boolean,
    changeDialogState: (Int, Boolean) -> Unit,
    toggleService: (AppToServiceEvent.Event) -> Unit,
    togglePermissionNotification: (Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(pad_s),
        modifier = Modifier.padding(vertical = pad_m)
    ) {
        Step1(
            onMoreClick = { changeDialogState(1, true) }
        )
        Step2(
            onMoreClick = { changeDialogState(2, true) }
        )
        StepContainer(
            stepTextResId = R.string.home_steps_3,
            stepSettingsResId = R.string.home_steps_3_settings,
            blocked = false,
            onMoreClick = { changeDialogState(3, true) },
            settingsCallback = { openAccessibilitySettings(it) }
        ) {
            StepSwitch(
                stepDescResId = R.string.home_steps_3_des,
                checked = isAccessibilityEnabled,
                enabled = !isAccessibilityEnabled,
                onCheckedChange = {
                    if (it) changeDialogState(3, true)
                }
            )
        }
        StepContainer(
            stepTextResId = R.string.home_steps_4,
            blocked = !isAccessibilityEnabled,
        ) {
            StepButton(
                serviceStatus = serviceStatus,
                showNotificationPermission = showNotificationPermission,
                toggleService = toggleService,
                togglePermissionNotification = togglePermissionNotification
            )
        }
    }
}

@Composable
private fun Step1(
    onMoreClick: (() -> Unit)
) {
    Surface(
        tonalElevation = elevation_2,
        shape = RoundedCornerShape(pad_s),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                StepHeader(
                    stepTextResId = R.string.home_steps_1,
                    onMoreClick = onMoreClick,
                )
                Text(
                    text = stringResource(R.string.home_steps_1_des),
                    modifier = Modifier.padding(start = pad_m, bottom = pad_m)
                )
            }
            SettingsLink(
                stepSettingsResId = R.string.home_steps_1_settings,
                buttonPadding = pad_m,
                settingsCallback = { openSettings(it) }
            )
        }
    }
}

@Composable
private fun Step2(
    onMoreClick: (() -> Unit)
) {
    Surface(
        tonalElevation = elevation_2,
        shape = RoundedCornerShape(pad_s),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            StepHeader(
                stepTextResId = R.string.home_steps_2,
                onMoreClick = onMoreClick,
            )
            Text(
                text = stringResource(R.string.home_steps_2_des),
                modifier = Modifier.padding(start = pad_m, bottom = pad_m),
                textAlign = TextAlign.Justify
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
            Spacer(
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier.padding(pad_s)
            ) {
                SettingsLink(
                    stepSettingsResId = stepSettingsResId,
                    buttonPadding = pad_xs,
                    style = MaterialTheme.typography.bodySmall,
                    settingsCallback = settingsCallback
                )
            }
        }
    }
}

@Composable
private fun SettingsLink(
    @StringRes stepSettingsResId: Int,
    buttonPadding: Dp,
    style: TextStyle = LocalTextStyle.current,
    settingsCallback: (Context) -> Unit = { },
) {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(pad_xs),
        modifier = Modifier
            .clip(RoundedCornerShape(clickable_text_corner))
            .clickable { settingsCallback(context) }
            .padding(buttonPadding)
    ) {
        Text(
            text = stringResource(stepSettingsResId),
            color = MaterialTheme.colorScheme.primary,
            style = style
        )
        Icon(
            painter = painterResource(R.drawable.open_in_new_24px),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
            modifier = Modifier.size(LINK_ICON_SIZE)
        )
    }
}

@Composable
private fun StepButton(
    serviceStatus: AppToServiceEvent.ServiceStatus,
    showNotificationPermission: Boolean,
    toggleService: (AppToServiceEvent.Event) -> Unit,
    togglePermissionNotification: (Boolean) -> Unit
) {
    Text(
        text = stringResource(R.string.home_steps_4_more),
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
                        text = stringResource(R.string.home_steps_4_turn_off),
                    )
                }
            }

            AppToServiceEvent.ServiceStatus.OFF -> {
                Button(
                    onClick = {
                        togglePermissionNotification(true)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.home_steps_4_turn_on),
                    )
                }
            }
        }
    }

    if (showNotificationPermission) {
        NotificationPermission(
            togglePermissionNotification = togglePermissionNotification,
            toggleService = toggleService
        )
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

@Composable
private fun NotificationPermission(
    togglePermissionNotification: (Boolean) -> Unit,
    toggleService: (AppToServiceEvent.Event) -> Unit
) {
    val context = LocalContext.current
    val afterPermission = {
        togglePermissionNotification(false)
        toggleService(AppToServiceEvent.Event.Start)
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        Log.d(TAG, "Notification permission ${if (it) "granted" else "denied"}")
        afterPermission()
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return@LaunchedEffect
            }
        }
        afterPermission()
    }
}

private const val TAG = "HomeScreen"

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
                isAccessibilityEnabled = true
            )
        )
    }
}


