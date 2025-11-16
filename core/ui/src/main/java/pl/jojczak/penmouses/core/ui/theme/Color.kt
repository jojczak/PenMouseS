package pl.jojczak.penmouses.core.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ColorSchemePreview(colorScheme: ColorScheme) {
    val colorItems = listOf(
        "primary" to colorScheme.primary,
        "onPrimary" to colorScheme.onPrimary,
        "primaryContainer" to colorScheme.primaryContainer,
        "onPrimaryContainer" to colorScheme.onPrimaryContainer,
        "inversePrimary" to colorScheme.inversePrimary,

        "secondary" to colorScheme.secondary,
        "onSecondary" to colorScheme.onSecondary,
        "secondaryContainer" to colorScheme.secondaryContainer,
        "onSecondaryContainer" to colorScheme.onSecondaryContainer,

        "tertiary" to colorScheme.tertiary,
        "onTertiary" to colorScheme.onTertiary,
        "tertiaryContainer" to colorScheme.tertiaryContainer,
        "onTertiaryContainer" to colorScheme.onTertiaryContainer,

        "background" to colorScheme.background,
        "onBackground" to colorScheme.onBackground,

        "surface" to colorScheme.surface,
        "onSurface" to colorScheme.onSurface,
        "surfaceVariant" to colorScheme.surfaceVariant,
        "onSurfaceVariant" to colorScheme.onSurfaceVariant,
        "surfaceTint" to colorScheme.surfaceTint,

        "inverseSurface" to colorScheme.inverseSurface,
        "inverseOnSurface" to colorScheme.inverseOnSurface,

        "error" to colorScheme.error,
        "onError" to colorScheme.onError,
        "errorContainer" to colorScheme.errorContainer,
        "onErrorContainer" to colorScheme.onErrorContainer,

        "outline" to colorScheme.outline,
        "outlineVariant" to colorScheme.outlineVariant,
        "scrim" to colorScheme.scrim
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "MaterialTheme Color Scheme",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        colorItems.forEach { (name, color) ->
            ColorRow(name = name, color = color)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ColorRow(name: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(color)
        )
        Text(
            text = "$name\n#${colorToHexString(color)}",
            modifier = Modifier.padding(start = 16.dp),
            style = TextStyle(fontSize = 14.sp),
            color = if (MaterialTheme.colorScheme.background.luminance() > 0.5f) Color.Black else Color.White
        )
    }
}

private fun colorToHexString(color: Color): String {
    return String.format(
        "%02X%02X%02X",
        (color.red * 255).toInt(),
        (color.green * 255).toInt(),
        (color.blue * 255).toInt()
    )
}

fun Color.luminance(): Float {
    val red = red
    val green = green
    val blue = blue
    return (0.2126f * red + 0.7152f * green + 0.0722f * blue)
}


@Preview(name = "Light Theme Colors", showBackground = true, widthDp = 380, heightDp = 2000)
@Composable
fun LightThemeColorSchemePreview() {
    PenMouseSTheme {
        Surface {
            ColorSchemePreview(MaterialTheme.colorScheme)
        }
    }
}

@Preview(name = "Dark Theme Colors", showBackground = true, widthDp = 380, heightDp = 2000)
@Composable
fun DarkThemeColorSchemePreview() {
    PenMouseSTheme(darkTheme = true) {
        Surface {
            ColorSchemePreview(MaterialTheme.colorScheme)
        }
    }
}