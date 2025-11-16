package pl.jojczak.penmouses.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.jojczak.penmouses.core.common.types.ManualPageType
import pl.jojczak.penmouses.core.ui.theme.PenMouseSTheme
import pl.jojczak.penmouses.core.ui.theme.hazeUltraThinSurface
import pl.jojczak.penmouses.screen.home.HomeScreen
import pl.jojczak.penmouses.screen.manual.ManualScreen
import pl.jojczak.penmouses.screen.manual.ManualUserAction
import pl.jojczak.penmouses.screen.manual.ManualViewModel
import pl.jojczak.penmouses.screen.manual.ManualViewState
import pl.jojczak.penmouses.screen.manual.components.ManualDrawer
import pl.jojczak.penmouses.screen.settings.SettingsScreen

private const val MANUAL_DRAWER_DELAY_AFTER_PAGE_CHANGE_MS = 150L

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
internal fun PenMouseSContentWithManualDrawer() {
    val scope = rememberCoroutineScope()
    val isDarkMode = isSystemInDarkTheme()
    val manualHazeState = rememberHazeState()
    val manualDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val manualViewModel: ManualViewModel = hiltViewModel()
    val manualViewState by manualViewModel.state.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = rememberScreen(navBackStackEntry?.destination?.route)

    LaunchedEffect(currentScreen) {
        if (currentScreen != Screen.Manual) {
            manualDrawerState.close()
        }
    }

    LaunchedEffect(isDarkMode) {
        manualViewModel.onViewAction(
            viewAction = ManualUserAction.ChangePage(
                page = manualViewState.page,
                isDarkMode = isDarkMode
            )
        )
    }

    ModalNavigationDrawer(
        drawerState = manualDrawerState,
        gesturesEnabled = currentScreen == Screen.Manual,
        drawerContent = {
            ManualDrawer(
                manualHazeState = manualHazeState,
                currentPageType = manualViewState.page,
                onPageClicked = {
                    manualViewModel.onViewAction(
                        viewAction = ManualUserAction.ChangePage(
                            page = it,
                            isDarkMode = isDarkMode
                        )
                    )
                    scope.launch { delay(MANUAL_DRAWER_DELAY_AFTER_PAGE_CHANGE_MS); manualDrawerState.close() }
                }
            )
        }
    ) {
        PenMouseSScaffold(
            navController = navController,
            currentScreen = currentScreen,
            manualDrawerState = manualDrawerState,
            manualViewState = manualViewState,
            modifier = Modifier.hazeSource(state = manualHazeState),
            showManualPageClicked = {
                manualViewModel.onViewAction(
                    viewAction = ManualUserAction.ChangePage(
                        page = it,
                        isDarkMode = isDarkMode
                    )
                )
                navController.navigateTo(Screen.Manual)
            }
        )
    }
}

@Composable
private fun PenMouseSScaffold(
    navController: NavHostController,
    currentScreen: Screen,
    manualDrawerState: DrawerState,
    manualViewState: ManualViewState,
    modifier: Modifier = Modifier,
    showManualPageClicked: (ManualPageType) -> Unit,
) {
    val navigationHazeState = rememberHazeState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            PenMouseSBottomBar(
                navController = navController,
                currentScreen = currentScreen,
                modifier = Modifier.hazeEffect(
                    state = navigationHazeState,
                    style = hazeUltraThinSurface()
                )
            )
        },
        content = { paddingValues ->
            PenMouseSNavigation(
                navController = navController,
                paddingValues = paddingValues,
                navigationHazeState = navigationHazeState,
                manualDrawerState = manualDrawerState,
                manualViewState = manualViewState,
                showManualPageClicked = showManualPageClicked
            )
        }
    )
}

@Composable
private fun PenMouseSNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues,
    navigationHazeState: HazeState,
    manualDrawerState: DrawerState,
    manualViewState: ManualViewState,
    showManualPageClicked: (ManualPageType) -> Unit,
) = NavHost(
    navController = navController,
    startDestination = Screen.Home,
    enterTransition = AnimatedContentTransitionScope<NavBackStackEntry>::enterAnimation,
    exitTransition = { ExitTransition.None },
    popEnterTransition = { EnterTransition.None },
    popExitTransition = AnimatedContentTransitionScope<NavBackStackEntry>::exitAnimation
) {
    composable<Screen.Home> {
        HomeScreen(
            paddingValues = paddingValues,
            navigationHazeState = navigationHazeState,
            showManualPageClicked = showManualPageClicked
        )
    }
    composable<Screen.Manual> {
        ManualScreen(
            paddingValues = paddingValues,
            navigationHazeState = navigationHazeState,
            manualDrawerState = manualDrawerState,
            viewState = manualViewState
        )
    }
    composable<Screen.Settings> {
        SettingsScreen(
            paddingValues = paddingValues,
            navigationHazeState = navigationHazeState
        )
    }
}

@Composable
private fun PenMouseSBottomBar(
    navController: NavHostController,
    currentScreen: Screen,
    modifier: Modifier = Modifier
) = NavigationBar(
    windowInsets = NavigationBarDefaults.windowInsets,
    containerColor = Color.Transparent,
    modifier = modifier
) {
    Screen.order.forEach { screen ->
        NavigationBarItem(
            selected = screen == currentScreen,
            onClick = { navController.navigateTo(screen) },
            icon = { NavIcon(screen = screen) },
            label = { Text(text = stringResource(screen.titleResId)) }
        )
    }
}

@Composable
private fun NavIcon(screen: Screen) = Icon(
    painter = painterResource(screen.iconResId),
    contentDescription = stringResource(screen.titleResId)
)

@Composable
private fun rememberScreen(currentRoute: String?) = remember(currentRoute) {
    Screen.findFromRoute(currentRoute)
}

private fun NavController.navigateTo(screen: Any) = navigate(screen) {
    launchSingleTop = true
    popUpTo(graph.findStartDestination().id) { saveState = true }
    restoreState = true
}

@Composable
@Preview
private fun PenMouseSPreview() {
    PenMouseSTheme {
        PenMouseSContentWithManualDrawer()
    }
}