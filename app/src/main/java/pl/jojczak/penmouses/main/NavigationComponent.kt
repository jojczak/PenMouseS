package pl.jojczak.penmouses.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.rememberHazeState
import pl.jojczak.penmouses.core.ui.R as coreR
import pl.jojczak.penmouses.screen.home.HomeScreen
import pl.jojczak.penmouses.screen.settings.SettingsScreen
import pl.jojczak.penmouses.core.ui.theme.PenMouseSTheme
import pl.jojczak.penmouses.core.ui.theme.hazeUltraThinSurface

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun PenMouseSContent() {
    var topBar by remember { mutableStateOf<(@Composable (HazeState) -> Unit)>({}) }
    var isTopBarVisible by remember { mutableStateOf(true) }
    val navController = rememberNavController()
    val hazeState = rememberHazeState()

    val setTopBar: ((@Composable (HazeState) -> Unit)?) -> Unit = { component ->
        component?.let {
            topBar = it
            isTopBarVisible = true
        } ?: run {
            isTopBarVisible = false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBarContainer(
                isTopBarVisible = isTopBarVisible,
                hazeState = hazeState,
                content = topBar
            )
        },
        bottomBar = {
            PenMouseSBottomBar(
                navController = navController,
                modifier = Modifier.hazeEffect(
                    state = hazeState,
                    style = hazeUltraThinSurface()
                )
            )
        },
        content = { paddingValues ->
            val animatedTopPadding by animateDpAsState(
                targetValue = if (isTopBarVisible) paddingValues.calculateTopPadding() else WindowInsets.statusBars.asPaddingValues()
                    .calculateTopPadding(),
                label = "topPaddingAnimation"
            )

            PenMouseSNavigation(
                navController = navController,
                paddingValues = PaddingValues(
                    top = animatedTopPadding,
                    bottom = paddingValues.calculateBottomPadding(),
                    start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                    end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                ),
                setTopBar = setTopBar,
                modifier = Modifier.hazeSource(state = hazeState)
            )
        }
    )
}

@Composable
private fun PenMouseSNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    setTopBar: ((@Composable (HazeState) -> Unit)?) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screens.HOME.name,
        modifier = modifier
    ) {
        composable(route = Screens.HOME.name) {
            HomeScreen(
                paddingValues = paddingValues,
                setTopBar = setTopBar
            )
        }
        composable(route = Screens.SETTINGS.name) {
            SettingsScreen(
                paddingValues = paddingValues,
                setTopBar = setTopBar
            )
        }
    }
}

@Composable
private fun PenMouseSBottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        windowInsets = NavigationBarDefaults.windowInsets,
        containerColor = Color.Transparent,
        modifier = modifier
    ) {
        Screens.entries.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.name,
                onClick = {
                    navController.navigate(screen.name) {
                        launchSingleTop = true
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                enabled = screen != Screens.MANUAL,
                icon = { NavIcon(screen = screen) },
                label = { NavLabel(screen = screen) }
            )
        }
    }
}

@Composable
private fun TopBarContainer(
    isTopBarVisible: Boolean,
    hazeState: HazeState,
    content: @Composable (HazeState) -> Unit
) {
    AnimatedVisibility(
        visible = isTopBarVisible,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> -fullHeight }
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> -fullHeight }
        )
    ) {
        content(hazeState)
    }
}

@Composable
private fun NavIcon(screen: Screens) {
    Icon(
        painter = painterResource(screen.iconResId),
        contentDescription = stringResource(screen.titleResId)
    )
}

@Composable
private fun NavLabel(screen: Screens) {
    Text(
        text = stringResource(screen.titleResId)
    )
}

enum class Screens(
    @param:StringRes val titleResId: Int,
    @param:DrawableRes val iconResId: Int
) {
    HOME(
        titleResId = coreR.string.screen_home,
        iconResId = coreR.drawable.stylus_24px
    ),
    MANUAL(
        titleResId = coreR.string.screen_manual,
        iconResId = coreR.drawable.menu_book_24px
    ),
    SETTINGS(
        titleResId = coreR.string.screen_settings,
        iconResId = coreR.drawable.settings_24px
    )
}

@Composable
@Preview
private fun PenMouseSPreview() {
    PenMouseSTheme {
        PenMouseSContent()
    }
}