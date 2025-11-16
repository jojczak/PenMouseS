package pl.jojczak.penmouses.screen.manual.components

import android.net.Uri
import android.util.Log
import android.util.Xml
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.SHOW_BUFFERING_NEVER
import pl.jojczak.penmouses.core.ui.theme.PLAYER_HEIGHT
import pl.jojczak.penmouses.core.ui.theme.pad_l
import pl.jojczak.penmouses.core.ui.theme.radius_l
import pl.jojczak.penmouses.screen.manual.R

@OptIn(UnstableApi::class)
fun LazyListScope.videoComponent(uri: Uri) = item(uri) {
    val context = LocalContext.current
    val resources = LocalResources.current

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
        }
    }

    val attrs = remember {
        resources.getXml(R.xml.player_view).let {
            try {
                it.next()
                it.nextTag()
            } catch (e: Exception) {
                Log.e("VideoComponent", e.toString(), e)
            }
            Xml.asAttributeSet(it)
        }
    }

    AndroidView(
        factory = {
            PlayerView(context, attrs).apply {
                this.player = player
                useController = true
                setShowNextButton(false)
                setShowPreviousButton(false)
                setShowBuffering(SHOW_BUFFERING_NEVER)
            }
        },
        modifier = Modifier
            .padding(horizontal = pad_l)
            .padding(bottom = pad_l)
            .clip(RoundedCornerShape(radius_l))
            .fillMaxWidth()
            .heightIn(max = PLAYER_HEIGHT)
    )

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }
}
