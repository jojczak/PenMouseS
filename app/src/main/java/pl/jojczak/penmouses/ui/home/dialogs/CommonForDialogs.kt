package pl.jojczak.penmouses.ui.home.dialogs

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.OptIn
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.SHOW_BUFFERING_NEVER
import pl.jojczak.penmouses.ui.theme.PLAYER_HEIGHT
import pl.jojczak.penmouses.ui.theme.PenMouseSTheme
import pl.jojczak.penmouses.ui.theme.radius_l

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebView(url: String) {
    val textColor = MaterialTheme.colorScheme.onBackground
    val textColorHex = String.format("#%06X", 0xFFFFFF and textColor.toArgb())

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                setBackgroundColor(Color.TRANSPARENT)

                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest
                    ): Boolean {
                        val intent = Intent(Intent.ACTION_VIEW, request.url)
                        context.startActivity(intent)
                        return true
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)

                        val js = """
                        document.body.style.color = '$textColorHex';
                    """.trimIndent()
                        view?.evaluateJavascript(js, null)
                    }
                }
                loadUrl(url)
            }
        }
    )
}

@OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun VideoPlayer(uri: Uri) {
    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
        }
    }

    AndroidView(
        factory = {
            PlayerView(context).apply {
                this.player = player
                useController = true
                setShowNextButton(false)
                setShowPreviousButton(false)
                setShowBuffering(SHOW_BUFFERING_NEVER)
            }
        },
        modifier = Modifier
            .clip(RoundedCornerShape(radius_l))
            .fillMaxWidth()
            .height(PLAYER_HEIGHT)
    )

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }
}

@Composable
fun DialogDismissButton(
    @StringRes textResId: Int,
    changeDialogState: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextButton(
            onClick = changeDialogState
        ) {
            Text(
                text = stringResource(textResId)
            )
        }
    }
}

@Composable
@Preview
private fun Step2DialogPreview() {
    PenMouseSTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Step3AccessibilityServicesDialog(
                showDialog = true
            )
        }
    }
}

@Composable
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
private fun MoreInfoDialogPreview() {
    PenMouseSTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Step3AccessibilityServicesDialog(
                showDialog = true
            )
        }
    }
}