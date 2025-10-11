package pl.jojczak.penmouses.screen.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.chrisbanes.haze.HazeState
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent.PenMode
import pl.jojczak.penmouses.core.ui.R as coreR
import pl.jojczak.penmouses.core.ui.theme.PenMouseSDevicePreview
import pl.jojczak.penmouses.core.ui.theme.pad_l
import pl.jojczak.penmouses.core.ui.theme.radius_l
import pl.jojczak.penmouses.screen.home.components.StepsContainer

@Composable
fun HomeScreen(
    paddingValues: PaddingValues = PaddingValues(),
    setTopBar: ((@Composable (HazeState) -> Unit)?) -> Unit = {},
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(lifecycleState) {
        viewModel.onLifecycleEvent(lifecycleState)
    }

    LaunchedEffect(Unit) {
        setTopBar(null)
    }

    HomeScreenContent(
        state = state,
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
    paddingValues: PaddingValues,
    changeDialogState: (step: Int, show: Boolean) -> Unit = { _, _ -> },
    toggleService: (event: AppToServiceEvent.Event) -> Unit = {},
    togglePermissionNotification: (state: Boolean) -> Unit = {}
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        if (maxHeight > maxWidth) {
            PortraitLayout(
                serviceStatus = state.serviceStatus,
                toggleService = toggleService,
                changeDialogState = changeDialogState
            )
        } else {
            LandscapeLayout(
                serviceStatus = state.serviceStatus,
                toggleService = toggleService,
                changeDialogState = changeDialogState
            )
        }
    }
}

@Composable
private fun PortraitLayout(
    serviceStatus: PenMode,
    changeDialogState: (step: Int, show: Boolean) -> Unit,
    toggleService: (event: AppToServiceEvent.Event) -> Unit,
) {
    Column {
        AppLogo(changeDialogState = changeDialogState)
        StepsContainer(
            serviceStatus = serviceStatus,
            toggleService = toggleService,
            changeDialogState = changeDialogState,
            modifier = Modifier.padding(horizontal = pad_l)
        )
    }
}

@Composable
private fun LandscapeLayout(
    serviceStatus: PenMode,
    changeDialogState: (step: Int, show: Boolean) -> Unit,
    toggleService: (event: AppToServiceEvent.Event) -> Unit,
) {
    Row {
        AppLogo(changeDialogState = changeDialogState)
        StepsContainer(
            serviceStatus = serviceStatus,
            toggleService = toggleService,
            changeDialogState = changeDialogState,
        )
    }
}

@Composable
private fun AppLogo(
    changeDialogState: (step: Int, show: Boolean) -> Unit
) {
    Image(
        painter = painterResource(coreR.drawable.logo),
        contentDescription = stringResource(coreR.string.home_logo_alt),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .padding(pad_l)
            .clip(RoundedCornerShape(radius_l))
            .clickable {
                changeDialogState(6, true)
            }
            .padding(pad_l)
    )
}

@Composable
@Preview(showSystemUi = true)
private fun HomeScreenPreview() {
    PenMouseSDevicePreview {
        HomeScreenContent(
            state = HomeScreenState(),
            paddingValues = PaddingValues()
        )
    }
}