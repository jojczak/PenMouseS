package pl.jojczak.penmouses.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry

internal fun AnimatedContentTransitionScope<NavBackStackEntry>.enterAnimation(): EnterTransition {
    return slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Up,
        animationSpec = tween(400)
    )
}

internal fun AnimatedContentTransitionScope<NavBackStackEntry>.exitAnimation(): ExitTransition {
    return slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Down,
        animationSpec = tween(400)
    )
}
