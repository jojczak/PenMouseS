package pl.jojczak.penmouses.ui.settings.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import pl.jojczak.penmouses.R
import pl.jojczak.penmouses.ui.theme.LINK_ICON_SIZE
import pl.jojczak.penmouses.ui.theme.PenMouseSTheme
import pl.jojczak.penmouses.ui.theme.pad_m
import pl.jojczak.penmouses.ui.theme.pad_s
import pl.jojczak.penmouses.ui.theme.pad_xl
import pl.jojczak.penmouses.ui.theme.pad_xs
import pl.jojczak.penmouses.ui.theme.radius_m
import pl.jojczak.penmouses.utils.openUrl

@Composable
fun BirdHuntBanner() {
    val context = LocalContext.current
    var bannerHeight by remember { mutableIntStateOf(0) }
    val hazeState = rememberHazeState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { bannerHeight = it.size.height }
    ) {
        Image(
            painter = painterResource(R.drawable.birdhunt_banner),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .height(with(LocalDensity.current) { bannerHeight.toDp() })
                .hazeSource(state = hazeState)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = pad_m, horizontal = pad_xl)
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(radius_m))
                    .hazeEffect(
                        state = hazeState,
                        style = HazeStyle(
                            backgroundColor = MaterialTheme.colorScheme.background,
                            tint = HazeTint(MaterialTheme.colorScheme.background.copy(alpha = 0.4f)),
                            blurRadius = 3.dp,
                            noiseFactor = 0f
                        )
                    )
                    .padding(vertical = pad_xs, horizontal = pad_s)
            ) {
                Text(
                    text = stringResource(R.string.settings_bird_hunt_check),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 3.dp)
                )
                Column {
                    Text(
                        text = stringResource(R.string.settings_bird_hunt),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = stringResource(R.string.settings_bird_hunt_desc),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }

            FilledTonalButton(
                onClick = { openUrl(context, R.string.settings_bird_hunt_url) },
            ) {
                Text(text = stringResource(R.string.settings_bird_hunt_button))
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
@Preview
private fun BirdHuntBannerPreviewLight() {
    PenMouseSTheme {
        Surface {
            BirdHuntBanner()
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun BirdHuntBannerPreviewNight() {
    PenMouseSTheme {
        Surface {
            BirdHuntBanner()
        }
    }
}