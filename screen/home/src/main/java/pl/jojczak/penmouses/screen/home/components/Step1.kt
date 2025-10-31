package pl.jojczak.penmouses.screen.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import pl.jojczak.penmouses.core.common.types.ManualPageType
import pl.jojczak.penmouses.core.ui.components.TextButton
import pl.jojczak.penmouses.core.ui.theme.PenMouseSPreview
import pl.jojczak.penmouses.core.ui.theme.pad_m
import pl.jojczak.penmouses.screen.home.R
import pl.jojczak.penmouses.screen.home.openSettings

internal fun LazyListScope.step1(
    showManualPageClicked: (ManualPageType) -> Unit,
) = item {
    val context = LocalContext.current

    StepSurface {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                StepHeader(
                    stepText = R.string.home_steps_1,
                    showManualPageClicked = { showManualPageClicked(ManualPageType.PreparationStep1) }
                )
                Text(
                    text = stringResource(R.string.home_steps_1_des),
                    modifier = Modifier.padding(start = pad_m, bottom = pad_m)
                )
            }
            TextButton(
                stringRes = R.string.home_steps_1_settings,
                onClick = { openSettings(context) },
            )
        }
    }
}

@Preview
@Composable
private fun Step1Preview() {
    PenMouseSPreview {
        LazyColumn {
            step1(showManualPageClicked = {})
        }
    }
}