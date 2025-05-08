package pl.jojczak.penmouses.ui.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import pl.jojczak.penmouses.R
import pl.jojczak.penmouses.ui.home.HomeScreen
import pl.jojczak.penmouses.ui.settings.SettingsScreen
import pl.jojczak.penmouses.ui.theme.PenMouseSTheme
import pl.jojczak.penmouses.ui.theme.elevation_1

@Composable
fun PenMouseSContent(
    navController: NavHostController = rememberNavController(),
) {
    val hazeState = rememberHazeState()

    Scaffold(
        bottomBar = {
            PenMouseSNavBar(
                navController = navController,
                hazeState = hazeState
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { paddings ->
        Surface {
            PenMouseSNavHost(
                navController = navController,
                paddingValues = paddings,
                hazeState = hazeState
            )
        }
    }
}

@Composable
private fun PenMouseSNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    hazeState: HazeState
) {
    NavHost(
        navController = navController,
        startDestination = Screens.HOME.name,
        modifier = Modifier.hazeSource(hazeState)
    ) {
        composable(Screens.HOME.name) {
            HomeScreen(
                paddingValues = paddingValues
            )
        }
        composable(Screens.SETTINGS.name) {
            SettingsScreen(
                paddingValues = paddingValues
            )
        }
    }
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
private fun PenMouseSNavBar(
    navController: NavHostController,
    hazeState: HazeState
) {
    NavigationBar(
        containerColor = Color.Transparent,
        modifier = Modifier.hazeEffect(
            state = hazeState,
            style = HazeMaterials.ultraThin(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(elevation_1)
            )
        )
    ) {
        val currentBackStackEntry = navController.currentBackStackEntryAsState().value
        val currentRoute = currentBackStackEntry?.destination?.route

        Screens.entries.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.name,
                onClick = {
                    if (currentRoute == screen.name) return@NavigationBarItem
                    navController.navigate(screen.name) {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId)
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(screen.iconResId),
                        contentDescription = stringResource(screen.titleResId)
                    )
                },
                label = {
                    Text(text = stringResource(screen.titleResId))
                }
            )
        }
    }
}

enum class Screens(
    @StringRes val titleResId: Int,
    @DrawableRes val iconResId: Int
) {
    HOME(
        titleResId = R.string.screen_home,
        iconResId = R.drawable.stylus_24px
    ),
    SETTINGS(
        titleResId = R.string.screen_settings,
        iconResId = R.drawable.settings_24px
    )
}

@Composable
@Preview
private fun PenMouseSContentPreview() {
    PenMouseSTheme {
        PenMouseSContent()
    }
}