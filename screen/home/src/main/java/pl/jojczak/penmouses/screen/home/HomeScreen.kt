package pl.jojczak.penmouses.screen.home

import android.annotation.SuppressLint
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
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent.PenMode
import pl.jojczak.penmouses.core.ui.theme.PenMouseSDevicePreview
import pl.jojczak.penmouses.core.ui.theme.pad_l
import pl.jojczak.penmouses.core.ui.theme.pad_s
import pl.jojczak.penmouses.core.ui.theme.radius_l
import pl.jojczak.penmouses.screen.home.components.stepsContainer
import pl.jojczak.penmouses.core.ui.R as coreR

@Composable
fun HomeScreen(
    paddingValues: PaddingValues = PaddingValues(),
    navigationHazeState: HazeState,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(lifecycleState) {
        viewModel.onLifecycleEvent(lifecycleState)
    }

    HomeScreenContent(
        state = state,
        navigationHazeState = navigationHazeState,
        paddingValues = paddingValues,
        changeDialogState = viewModel::changeDialogState,
        toggleService = viewModel::sendSignalToService,
        togglePermissionNotification = viewModel::togglePermissionNotification
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun HomeScreenContent(
    state: HomeScreenState,
    navigationHazeState: HazeState,
    paddingValues: PaddingValues,
    changeDialogState: (step: Int, show: Boolean) -> Unit = { _, _ -> },
    toggleService: (event: AppToServiceEvent.Event) -> Unit = {},
    togglePermissionNotification: (state: Boolean) -> Unit = {}
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
                toggleService = toggleService,
                changeDialogState = changeDialogState
            )
        } else {
            LandscapeLayout(
                serviceStatus = state.serviceStatus,
                paddingValues = paddingValues,
                toggleService = toggleService,
                changeDialogState = changeDialogState
            )
        }
    }
}

@Composable
private fun PortraitLayout(
    serviceStatus: PenMode,
    paddingValues: PaddingValues,
    changeDialogState: (step: Int, show: Boolean) -> Unit,
    toggleService: (event: AppToServiceEvent.Event) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(
            start = paddingValues.calculateStartPadding(LocalLayoutDirection.current) + pad_l,
            top = paddingValues.calculateTopPadding() + pad_l,
            end = paddingValues.calculateEndPadding(LocalLayoutDirection.current) + pad_l,
            bottom = paddingValues.calculateBottomPadding() + pad_l
        ),
        verticalArrangement = Arrangement.spacedBy(pad_s)
    ) {
        item {
            AppLogo(
                changeDialogState = changeDialogState,
                modifier = Modifier.padding(bottom = pad_s)
            )
        }

        stepsContainer(
            serviceStatus = serviceStatus,
            toggleService = toggleService,
            changeDialogState = changeDialogState,
        )
    }
}

@Composable
private fun LandscapeLayout(
    serviceStatus: PenMode,
    paddingValues: PaddingValues,
    changeDialogState: (step: Int, show: Boolean) -> Unit,
    toggleService: (event: AppToServiceEvent.Event) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(pad_l)
    ) {
        AppLogo(
            changeDialogState = changeDialogState,
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
                changeDialogState = changeDialogState,
            )
        }
    }
}

@Composable
private fun AppLogo(
    changeDialogState: (step: Int, show: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(coreR.drawable.logo),
        contentDescription = stringResource(R.string.home_logo_alt),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
        modifier = modifier
            .clip(RoundedCornerShape(radius_l))
            .clickable {
                changeDialogState(6, true)
            }
            .padding(pad_l)
    )
}

@Composable
@Preview
private fun HomeScreenPreview() {
    PenMouseSDevicePreview {
        HomeScreenContent(
            state = HomeScreenState(),
            navigationHazeState = rememberHazeState(),
            paddingValues = it
        )
    }
}