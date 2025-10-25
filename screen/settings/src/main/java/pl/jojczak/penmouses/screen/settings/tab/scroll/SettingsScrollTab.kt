package pl.jojczak.penmouses.screen.settings.tab.scroll

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.jojczak.penmouses.core.ui.theme.PenMouseSDevicePreview
import pl.jojczak.penmouses.core.ui.theme.pad_m
import pl.jojczak.penmouses.core.ui.theme.radius_m
import pl.jojczak.penmouses.screen.settings.R
import pl.jojczak.penmouses.screen.settings.components.horizontalDivider
import pl.jojczak.penmouses.screen.settings.mvi.SettingsViewAction

@Composable
internal fun SettingsScrollTab(
    contentPadding: PaddingValues,
    refreshDataTrigger: Boolean,
    viewModel: SettingsScrollViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(refreshDataTrigger) {
        if (!refreshDataTrigger) viewModel.onViewAction(SettingsViewAction.RefreshData)
    }

    // @formatter:off
    SettingsScrollTabContent(
        viewState = viewState,
        contentPadding = contentPadding,
        onExperimentalModeClicked = { viewModel.onViewAction(SettingsScrollViewAction.ExperimentalModeUpdate(it)) }
    )
    // @formatter:on
}

@Composable
private fun SettingsScrollTabContent(
    viewState: SettingsScrollState,
    contentPadding: PaddingValues,
    onExperimentalModeClicked: (Boolean) -> Unit = {}
) = LazyColumn(
    contentPadding = contentPadding,
    modifier = Modifier.fillMaxSize()
) {
    experimentalModeCheckBox(
        experimentalModeEnabled = viewState.experimentalModeEnabled,
        onExperimentalModeClicked = onExperimentalModeClicked
    )

    horizontalDivider()
}

private fun LazyListScope.experimentalModeCheckBox(
    experimentalModeEnabled: Boolean,
    onExperimentalModeClicked: (Boolean) -> Unit
) = item {
    Row(
        modifier = Modifier
            .padding(all = pad_m)
            .clip(shape = RoundedCornerShape(radius_m))
            .clickable { onExperimentalModeClicked(!experimentalModeEnabled) }
    ) {
        Checkbox(
            checked = experimentalModeEnabled,
            onCheckedChange = onExperimentalModeClicked
        )
        Column {
            Text(
                text = stringResource(R.string.settings_scroll_experimental_mode)
            )
            Text(
                text = stringResource(R.string.settings_scroll_experimental_mode_desc),
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Preview
@Composable
private fun SettingsScrollPreview() {
    PenMouseSDevicePreview {
        SettingsScrollTabContent(
            viewState = SettingsScrollState(),
            contentPadding = it,
        )
    }
}