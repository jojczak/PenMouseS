package pl.jojczak.penmouses.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

val LocalDynamicLightColors = compositionLocalOf { lightColorScheme() }

@Composable
fun PenMouseSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val lightScheme = dynamicLightColorScheme(context)

    val colorScheme = if (darkTheme) {
        dynamicDarkColorScheme(context)
    } else {
        dynamicLightColorScheme(context)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = {
            CompositionLocalProvider(LocalDynamicLightColors provides lightScheme) {
                content()
            }
        }
    )
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun hazeUltraThinSurface(): HazeStyle {
    return HazeMaterials.ultraThin(
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(elevation_1)
    )
}

@Composable
fun PenMouseSPreview(
    content: @Composable () -> Unit
) {
    PenMouseSTheme {
        Surface {
            content()
        }
    }
}

@Composable
fun PenMouseSDevicePreview(
    content: @Composable (PaddingValues) -> Unit
) {
    PenMouseSTheme {
        Scaffold {
            content(it)
        }
    }
}