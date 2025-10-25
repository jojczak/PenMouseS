package pl.jojczak.penmouses.screen.manual

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults.TopAppBarExpandedHeight
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch
import pl.jojczak.penmouses.core.ui.theme.PenMouseSDevicePreview
import pl.jojczak.penmouses.core.ui.theme.hazeUltraThinSurface
import pl.jojczak.penmouses.core.ui.theme.pad_l
import pl.jojczak.penmouses.core.ui.utils.copy
import pl.jojczak.penmouses.screen.manual.components.ManualTopAppBar
import pl.jojczak.penmouses.screen.manual.components.aboutComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualScreen(
    hazeState: HazeState,
    paddingValues: PaddingValues,
    manualDrawerState: DrawerState,
    viewState: ManualViewState
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val localDensity = LocalDensity.current
    var topAppBarHeight by remember { mutableStateOf(TopAppBarExpandedHeight) }

    BackHandler(manualDrawerState.isOpen) {
        scope.launch { manualDrawerState.close() }
    }

    Box {
        LazyColumn(
            contentPadding = paddingValues.copy(top = topAppBarHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.hazeSource(state = hazeState)
        ) {
            if (viewState.page == ManualPageType.AboutPenMouseS) {
                aboutComponent(ctx)
            }
            manualPage(markdown = viewState.markdownContent)
        }

        ManualTopAppBar(
            onMenuIconClicked = {
                scope.launch {
                    manualDrawerState.apply {
                        if (isClosed) open() else close()
                    }
                }
            },
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
}

private fun LazyListScope.manualPage(markdown: String) = item {
    MarkdownText(
        markdown = markdown,
        style = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        modifier = Modifier.padding(all = pad_l)
    )
}

@Preview
@Composable
private fun ManualScreenPreview() {
    PenMouseSDevicePreview {
        ManualScreen(
            hazeState = rememberHazeState(),
            paddingValues = it,
            manualDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
            viewState = ManualViewState()
        )
    }
}