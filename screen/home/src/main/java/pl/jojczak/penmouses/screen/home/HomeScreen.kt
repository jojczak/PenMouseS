package pl.jojczak.penmouses.screen.home

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent.ModeStatus
import pl.jojczak.penmouses.core.common.types.ManualPageType
import pl.jojczak.penmouses.core.ui.theme.PenMouseSDevicePreview
import pl.jojczak.penmouses.core.ui.theme.pad_l
import pl.jojczak.penmouses.core.ui.theme.pad_s
import pl.jojczak.penmouses.core.ui.theme.radius_l
import pl.jojczak.penmouses.core.ui.utils.add
import pl.jojczak.penmouses.screen.home.components.stepsContainer
import pl.jojczak.penmouses.screen.home.dialog.DialogFirstRun
import pl.jojczak.penmouses.screen.home.dialog.DialogUnsupportedDevice
import pl.jojczak.penmouses.core.ui.R as coreR

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    navigationHazeState: HazeState,
    showManualPageClicked: (ManualPageType) -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val activity = LocalActivity.current
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(lifecycleState) {
        viewModel.onViewAction(HomeViewAction.LifecycleEvent(lifecycleState))
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is HomeScreenEvent.TryToShowReviewDialog && activity != null) {
                tryToShowReviewDialog(activity)
            }
        }
    }

    //@formatter:off
    HomeScreenContent(
        state = state,
        navigationHazeState = navigationHazeState,
        paddingValues = paddingValues,
        showManualPageClicked = showManualPageClicked,
        sendEventToService = { viewModel.onViewAction(HomeViewAction.SendEventToService(it)) },
        toggleUnsupportedDeviceDialog = { viewModel.onViewAction(HomeViewAction.ToggleUnsupportedDeviceDialog(it)) },
        toggleFirstRunDialog = { viewModel.onViewAction(HomeViewAction.ToggleFirstRunDialog(it)) }
    )
    //@formatter:on
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun HomeScreenContent(
    state: HomeScreenState,
    navigationHazeState: HazeState,
    paddingValues: PaddingValues,
    showManualPageClicked: (ManualPageType) -> Unit,
    sendEventToService: (event: AppToServiceEvent.Event) -> Unit = {},
    toggleUnsupportedDeviceDialog: (Boolean) -> Unit,
    toggleFirstRunDialog: (Boolean) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .hazeSource(navigationHazeState)
    ) {
        if (maxHeight > maxWidth) {
            PortraitLayout(
                serviceStatus = state.serviceStatus,
                paddingValues = paddingValues,
                isAccessibilityEnabled = state.isAccessibilityEnabled,
                toggleService = sendEventToService,
                showManualPageClicked = showManualPageClicked
            )
        } else {
            LandscapeLayout(
                serviceStatus = state.serviceStatus,
                paddingValues = paddingValues,
                isAccessibilityEnabled = state.isAccessibilityEnabled,
                toggleService = sendEventToService,
                showManualPageClicked = showManualPageClicked
            )
        }
    }

    if (state.unsupportedDeviceDialogEnabled) {
        DialogUnsupportedDevice { toggleUnsupportedDeviceDialog(false) }
    }

    if (state.firstRunDialogEnabled) {
        DialogFirstRun(
            onDismiss = { toggleFirstRunDialog(false) },
            onConfirm = {
                toggleFirstRunDialog(false)
                showManualPageClicked(ManualPageType.HowToUse)
            }
        )
    }
}

@Composable
private fun PortraitLayout(
    serviceStatus: ModeStatus,
    paddingValues: PaddingValues,
    isAccessibilityEnabled: Boolean,
    showManualPageClicked: (ManualPageType) -> Unit,
    toggleService: (event: AppToServiceEvent.Event) -> Unit,
) = LazyColumn(
    contentPadding = paddingValues.add(pad_l),
    verticalArrangement = Arrangement.spacedBy(pad_s)
) {
    item {
        AppLogo(
            showManualPageClicked = showManualPageClicked,
            modifier = Modifier.padding(bottom = pad_s)
        )
    }

    stepsContainer(
        serviceStatus = serviceStatus,
        toggleService = toggleService,
        isAccessibilityEnabled = isAccessibilityEnabled,
        showManualPageClicked = showManualPageClicked
    )
}

@Composable
private fun LandscapeLayout(
    serviceStatus: ModeStatus,
    paddingValues: PaddingValues,
    isAccessibilityEnabled: Boolean,
    showManualPageClicked: (ManualPageType) -> Unit,
    toggleService: (event: AppToServiceEvent.Event) -> Unit,
) = Row(
    horizontalArrangement = Arrangement.spacedBy(pad_l)
) {
    AppLogo(
        showManualPageClicked = showManualPageClicked,
        modifier = Modifier
            .padding(
                start = paddingValues.calculateStartPadding(LocalLayoutDirection.current) + pad_l,
                top = paddingValues.calculateTopPadding() + pad_l
            )
            .weight(1f)
    )
    LazyColumn(
        contentPadding = PaddingValues(
            top = paddingValues.calculateTopPadding() + pad_l,
            end = paddingValues.calculateEndPadding(LocalLayoutDirection.current) + pad_l,
            bottom = paddingValues.calculateBottomPadding() + pad_l
        ),
        verticalArrangement = Arrangement.spacedBy(pad_s),
        modifier = Modifier.weight(2f)
    ) {
        stepsContainer(
            serviceStatus = serviceStatus,
            toggleService = toggleService,
            isAccessibilityEnabled = isAccessibilityEnabled,
            showManualPageClicked = showManualPageClicked
        )
    }
}

private fun tryToShowReviewDialog(activity: Activity) {
    val manager = ReviewManagerFactory.create(activity)
    val request = manager.requestReviewFlow()
    request.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            manager.launchReviewFlow(activity, task.result)
        } else {
            Log.e("HomeScreen", "tryToShowReviewDialog", task.exception as ReviewException)
        }
    }
}

@Composable
private fun AppLogo(
    modifier: Modifier = Modifier,
    showManualPageClicked: (ManualPageType) -> Unit,
) = Image(
    painter = painterResource(coreR.drawable.logo),
    contentDescription = stringResource(R.string.home_logo_alt),
    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
    modifier = modifier
        .clip(RoundedCornerShape(radius_l))
        .clickable { showManualPageClicked(ManualPageType.AboutPenMouseS) }
        .padding(pad_l)
)

@Composable
@Preview
private fun HomeScreenPreview() {
    PenMouseSDevicePreview {
        HomeScreenContent(
            state = HomeScreenState(),
            navigationHazeState = rememberHazeState(),
            paddingValues = it,
            showManualPageClicked = {},
            toggleUnsupportedDeviceDialog = {},
            toggleFirstRunDialog = {}
        )
    }
}