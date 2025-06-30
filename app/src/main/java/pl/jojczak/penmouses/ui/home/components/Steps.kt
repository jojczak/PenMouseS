package pl.jojczak.penmouses.ui.home.components

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import pl.jojczak.penmouses.R
import pl.jojczak.penmouses.ui.common.TextButton
import pl.jojczak.penmouses.ui.home.openAccessibilitySettings
import pl.jojczak.penmouses.ui.home.openSettings
import pl.jojczak.penmouses.ui.theme.LINK_ICON_SIZE_SMALL
import pl.jojczak.penmouses.ui.theme.PenMouseSPreview
import pl.jojczak.penmouses.ui.theme.elevation_1
import pl.jojczak.penmouses.ui.theme.pad_l
import pl.jojczak.penmouses.ui.theme.pad_m
import pl.jojczak.penmouses.ui.theme.pad_s
import pl.jojczak.penmouses.ui.theme.pad_xs
import pl.jojczak.penmouses.ui.theme.pad_xxs
import pl.jojczak.penmouses.ui.theme.radius_m

@Composable
fun StepsContainer(
    modifier: Modifier = Modifier,
    changeDialogState: (step: Int, show: Boolean) -> Unit = { _, _ -> },
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(pad_s),
        modifier = modifier
    ) {
        Step1(changeDialogState = changeDialogState)
        Step2(changeDialogState = changeDialogState)
        Step3(changeDialogState = changeDialogState)
        Step4(changeDialogState = changeDialogState)
    }
}

@Composable
private fun Step1(
    changeDialogState: (step: Int, show: Boolean) -> Unit,
) {
    val context = LocalContext.current

    StepSurface {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                StepHeader(
                    stepText = R.string.home_steps_1,
                    dialogIdToOpen = 1,
                    changeDialogState = changeDialogState
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

@Composable
private fun Step2(
    changeDialogState: (step: Int, show: Boolean) -> Unit,
) {
    StepSurface {
        Column {
            StepHeader(
                stepText = R.string.home_steps_2,
                dialogIdToOpen = 2,
                changeDialogState = changeDialogState
            )
            Text(
                text = stringResource(R.string.home_steps_2_des),
                modifier = Modifier.padding(start = pad_m, end = pad_m, bottom = pad_m)
            )
        }
    }
}


@Composable
private fun Step3(
    changeDialogState: (step: Int, show: Boolean) -> Unit,
) {
    val context = LocalContext.current

    StepSurface {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                StepHeader(
                    stepText = R.string.home_steps_3,
                    dialogIdToOpen = 3,
                    changeDialogState = changeDialogState
                )
                StepHeaderLink(
                    linkText = R.string.home_steps_3_settings,
                    linkCallback = { openSettings(context) }
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
                        checked = true,
                        onCheckedChange = { openAccessibilitySettings(context) },
                        modifier = Modifier.padding(top = pad_xs, end = pad_m)
                    )
                }
            }
        }
    }
}

@Composable
private fun Step4(
    changeDialogState: (step: Int, show: Boolean) -> Unit,
) {
    val options = listOf("Off", "Mouse", "Scroll", "Point")
    var selectedIndex by remember { mutableIntStateOf(0) }

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
                stepText = R.string.home_steps_4,
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
                    text = stringResource(R.string.home_steps_4_more),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Justify
                )
                Text("Select mode:")
                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                    SingleChoiceSegmentedButtonRow {
                        options.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index, options.size),
                                onClick = { selectedIndex = index },
                                selected = index == selectedIndex,
                            ) {
                                Text(label)
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
        StepsContainer()
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
private fun StepsPreviewDark() {
    PenMouseSPreview {
        StepsContainer()
    }
}