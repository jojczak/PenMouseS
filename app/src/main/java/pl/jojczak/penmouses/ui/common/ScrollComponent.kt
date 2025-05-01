package pl.jojczak.penmouses.ui.common

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val GRADIENT_HEIGHT = 50.dp

@Composable
fun ScrollComponent(
    showDivider: Boolean,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    shadowColor: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(DIALOG_ELEVATION),
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()
    var maxScroll by remember { mutableIntStateOf(0) }
    val isAtTop by remember { derivedStateOf { scrollState.value == 0 } }
    val isAtBottom by remember { derivedStateOf { scrollState.value >= maxScroll } }

    LaunchedEffect(scrollState.maxValue) {
        maxScroll = scrollState.maxValue
    }

    Box {
        Box(
            modifier = Modifier
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = modifier
            ) {
                content()
            }
        }

        Crossfade(
            isAtTop,
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .align(Alignment.TopCenter)
        ) {
            if (!it) {
                Column {
                    if (showDivider) HorizontalDivider()
                    GetScrollGradient(top = true, shadowColor)
                }
            }
        }

        Crossfade(
            isAtBottom,
            modifier = Modifier
                .padding(bottom = paddingValues.calculateBottomPadding())
                .align(Alignment.BottomCenter)
        ) {
            if (!it) {
                Column {
                    GetScrollGradient(top = false, shadowColor)
                    if (showDivider) HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun GetScrollGradient(
    top: Boolean,
    shadowColor: Color
) {
    val colorsArray = listOf(
        shadowColor,
        Color.Transparent
    )

    val gradient = Brush.verticalGradient(
        colors = if (top) colorsArray else colorsArray.reversed()
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(GRADIENT_HEIGHT)
            .background(gradient)
    )
}