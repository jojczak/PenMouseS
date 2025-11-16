package pl.jojczak.penmouses.screen.manual

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import com.halilibo.richtext.markdown.node.AstNode
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch
import pl.jojczak.penmouses.core.common.types.ManualPageType
import pl.jojczak.penmouses.core.ui.components.PenMouseSMarkdown
import pl.jojczak.penmouses.core.ui.theme.PenMouseSDevicePreview
import pl.jojczak.penmouses.core.ui.theme.hazeUltraThinSurface
import pl.jojczak.penmouses.core.ui.theme.pad_l
import pl.jojczak.penmouses.core.ui.utils.add
import pl.jojczak.penmouses.core.ui.utils.copy
import pl.jojczak.penmouses.screen.manual.components.ManualTopAppBar
import pl.jojczak.penmouses.screen.manual.components.appInfoComponent
import pl.jojczak.penmouses.screen.manual.components.birdHuntBanner
import pl.jojczak.penmouses.screen.manual.components.donateComponent
import pl.jojczak.penmouses.screen.manual.components.videoComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualScreen(
    navigationHazeState: HazeState,
    paddingValues: PaddingValues,
    manualDrawerState: DrawerState,
    viewState: ManualViewState
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val localHazeState = rememberHazeState()
    val localDensity = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val insets = WindowInsets.safeDrawing.asPaddingValues()
    var topAppBarHeight by remember { mutableStateOf(TopAppBarExpandedHeight) }

    BackHandler(manualDrawerState.isOpen) {
        scope.launch { manualDrawerState.close() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            contentPadding = paddingValues
                .copy(top = topAppBarHeight)
                .add(
                    start = insets.calculateStartPadding(layoutDirection),
                    end = insets.calculateEndPadding(layoutDirection)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .hazeSource(state = navigationHazeState)
                .hazeSource(state = localHazeState),
        ) {
            handlePage(
                ctx = ctx,
                pageType = viewState.page,
                markdownNode = viewState.markdownNode
            )
        }

        ManualTopAppBar(
            manualPage = viewState.page,
            onMenuIconClicked = {
                scope.launch { manualDrawerState.apply { if (isClosed) open() else close() } }
            },
            modifier = Modifier
                .hazeEffect(
                    state = localHazeState,
                    style = hazeUltraThinSurface()
                )
                .onGloballyPositioned {
                    with(localDensity) { topAppBarHeight = it.size.height.toDp() }
                },
        )
    }
}

private fun LazyListScope.handlePage(
    ctx: Context,
    pageType: ManualPageType,
    markdownNode: AstNode
) {
    for (feature in manualPageData.getValue(pageType).featuresBefore) handlePageFeature(
        ctx = ctx,
        feature = feature
    )
    manualPage(markdownNode = markdownNode)
    for (feature in manualPageData.getValue(pageType).featuresAfter) handlePageFeature(
        ctx = ctx,
        feature = feature
    )
}

private fun LazyListScope.handlePageFeature(
    ctx: Context,
    feature: ManualPageFeature
) = when (feature) {
    ManualPageFeature.AppInfo -> {
        appInfoComponent(ctx)
    }

    ManualPageFeature.Banners -> {
        donateComponent()
        birdHuntBanner()
    }

    is ManualPageFeature.Video -> {
        videoComponent(feature.uri)
    }
}

private fun LazyListScope.manualPage(markdownNode: AstNode) = item {
    PenMouseSMarkdown(
        astNode = markdownNode,
        modifier = Modifier
            .fillMaxSize()
            .padding(pad_l)
    )
}

@Preview
@Composable
private fun ManualScreenPreview() {
    PenMouseSDevicePreview {
        ManualScreen(
            navigationHazeState = rememberHazeState(),
            paddingValues = it,
            manualDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
            viewState = ManualViewState()
        )
    }
}