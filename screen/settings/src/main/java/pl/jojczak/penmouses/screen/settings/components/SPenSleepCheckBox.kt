package pl.jojczak.penmouses.screen.settings.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import pl.jojczak.penmouses.core.ui.theme.PenMouseSTheme
import pl.jojczak.penmouses.core.ui.theme.pad_m
import pl.jojczak.penmouses.core.ui.theme.radius_m
import pl.jojczak.penmouses.core.ui.R as coreR

internal fun LazyListScope.sPenSleepCheckBox(
    sPenSleepEnabled: Boolean,
    onSPenSleepEnabledChange: (Boolean) -> Unit
) = item {
    Row(
        modifier = Modifier
            .padding(pad_m)
            .clip(RoundedCornerShape(radius_m))
            .clickable {
                onSPenSleepEnabledChange(!sPenSleepEnabled)
            }
    ) {
        Checkbox(
            checked = sPenSleepEnabled,
            onCheckedChange = onSPenSleepEnabledChange
        )
        Column {
            Text(
                text = stringResource(coreR.string.settings_s_pen_sleep_info)
            )
            Crossfade(sPenSleepEnabled) {
                if (it) {
                    Text(
                        text = stringResource(coreR.string.settings_s_pen_sleep_enabled_info),
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.bodySmall,
                    )
                } else {
                    Text(
                        text = stringResource(coreR.string.settings_s_pen_sleep_disabled_warning),
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun SPenSleepCheckBoxComponentPreview() {
    PenMouseSTheme {
        Surface {
            LazyColumn {
                sPenSleepCheckBox(
                    sPenSleepEnabled = true,
                    onSPenSleepEnabledChange = {}
                )
            }
        }
    }
}