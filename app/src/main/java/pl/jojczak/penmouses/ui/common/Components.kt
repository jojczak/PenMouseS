package pl.jojczak.penmouses.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pl.jojczak.penmouses.R
import pl.jojczak.penmouses.ui.theme.LINK_ICON_SIZE
import pl.jojczak.penmouses.ui.theme.PenMouseSPreview
import pl.jojczak.penmouses.ui.theme.pad_m
import pl.jojczak.penmouses.ui.theme.pad_s
import pl.jojczak.penmouses.ui.theme.radius_m

@Composable
fun TextButton(
    @StringRes stringRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    iconSize: Dp = LINK_ICON_SIZE,
    spacedBy: Dp = pad_s,
    smallPad: PaddingValues = PaddingValues(0.dp, 0.dp, 0.dp, 0.dp),
    normalPad: PaddingValues = PaddingValues(pad_m, pad_m, pad_m, pad_m),
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacedBy),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(smallPad)
            .clip(RoundedCornerShape(radius_m))
            .clickable { onClick() }
            .padding(normalPad)
    ) {
        Text(
            text = stringResource(id = stringRes),
            style = textStyle,
            color = MaterialTheme.colorScheme.primary
        )
        Icon(
            painter = painterResource(id = R.drawable.open_in_new_24px),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
@Preview
private fun TextButtonPreview() {
    PenMouseSPreview {
        TextButton(
            stringRes = R.string.app_name,
            onClick = { }
        )
    }
}