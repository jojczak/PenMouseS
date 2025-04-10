package pl.jojczak.penmouses.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

@Composable
fun getRedButtonColors() = if (isSystemInDarkTheme()) {
    ButtonColors(
        containerColor = Color(0xFFD32F2F),
        contentColor = Color.White,
        disabledContainerColor = Color(0xFF9B4C4C),
        disabledContentColor = Color.White
    )
} else {
    ButtonColors(
        containerColor = Color(0xFFB71C1C),
        contentColor = Color.White,
        disabledContainerColor = Color(0xFF774343),
        disabledContentColor = Color.White
    )
}