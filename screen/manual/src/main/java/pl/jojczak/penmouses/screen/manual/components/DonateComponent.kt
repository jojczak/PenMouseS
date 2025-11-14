package pl.jojczak.penmouses.screen.manual.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.jojczak.penmouses.core.ui.theme.LINK_ICON_SIZE
import pl.jojczak.penmouses.core.ui.theme.PenMouseSTheme
import pl.jojczak.penmouses.core.ui.theme.pad_l
import pl.jojczak.penmouses.core.ui.theme.pad_m
import pl.jojczak.penmouses.core.ui.utils.openUrl
import pl.jojczak.penmouses.screen.manual.R
import pl.jojczak.penmouses.core.ui.R as coreR

internal fun LazyListScope.donateComponent() = item {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(horizontal = pad_l)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.manual_support_text),
            modifier = Modifier.weight(1f, fill = false),
        )
        Box(
            modifier = Modifier.padding(start = pad_m, top = pad_l, bottom = pad_l)
        ) {
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                FilledTonalButton(
                    onClick = {
                        Toast.makeText(context, R.string.manual_support_thanks, Toast.LENGTH_LONG)
                            .show()
                        openUrl(context, R.string.manual_support_url)
                    },
                ) {
                    Text(text = stringResource(R.string.manual_support_button))
                    Icon(
                        painter = painterResource(coreR.drawable.ic_open_in_new),
                        contentDescription = null,
                        modifier = Modifier.size(LINK_ICON_SIZE)
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun PreviewDonateComponent() {
    PenMouseSTheme {
        Surface {
            LazyColumn {
                donateComponent()
            }
        }
    }
}
