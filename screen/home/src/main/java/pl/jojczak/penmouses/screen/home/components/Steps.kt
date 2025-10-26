package pl.jojczak.penmouses.screen.home.components

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent.Event
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent.ModeStatus
import pl.jojczak.penmouses.core.common.types.ManualPageType
import pl.jojczak.penmouses.core.ui.components.TextButton
import pl.jojczak.penmouses.core.ui.theme.LINK_ICON_SIZE_SMALL
import pl.jojczak.penmouses.core.ui.theme.PenMouseSPreview
import pl.jojczak.penmouses.core.ui.theme.elevation_1
import pl.jojczak.penmouses.core.ui.theme.pad_l
import pl.jojczak.penmouses.core.ui.theme.pad_m
import pl.jojczak.penmouses.core.ui.theme.pad_s
import pl.jojczak.penmouses.core.ui.theme.pad_xs
import pl.jojczak.penmouses.core.ui.theme.pad_xxs
import pl.jojczak.penmouses.core.ui.theme.radius_m
import pl.jojczak.penmouses.screen.home.R
import pl.jojczak.penmouses.screen.home.modesComponentData
import pl.jojczak.penmouses.screen.home.openAccessibilitySettings
import pl.jojczak.penmouses.screen.home.openSettings

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
        toggleService = toggleService,
    )
}

private fun LazyListScope.step1(
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

private fun LazyListScope.step2(
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

private fun LazyListScope.step3(
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

private fun LazyListScope.step4(
    serviceStatus: ModeStatus,
    toggleService: (event: Event) -> Unit,
) = item {
    StepSurface(
        shape = RoundedCornerShape(
            topStart = radius_m,
            topEnd = radius_m,
            bottomStart = 32.dp,
            bottomEnd = 32.dp
        )
    ) {
        Column {
            StepHeader(stepText = R.string.home_steps_4)
            Column(
                verticalArrangement = Arrangement.spacedBy(space = pad_s),
                modifier = Modifier.padding(
                    start = pad_m,
                    top = pad_xs,
                    end = pad_m,
                    bottom = pad_m
                )
            ) {
                Text(
                    text = stringResource(R.string.home_steps_4_more),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Justify
                )
                Text("Select mode:")
                Step4SegmentedButtonsContainer(
                    serviceStatus = serviceStatus,
                    toggleService = toggleService
                )
            }
        }
    }
}

@Composable
private fun Step4SegmentedButtonsContainer(
    serviceStatus: ModeStatus,
    toggleService: (event: Event) -> Unit,
) = CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
    Box {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            modesComponentData.forEachIndexed { index, mode ->
                SegmentedButton(
                    onClick = { toggleService(Event.Start(mode.mode)) },
                    selected = mode.mode == serviceStatus,
                    enabled = serviceStatus != ModeStatus.Loading,
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = modesComponentData.size
                    ),
                    label = {
                        Text(text = stringResource(id = mode.labelId))
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = if (mode.mode == serviceStatus) mode.iconActiveId else mode.iconId),
                            contentDescription = null
                        )
                    }
                )
            }
        }
        if (serviceStatus == ModeStatus.Loading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(elevation = 2.dp)
                            .copy(alpha = 0.7f)
                    )
                    .padding(all = pad_s)
            ) {
                LinearProgressIndicator()
            }
        }
    }
}

@Composable
private fun StepSurface(
    shape: RoundedCornerShape = RoundedCornerShape(radius_m),
    content: @Composable () -> Unit
) = Surface(
    tonalElevation = elevation_1,
    shape = shape,
    modifier = Modifier.fillMaxWidth(),
    content = content
)

@Composable
private fun StepHeader(
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
private fun StepHeaderLink(
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