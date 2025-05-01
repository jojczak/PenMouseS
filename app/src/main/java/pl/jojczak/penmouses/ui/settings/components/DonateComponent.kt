package pl.jojczak.penmouses.ui.settings.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import pl.jojczak.penmouses.R
import pl.jojczak.penmouses.ui.theme.LINK_ICON_SIZE
import pl.jojczak.penmouses.ui.theme.PenMouseSTheme
import pl.jojczak.penmouses.ui.theme.pad_l
import pl.jojczak.penmouses.ui.theme.pad_xl
import pl.jojczak.penmouses.utils.openUrl

@Composable
fun DonateComponent() {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(vertical = pad_l, horizontal = pad_xl)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.home_support_text),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f, fill = false),
        )
        FilledTonalButton(
            onClick = {
                Toast.makeText(context, R.string.home_support_thanks, Toast.LENGTH_LONG).show()
                openUrl(context, R.string.home_support_url)
            },
        ) {
            Text(text = stringResource(R.string.home_support_button))
            Icon(
                painter = painterResource(R.drawable.open_in_new_24px),
                contentDescription = null,
                modifier = Modifier.size(LINK_ICON_SIZE)
            )
        }
    }
}

@Composable
@Preview
private fun PreviewDonateComponent() {
    PenMouseSTheme {
        Surface {
            DonateComponent()
        }
    }
}
