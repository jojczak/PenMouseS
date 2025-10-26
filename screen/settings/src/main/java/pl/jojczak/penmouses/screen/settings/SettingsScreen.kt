package pl.jojczak.penmouses.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults.TopAppBarExpandedHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import pl.jojczak.penmouses.core.ui.theme.PenMouseSDevicePreview
import pl.jojczak.penmouses.screen.settings.components.ResetSettingsDialog
import pl.jojczak.penmouses.screen.settings.components.SettingsTopAppBar
import pl.jojczak.penmouses.screen.settings.tab.general.SettingsGeneralTab
import pl.jojczak.penmouses.screen.settings.tab.mouse.SettingsMouseTab
import pl.jojczak.penmouses.screen.settings.tab.point.SettingsPointTab
import pl.jojczak.penmouses.screen.settings.tab.scroll.SettingsScrollTab

@Composable
fun SettingsScreen(
    paddingValues: PaddingValues = PaddingValues(),
    navigationHazeState: HazeState,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // @formatter:off
    SettingsScreenContent(
        state = state,
        navigationHazeState = navigationHazeState,
        paddingValues = paddingValues,
        resetSettingsDialogClicked = { viewModel.onViewAction(SettingsViewAction.ToggleResetDialog(it)) },
        resetSettings = { viewModel.onViewAction(SettingsViewAction.ResetSettings) }
    )
    // @formatter:on
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenContent(
    state: SettingsScreenState,
    navigationHazeState: HazeState,
    paddingValues: PaddingValues,
    resetSettingsDialogClicked: (Boolean) -> Unit = {},
    resetSettings: () -> Unit = {}
) {
    val localDensity = LocalDensity.current
    val localHazeState = rememberHazeState()
    val pagerState = rememberPagerState { SettingTabs.entries.size }
    var topAppBarHeight by remember { mutableStateOf(TopAppBarExpandedHeight) }
    val contentPadding = rememberPagerContentPadding(paddingValues, topAppBarHeight)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SettingsPager(
            pagerState = pagerState,
            contentPadding = contentPadding,
            refreshDataTrigger = state.showSettingsResetDialog,
            modifier = Modifier
                .hazeSource(navigationHazeState)
                .hazeSource(localHazeState)
        )

        SettingsTopAppBar(
            localHazeState = localHazeState,
            pagerState = pagerState,
            resetSettingsDialogClicked = resetSettingsDialogClicked,
            modifier = Modifier.onGloballyPositioned {
                with(localDensity) { topAppBarHeight = it.size.height.toDp() }
            },
        )
    }

    if (state.showSettingsResetDialog) {
        ResetSettingsDialog(
            toggleSettingsResetDialog = resetSettingsDialogClicked,
            resetSettings = resetSettings
        )
    }
}

@Composable
private fun SettingsPager(
    pagerState: PagerState,
    contentPadding: PaddingValues,
    refreshDataTrigger: Boolean,
    modifier: Modifier = Modifier
) = HorizontalPager(
    state = pagerState,
    modifier = Modifier
        .fillMaxSize()
        .then(modifier)
) {
    if (LocalInspectionMode.current) return@HorizontalPager

    when (it) {
        0 -> SettingsMouseTab(
            contentPadding = contentPadding,
            refreshDataTrigger = refreshDataTrigger
        )

        1 -> SettingsPointTab(
            contentPadding = contentPadding,
            refreshDataTrigger = refreshDataTrigger
        )

        2 -> SettingsScrollTab(
            contentPadding = contentPadding,
            refreshDataTrigger = refreshDataTrigger
        )

        else -> SettingsGeneralTab(
            contentPadding = contentPadding
        )
    }
}

@Composable
private fun rememberPagerContentPadding(
    paddingValues: PaddingValues,
    topAppBarHeight: Dp
): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return remember(paddingValues, topAppBarHeight) {
        PaddingValues(
            start = paddingValues.calculateStartPadding(layoutDirection),
            top = topAppBarHeight,
            end = paddingValues.calculateEndPadding(layoutDirection),
            bottom = paddingValues.calculateBottomPadding()
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    PenMouseSDevicePreview {
        SettingsScreenContent(
            state = SettingsScreenState(),
            navigationHazeState = rememberHazeState(),
            paddingValues = it
        )
    }
}