package pl.jojczak.penmouses.screen.settings.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import pl.jojczak.penmouses.core.ui.theme.LINK_ICON_SIZE

internal fun LazyListScope.horizontalDivider() = item {
    HorizontalDivider()
}

@Composable
internal fun PreferenceIcon(
    @DrawableRes iconId: Int
) = Icon(
    painter = painterResource(iconId),
    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
    contentDescription = null,
    modifier = Modifier.height(LINK_ICON_SIZE)
)
