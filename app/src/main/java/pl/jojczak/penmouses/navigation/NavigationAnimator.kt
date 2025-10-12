package pl.jojczak.penmouses.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry

internal fun AnimatedContentTransitionScope<NavBackStackEntry>.enterAnimation(): EnterTransition {
    val fromIndex = Screen.indexOfRoute(initialState.destination.route)
    val toIndex = Screen.indexOfRoute(targetState.destination.route)

    return if (toIndex > fromIndex) {
        slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth })
    } else {
        slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth })
    }
}

internal fun AnimatedContentTransitionScope<NavBackStackEntry>.exitAnimation(): ExitTransition {
    val fromIndex = Screen.indexOfRoute(initialState.destination.route)
    val toIndex = Screen.indexOfRoute(targetState.destination.route)

    return if (toIndex > fromIndex) {
        slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth })
    } else {
        slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
    }
}