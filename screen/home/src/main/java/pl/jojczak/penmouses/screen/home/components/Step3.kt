package pl.jojczak.penmouses.screen.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import pl.jojczak.penmouses.core.common.types.ManualPageType
import pl.jojczak.penmouses.core.ui.theme.PenMouseSPreview
import pl.jojczak.penmouses.core.ui.theme.pad_l
import pl.jojczak.penmouses.core.ui.theme.pad_m
import pl.jojczak.penmouses.core.ui.theme.pad_xs
import pl.jojczak.penmouses.screen.home.R
import pl.jojczak.penmouses.screen.home.openAccessibilitySettings

internal fun LazyListScope.step3(
    isAccessibilityEnabled: Boolean,
    showManualPageClicked: (ManualPageType) -> Unit,
) = item {
    val context = LocalContext.current

    StepSurface {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                StepHeader(
                    stepText = R.string.home_steps_3,
                    showManualPageClicked = { showManualPageClicked(ManualPageType.PreparationStep3) }
                )
                StepHeaderLink(
                    linkText = R.string.home_steps_3_settings,
                    linkCallback = { openAccessibilitySettings(context) }
                )
            }
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(pad_l),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.home_steps_3_des),
                    modifier = Modifier
                        .padding(start = pad_m, bottom = pad_m)
                        .weight(1f)
                )
                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                    Switch(
                        checked = isAccessibilityEnabled,
                        enabled = !isAccessibilityEnabled,
                        onCheckedChange = { openAccessibilitySettings(context) },
                        modifier = Modifier.padding(top = pad_xs, end = pad_m)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Step3Preview() {
    PenMouseSPreview {
        LazyColumn {
            step3(
                isAccessibilityEnabled = true,
                showManualPageClicked = {}
            )
        }
    }
}
