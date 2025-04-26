package pl.jojczak.penmouses.ui.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import pl.jojczak.penmouses.R
import pl.jojczak.penmouses.ui.home.HomeScreen
import pl.jojczak.penmouses.ui.settings.SettingsScreen
import pl.jojczak.penmouses.ui.theme.PenMouseSTheme

@Composable
fun PenMouseSContent(
    navController: NavHostController = rememberNavController(),
) {
    Scaffold(
        bottomBar = { PenMouseSNavBar(navController) },
        contentWindowInsets = WindowInsets(0.dp),
    ) { paddings ->
        Surface(
            modifier = Modifier.padding(paddings)
        ) {
            PenMouseSNavHost(navController)
        }
    }
}

@Composable
private fun PenMouseSNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screens.HOME.name
    ) {
        composable(Screens.HOME.name) {
            HomeScreen()
        }
        composable(Screens.SETTINGS.name) {
            SettingsScreen()
        }
    }
}

@Composable
private fun PenMouseSNavBar(
    navController: NavHostController
) {
    NavigationBar {
        val currentBackStackEntry = navController.currentBackStackEntryAsState().value
        val currentRoute = currentBackStackEntry?.destination?.route

        Screens.entries.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.name,
                onClick = {
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