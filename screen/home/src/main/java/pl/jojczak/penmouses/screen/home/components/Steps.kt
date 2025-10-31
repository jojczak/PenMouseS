package pl.jojczak.penmouses.screen.home.components

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent.Event
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent.ModeStatus
import pl.jojczak.penmouses.core.common.types.ManualPageType
import pl.jojczak.penmouses.core.ui.components.TextButton
import pl.jojczak.penmouses.core.ui.theme.LINK_ICON_SIZE_SMALL
import pl.jojczak.penmouses.core.ui.theme.PenMouseSPreview
import pl.jojczak.penmouses.core.ui.theme.elevation_1
import pl.jojczak.penmouses.core.ui.theme.pad_m
import pl.jojczak.penmouses.core.ui.theme.pad_s
import pl.jojczak.penmouses.core.ui.theme.pad_xs
import pl.jojczak.penmouses.core.ui.theme.pad_xxs
import pl.jojczak.penmouses.core.ui.theme.radius_m
import pl.jojczak.penmouses.screen.home.R

internal fun LazyListScope.stepsContainer(
    serviceStatus: ModeStatus,
    isAccessibilityEnabled: Boolean,
    toggleService: (event: Event) -> Unit,
    showManualPageClicked: (ManualPageType) -> Unit,
) {
    step1(showManualPageClicked = showManualPageClicked)
    step2(showManualPageClicked = showManualPageClicked)
    step3(
        isAccessibilityEnabled = isAccessibilityEnabled,
        showManualPageClicked = showManualPageClicked
    )
    step4(
        serviceStatus = serviceStatus,
        isAccessibilityEnabled = isAccessibilityEnabled,
        toggleService = toggleService,
    )
}

@Composable
internal fun StepSurface(
    shape: RoundedCornerShape = RoundedCornerShape(radius_m),
    content: @Composable () -> Unit
) = Surface(
    tonalElevation = elevation_1,
    shape = shape,
    modifier = Modifier.fillMaxWidth(),
    content = content
)

@Composable
internal fun StepHeader(
    @StringRes stepText: Int,
    showManualPageClicked: (() -> Unit)? = null,
) = Row {
    Text(
        text = stringResource(id = stepText),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = pad_m, top = pad_s)
    )
    showManualPageClicked?.let {
        Text(
            text = stringResource(id = R.string.home_steps_bullet),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = pad_s)
        )
        Text(
            text = stringResource(id = R.string.home_steps_more),
            textDecoration = TextDecoration.Underline,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(start = pad_xxs, top = pad_xs)
                .clip(RoundedCornerShape(radius_m))
                .clickable { it() }
                .padding(all = pad_xs)
        )
    }
}

@Composable
internal fun StepHeaderLink(
    @StringRes linkText: Int,
    linkCallback: () -> Unit
) = TextButton(
    stringRes = linkText,
    textStyle = MaterialTheme.typography.bodySmall,
    onClick = linkCallback,
    iconSize = LINK_ICON_SIZE_SMALL,
    spacedBy = pad_xs,
    smallPad = PaddingValues(pad_xs, pad_xs, pad_xs, 0.dp),
    normalPad = PaddingValues(pad_s)
)

@Composable
@Preview
private fun StepsPreview() {
    PenMouseSPreview {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(pad_s)
        ) {
            stepsContainer(
                serviceStatus = ModeStatus.Loading,
                isAccessibilityEnabled = false,
                showManualPageClicked = { _ -> },
                toggleService = {}
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
private fun StepsPreviewDark() {
    PenMouseSPreview {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(pad_s)
        ) {
            stepsContainer(
                serviceStatus = ModeStatus.Off,
                isAccessibilityEnabled = true,
                showManualPageClicked = { _ -> },
                toggleService = {}
            )
        }
    }
}