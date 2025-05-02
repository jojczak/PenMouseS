package pl.jojczak.penmouses.ui.home

import android.content.res.Configuration
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.SHOW_BUFFERING_NEVER
import pl.jojczak.penmouses.R
import pl.jojczak.penmouses.ui.common.MoreInfoDialog
import pl.jojczak.penmouses.ui.theme.LINK_ICON_SIZE
import pl.jojczak.penmouses.ui.theme.PLAYER_HEIGHT
import pl.jojczak.penmouses.ui.theme.PenMouseSTheme
import pl.jojczak.penmouses.ui.theme.pad_xl
import pl.jojczak.penmouses.ui.theme.radius_l

@Composable
fun Step1Dialog(
    showDialog: Boolean,
    changeDialogState: (Int, Boolean) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current

    MoreInfoDialog(
        titleResId = R.string.home_steps_1_des,
        showDialog = showDialog,
        dialogId = 1,
        changeDialogState = changeDialogState,
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(pad_xl)
            ) {
                Text(
                    text = stringResource(R.string.home_steps_1_dialog_content),
                    textAlign = TextAlign.Justify
                )
                VideoPlayer(
                    uri = STEP_1_VIDEO.toUri(),
                    modifier = Modifier
                        .clip(RoundedCornerShape(radius_l))
                        .fillMaxWidth()
                        .height(PLAYER_HEIGHT)
                )
            }

        }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = { changeDialogState(1, false) }
            ) {
                Text(
                    text = stringResource(R.string.home_steps_dialog_dismiss)
                )
            }
            TextButton(
                onClick = {
                    openSettings(context)
                    changeDialogState(1, false)
                },
            ) {
                Text(
                    text = stringResource(R.string.home_steps_1_settings) + " "
                )
                Icon(
                    painter = painterResource(R.drawable.open_in_new_24px),
                    contentDescription = null,
                    modifier = Modifier.size(LINK_ICON_SIZE)
                )
            }
        }
    }
}

@Composable
fun Step2Dialog(
    showDialog: Boolean,
    changeDialogState: (Int, Boolean) -> Unit = { _, _ -> },
) {
    MoreInfoDialog(
        titleResId = R.string.home_steps_2_dialog_title,
        showDialog = showDialog,
        dialogId = 2,
        changeDialogState = changeDialogState,
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(pad_xl)
            ) {
                Text(
                    text = stringResource(R.string.home_steps_2_dialog_content),
                    textAlign = TextAlign.Justify
                )
                VideoPlayer(
                    uri = STEP_2_VIDEO.toUri(),
                    modifier = Modifier
                        .clip(RoundedCornerShape(radius_l))
                        .fillMaxWidth()
                        .height(PLAYER_HEIGHT)
                )
            }
        }
    ) {
        TextButton(
            onClick = { changeDialogState(2, false) }
        ) {
            Text(
                text = stringResource(R.string.home_steps_dialog_dismiss)
            )
        }
    }
}

@Composable
fun Step3Dialog(
    showDialog: Boolean,
    changeDialogState: (Int, Boolean) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current

    MoreInfoDialog(
        titleResId = R.string.home_steps_3_des,
        showDialog = showDialog,
        dialogId = 3,
        changeDialogState = changeDialogState,
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(pad_xl)
            ) {
                Text(
                    text = stringResource(R.string.home_steps_3_dialog_content),
                    textAlign = TextAlign.Justify
                )
                Text(
                    text = stringResource(R.string.home_steps_3_dialog_content2),
                    textAlign = TextAlign.Justify
                )
                VideoPlayer(
                    uri = STEP_3_VIDEO.toUri(),
                    modifier = Modifier
                        .clip(RoundedCornerShape(radius_l))
                        .fillMaxWidth()
                        .height(PLAYER_HEIGHT)
                )
            }
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = { changeDialogState(3, false) }
            ) {
                Text(
                    text = stringResource(R.string.home_steps_dialog_dismiss)
                )
            }
            TextButton(
                onClick = {
                    openAccessibilitySettings(context)
                    changeDialogState(3, false)
                },
            ) {
                Text(
                    text = stringResource(R.string.home_steps_3_settings) + " "
                )
                Icon(
                    painter = painterResource(R.drawable.open_in_new_24px),
                    contentDescription = null,
                    modifier = Modifier.size(LINK_ICON_SIZE)
                )
            }
        }
    }
}

@OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
private fun VideoPlayer(uri: Uri, modifier: Modifier = Modifier) {
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
        modifier = modifier
    )

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }
}

private const val STEP_1_VIDEO = "asset:///manual_videos/step1.mp4"
private const val STEP_2_VIDEO = "asset:///manual_videos/step2.mp4"
private const val STEP_3_VIDEO = "asset:///manual_videos/step3.mp4"

@Composable
@Preview
private fun Step2DialogPreview() {
    PenMouseSTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Step3Dialog(
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
            Step1Dialog(
                showDialog = true
            )
        }
    }
}