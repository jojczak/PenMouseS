package pl.jojczak.penmouses.core.ui.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PaddingValues.copy(
    start: Dp = calculateStartPadding(LocalLayoutDirection.current),
    top: Dp = calculateTopPadding(),
    end: Dp = calculateEndPadding(LocalLayoutDirection.current),
    bottom: Dp = calculateBottomPadding()
) = PaddingValues(start, top, end, bottom)

@Composable
fun PaddingValues.add(value: Dp) = PaddingValues(
    start = calculateStartPadding(LocalLayoutDirection.current) + value,
    top = calculateTopPadding() + value,
    end = calculateEndPadding(LocalLayoutDirection.current) + value,
    bottom = calculateBottomPadding() + value
)

@Composable
fun PaddingValues.add(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp
) = PaddingValues(
    start = calculateStartPadding(LocalLayoutDirection.current) + start,
    top = calculateTopPadding() + top,
    end = calculateEndPadding(LocalLayoutDirection.current) + end,
    bottom = calculateBottomPadding() + bottom
)