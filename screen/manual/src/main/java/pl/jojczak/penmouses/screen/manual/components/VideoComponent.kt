package pl.jojczak.penmouses.screen.manual.components

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.ContentFrame
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.material3.buttons.PlayPauseButton
import androidx.media3.ui.compose.material3.buttons.SeekBackButton
import androidx.media3.ui.compose.material3.buttons.SeekForwardButton
import androidx.media3.ui.compose.material3.indicator.PositionAndDurationText
import androidx.media3.ui.compose.material3.indicator.ProgressSlider
import kotlinx.coroutines.delay
import pl.jojczak.penmouses.core.ui.theme.LocalDynamicLightColors
import pl.jojczak.penmouses.core.ui.theme.PLAYER_HEIGHT
import pl.jojczak.penmouses.core.ui.theme.pad_l
import pl.jojczak.penmouses.core.ui.theme.pad_m
import pl.jojczak.penmouses.core.ui.theme.radius_l
import kotlin.time.Duration.Companion.milliseconds

@OptIn(UnstableApi::class)
fun LazyListScope.videoComponent(uri: Uri) = item(uri) {
    val context = LocalContext.current
    var showControls by remember { mutableStateOf(true) }

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
        }
    }

    LaunchedEffect(showControls, player.isPlaying) {
        if (showControls && player.isPlaying) {
            delay(3000.milliseconds)
            showControls = false
        }
    }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    Box(
        modifier = Modifier
            .padding(horizontal = pad_l)
            .padding(bottom = pad_l)
            .clip(RoundedCornerShape(radius_l))
            .fillMaxWidth()
            .heightIn(max = PLAYER_HEIGHT)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { showControls = !showControls }
    ) {
        ContentFrame(
            player = player,
            surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
            modifier = Modifier.fillMaxSize()
        )
        AnimatedVisibility(visible = showControls, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LocalDynamicLightColors.current.onSurface.copy(alpha = 0.5f))
            ) {
                PlayerCenterButtons(player, modifier = Modifier.align(Alignment.Center))
                PlayerBottomButtons(player, modifier = Modifier.align(Alignment.BottomCenter))
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun PlayerCenterButtons(
    player: Player?,
    modifier: Modifier = Modifier
) = Row(
    horizontalArrangement = Arrangement.spacedBy(pad_m, Alignment.CenterHorizontally),
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
) {
    val modifier = Modifier
        .size(50.dp)
        .background(
            color = LocalDynamicLightColors.current.background.copy(alpha = 0.9f),
            shape = ButtonDefaults.shape,
        )

    val colors = IconButtonDefaults.iconButtonColors(
        contentColor = LocalDynamicLightColors.current.primary
    )

    SeekBackButton(player, modifier = modifier, colors = colors)
    PlayPauseButton(player, modifier = modifier, colors = colors)
    SeekForwardButton(player, modifier = modifier, colors = colors)
}

@OptIn(UnstableApi::class)
@Composable
private fun PlayerBottomButtons(
    player: Player?,
    modifier: Modifier = Modifier
) = Column(
    verticalArrangement = Arrangement.spacedBy(pad_m),
    modifier = modifier.padding(pad_l)
) {
    ProgressSlider(
        player = player,
        colors = SliderDefaults.colors(
            thumbColor = LocalDynamicLightColors.current.inversePrimary,
            activeTrackColor = LocalDynamicLightColors.current.inversePrimary,
            inactiveTrackColor = LocalDynamicLightColors.current.background
        )
    )
    PositionAndDurationText(player, color = LocalDynamicLightColors.current.background)
}
