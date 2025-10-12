package pl.jojczak.penmouses.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.rememberHazeState
import pl.jojczak.penmouses.core.ui.theme.PenMouseSTheme
import pl.jojczak.penmouses.core.ui.theme.hazeUltraThinSurface
import pl.jojczak.penmouses.screen.home.HomeScreen
import pl.jojczak.penmouses.screen.settings.SettingsScreen

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
internal fun PenMouseSContent() {
    val navController = rememberNavController()
    val hazeState = rememberHazeState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
            PenMouseSNavigation(
                navController = navController,
                paddingValues = paddingValues,
                hazeState = hazeState
            )
        }
    )
}

@Composable
private fun PenMouseSNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues,
    hazeState: HazeState
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home,
        enterTransition = AnimatedContentTransitionScope<NavBackStackEntry>::enterAnimation,
        exitTransition = AnimatedContentTransitionScope<NavBackStackEntry>::exitAnimation,
        popEnterTransition = AnimatedContentTransitionScope<NavBackStackEntry>::enterAnimation,
        popExitTransition = AnimatedContentTransitionScope<NavBackStackEntry>::exitAnimation
    ) {
        composable<Screen.Home> {
            HomeScreen(
                paddingValues = paddingValues,
                hazeState = hazeState
            )
        }
        composable<Screen.Settings> {
            SettingsScreen(
                paddingValues = paddingValues,
                hazeState = hazeState
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
    val currentScreen = rememberScreen(navBackStackEntry?.destination?.route)

    NavigationBar(
        windowInsets = NavigationBarDefaults.windowInsets,
        containerColor = Color.Transparent,
        modifier = modifier
    ) {
        Screen.order.forEach { screen ->
            NavigationBarItem(
                selected = screen == currentScreen,
                onClick = {
                    navController.navigate(screen) {
                        launchSingleTop = true
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                enabled = screen != Screen.Manual,
                icon = { NavIcon(screen = screen) },
                label = { NavLabel(screen = screen) }
            )
        }
    }
}

@Composable
private fun NavIcon(screen: Screen) {
    Icon(
        painter = painterResource(screen.iconResId),
        contentDescription = stringResource(screen.titleResId)
    )
}

@Composable
private fun NavLabel(screen: Screen) {
    Text(
        text = stringResource(screen.titleResId)
    )
}

@Composable
private fun rememberScreen(currentRoute: String?) = remember(currentRoute) {
    Screen.findFromRoute(currentRoute)
}

@Composable
@Preview
private fun PenMouseSPreview() {
    PenMouseSTheme {
        PenMouseSContent()
    }
}