package pl.jojczak.penmouses.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent.Event
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent.ModeStatus
import pl.jojczak.penmouses.core.ui.theme.PenMouseSPreview
import pl.jojczak.penmouses.core.ui.theme.pad_m
import pl.jojczak.penmouses.core.ui.theme.pad_s
import pl.jojczak.penmouses.core.ui.theme.pad_xs
import pl.jojczak.penmouses.core.ui.theme.pad_xxl
import pl.jojczak.penmouses.core.ui.theme.radius_m
import pl.jojczak.penmouses.screen.home.R
import pl.jojczak.penmouses.screen.home.modesComponentData

fun LazyListScope.step4(
    serviceStatus: ModeStatus,
    isAccessibilityEnabled: Boolean,
    toggleService: (event: Event) -> Unit,
) = item {
    StepSurface(
        shape = RoundedCornerShape(
            topStart = radius_m,
            topEnd = radius_m,
            bottomStart = pad_xxl,
            bottomEnd = pad_xxl
        )
    ) {
        Box {
            MainContainer(
                serviceStatus = serviceStatus,
                toggleService = toggleService
            )
            if (!isAccessibilityEnabled) {
                BlockingOverlay()
            }
        }
    }
}

@Composable
private fun MainContainer(
    serviceStatus: ModeStatus,
    toggleService: (event: Event) -> Unit,
) = Column {
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
        SegmentedButtonsContainer(
            serviceStatus = serviceStatus,
            toggleService = toggleService
        )
    }
}

@Composable
private fun SegmentedButtonsContainer(
    serviceStatus: ModeStatus,
    toggleService: (event: Event) -> Unit,
) = Box {
    ModesSegmentedButtons(
        serviceStatus = serviceStatus,
        toggleService = toggleService
    )
    if (serviceStatus == ModeStatus.Loading) {
        BlockingOverlay {
            LinearProgressIndicator()
        }
    }
}

@Composable
private fun ModesSegmentedButtons(
    serviceStatus: ModeStatus,
    toggleService: (event: Event) -> Unit,
) = CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
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
}

@Composable
private fun BoxScope.BlockingOverlay(
    content: @Composable BoxScope.() -> Unit = {}
) = Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
        .matchParentSize()
        .background(
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(elevation = 2.dp)
                .copy(alpha = 0.7f)
        )
        .clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = {}
        )
        .padding(all = pad_s),
    content = content
)

@Preview
@Composable
private fun Step4Preview() {
    PenMouseSPreview {
        LazyColumn {
            step4(
                isAccessibilityEnabled = true,
                serviceStatus = ModeStatus.Off,
                toggleService = {}
            )
        }
    }
}

@Preview
@Composable
private fun Step4AccessibilityDisabledPreview() {
    PenMouseSPreview {
        LazyColumn {
            step4(
                isAccessibilityEnabled = false,
                serviceStatus = ModeStatus.Off,
                toggleService = {}
            )
        }
    }
}

@Preview
@Composable
private fun Step4MouseModePreview() {
    PenMouseSPreview {
        LazyColumn {
            step4(
                isAccessibilityEnabled = true,
                serviceStatus = ModeStatus.Mouse,
                toggleService = {}
            )
        }
    }
}

