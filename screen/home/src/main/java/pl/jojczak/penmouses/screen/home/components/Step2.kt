package pl.jojczak.penmouses.screen.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import pl.jojczak.penmouses.core.common.types.ManualPageType
import pl.jojczak.penmouses.core.ui.theme.PenMouseSPreview
import pl.jojczak.penmouses.core.ui.theme.pad_m
import pl.jojczak.penmouses.screen.home.R

internal fun LazyListScope.step2(
    showManualPageClicked: (ManualPageType) -> Unit,
) = item {
    StepSurface {
        Column {
            StepHeader(
                stepText = R.string.home_steps_2,
                showManualPageClicked = { showManualPageClicked(ManualPageType.PreparationStep2) }
            )
            Text(
                text = stringResource(R.string.home_steps_2_des),
                modifier = Modifier.padding(start = pad_m, end = pad_m, bottom = pad_m)
            )
        }
    }
}

@Preview
@Composable
private fun Step2Preview() {
    PenMouseSPreview {
        LazyColumn {
            step2(showManualPageClicked = {})
        }
    }
}