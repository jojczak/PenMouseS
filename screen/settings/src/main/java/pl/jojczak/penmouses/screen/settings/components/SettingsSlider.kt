package pl.jojczak.penmouses.screen.settings.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import pl.jojczak.penmouses.core.common.utils.PrefKey
import pl.jojczak.penmouses.core.ui.theme.pad_s
import pl.jojczak.penmouses.core.ui.theme.pad_xl
import kotlin.math.round

internal fun LazyListScope.settingsSlider(
    @StringRes text: Int,
    @StringRes textOnLastValue: Int? = null,
    value: Float,
    prefKey: PrefKey<Float>,
    onValueChange: (PrefKey<Float>, Float) -> Unit = { _, _ -> },
    onValueChangeFinished: (PrefKey<Float>, Float) -> Unit = { _, _ -> },
) = item {
    Column(
        verticalArrangement = Arrangement.spacedBy(pad_s),
        modifier = Modifier.padding(pad_xl)
    ) {
        Text(
            stringResource(
                textOnLastValue.takeIf { it != null && value == prefKey.range.endInclusive }
                    ?: text,
                round(value).toInt()
            )
        )
        var sliderValue by remember(value) { mutableFloatStateOf(value) }
        Slider(
            value = sliderValue,
            onValueChange = {
                val roundedValue = (round(it / prefKey.step)) * prefKey.step
                sliderValue = roundedValue
                onValueChange(prefKey, roundedValue)
            },
            valueRange = prefKey.range,
            onValueChangeFinished = { onValueChangeFinished(prefKey, sliderValue) },
        )
    }
}
