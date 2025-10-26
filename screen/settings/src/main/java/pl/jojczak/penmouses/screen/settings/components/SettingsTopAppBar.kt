package pl.jojczak.penmouses.screen.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import kotlinx.coroutines.launch
import pl.jojczak.penmouses.core.ui.theme.PenMouseSTheme
import pl.jojczak.penmouses.core.ui.theme.hazeUltraThinSurface
import pl.jojczak.penmouses.screen.settings.R
import pl.jojczak.penmouses.screen.settings.SettingTabs
import pl.jojczak.penmouses.core.ui.R as coreR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsTopAppBar(
    localHazeState: HazeState,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    resetSettingsDialogClicked: (Boolean) -> Unit
) = Column(
    modifier = modifier
        .hazeEffect(
            state = localHazeState,
            style = hazeUltraThinSurface()
        )
) {
    TopAppBar(
        title = { Text(text = stringResource(coreR.string.screen_settings)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        actions = {
            IconButton(
                onClick = {
                    resetSettingsDialogClicked(true)
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_reset_settings),
                    contentDescription = stringResource(R.string.settings_reset_to_defaults)
                )
            }
        },
    )
    PrimaryTabRow(
        containerColor = Color.Transparent,
        selectedTabIndex = pagerState.currentPage,
        divider = {},
        modifier = Modifier.fillMaxWidth()
    ) {
        val scope = rememberCoroutineScope()
        SettingTabs.entries.forEachIndexed { index, tab ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                text = { Text(text = stringResource(tab.tabNameId)) }
            )
        }
    }
}

@Preview
@Composable
private fun PreviewSettingsAppTopBar() {
    PenMouseSTheme {
        Surface {
            SettingsTopAppBar(
                localHazeState = HazeState(),
                pagerState = rememberPagerState { SettingTabs.entries.size },
                resetSettingsDialogClicked = {}
            )
        }
    }
}
