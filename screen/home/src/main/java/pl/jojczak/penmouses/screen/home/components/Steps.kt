package pl.jojczak.penmouses.screen.home.components

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent.Event
import pl.jojczak.penmouses.core.common.spen.AppToServiceEvent.PenMode
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
import pl.jojczak.penmouses.screen.home.openAccessibilitySettings
import pl.jojczak.penmouses.screen.home.openSettings
import pl.jojczak.penmouses.core.ui.R as coreR

internal fun LazyListScope.stepsContainer(
    serviceStatus: PenMode,
    toggleService: (event: Event) -> Unit,
    changeDialogState: (step: Int, show: Boolean) -> Unit,
) {
    step1(changeDialogState = changeDialogState)
    step2(changeDialogState = changeDialogState)
    step3(changeDialogState = changeDialogState)
    step4(
        serviceStatus = serviceStatus,
        toggleService = toggleService,
        changeDialogState = changeDialogState
    )
}

private fun LazyListScope.step1(
    changeDialogState: (step: Int, show: Boolean) -> Unit,
) = item {
    val context = LocalContext.current

    StepSurface {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                StepHeader(
                    stepText = coreR.string.home_steps_1,
                    dialogIdToOpen = 1,
                    changeDialogState = changeDialogState
                )
                Text(
                    text = stringResource(coreR.string.home_steps_1_des),
                    modifier = Modifier.padding(start = pad_m, bottom = pad_m)
                )
            }
            TextButton(
                stringRes = coreR.string.home_steps_1_settings,
                onClick = { openSettings(context) },
            )
        }
    }
}

private fun LazyListScope.step2(
    changeDialogState: (step: Int, show: Boolean) -> Unit,
) = item {
    StepSurface {
        Column {
            StepHeader(
                stepText = coreR.string.home_steps_2,
                dialogIdToOpen = 2,
                changeDialogState = changeDialogState
            )
            Text(
                text = stringResource(coreR.string.home_steps_2_des),
                modifier = Modifier.padding(start = pad_m, end = pad_m, bottom = pad_m)
            )
        }
    }
}

private fun LazyListScope.step3(
    changeDialogState: (step: Int, show: Boolean) -> Unit,
) = item {
    val context = LocalContext.current

    StepSurface {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                StepHeader(
                    stepText = coreR.string.home_steps_3,
                    dialogIdToOpen = 3,
                    changeDialogState = changeDialogState
                )
                StepHeaderLink(
                    linkText = coreR.string.home_steps_3_settings,
                    linkCallback = { openAccessibilitySettings(context) }
                )
            }
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(pad_l),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(coreR.string.home_steps_3_des),
                    modifier = Modifier
                        .padding(start = pad_m, bottom = pad_m)
                        .weight(1f)
                )
                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                    Switch(
                        checked = true,
                        onCheckedChange = { openAccessibilitySettings(context) },
                        modifier = Modifier.padding(top = pad_xs, end = pad_m)
                    )
                }
            }
        }
    }
}

private fun LazyListScope.step4(
    serviceStatus: PenMode,
    toggleService: (event: Event) -> Unit,
    changeDialogState: (step: Int, show: Boolean) -> Unit,
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
            StepHeader(
                stepText = coreR.string.home_steps_4,
                changeDialogState = changeDialogState
            )
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
                    text = stringResource(coreR.string.home_steps_4_more),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Justify
                )
                Text("Select mode:")
                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                    SingleChoiceSegmentedButtonRow {
                        PenMode.entries.forEachIndexed { index, mode ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index,
                                    PenMode.entries.size
                                ),
                                onClick = {
                                    toggleService(Event.Start(mode))
                                },
                                selected = mode == serviceStatus,
                            ) {
                                Text(mode.name)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepSurface(
    shape: RoundedCornerShape = RoundedCornerShape(radius_m),
    content: @Composable () -> Unit
) {
    Surface(
        tonalElevation = elevation_1,
        shape = shape,
        modifier = Modifier
            .fillMaxWidth(),
        content = content
    )
}

@Composable
private fun StepHeader(
    @StringRes stepText: Int,
    dialogIdToOpen: Int? = null,
    changeDialogState: (step: Int, show: Boolean) -> Unit,
) {
    Row {
        Text(
            text = stringResource(id = stepText),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = pad_m, top = pad_s)
        )
        dialogIdToOpen?.let {
            Text(
                text = stringResource(id = coreR.string.home_steps_bullet),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = pad_s)
            )
            Text(
                text = stringResource(id = coreR.string.home_steps_more),
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(start = pad_xxs, top = pad_xs)
                    .clip(RoundedCornerShape(radius_m))
                    .clickable { changeDialogState(dialogIdToOpen, true) }
                    .padding(all = pad_xs)
            )
        }
    }
}

@Composable
private fun StepHeaderLink(
    @StringRes linkText: Int,
    linkCallback: () -> Unit
) {
    TextButton(
        stringRes = linkText,
        textStyle = MaterialTheme.typography.bodySmall,
        onClick = linkCallback,
        iconSize = LINK_ICON_SIZE_SMALL,
        spacedBy = pad_xs,
        smallPad = PaddingValues(pad_xs, pad_xs, pad_xs, 0.dp),
        normalPad = PaddingValues(pad_s)
    )
}

@Composable
@Preview
private fun StepsPreview() {
    PenMouseSPreview {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(pad_s)
        ) {
            stepsContainer(
                serviceStatus = PenMode.Off,
                changeDialogState = { _, _ -> },
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
                serviceStatus = PenMode.Off,
                changeDialogState = { _, _ -> },
                toggleService = {}
            )
        }
    }
}