package pl.jojczak.penmouses.screen.settings.tab.general

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.jojczak.penmouses.core.common.utils.PrefKey
import pl.jojczak.penmouses.core.common.utils.PrefKeys
import pl.jojczak.penmouses.core.ui.theme.PenMouseSDevicePreview
import pl.jojczak.penmouses.core.ui.theme.pad_l
import pl.jojczak.penmouses.core.ui.theme.pad_m
import pl.jojczak.penmouses.core.ui.theme.pad_xl
import pl.jojczak.penmouses.screen.settings.R
import pl.jojczak.penmouses.screen.settings.components.PreferenceIcon
import pl.jojczak.penmouses.screen.settings.components.horizontalDivider
import pl.jojczak.penmouses.screen.settings.mvi.SettingsViewAction
import pl.jojczak.penmouses.core.ui.R as coreR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsGeneralTab(
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: SettingsGeneralViewModel = hiltViewModel()
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    // @formatter:off
    SettingsGeneralTabContent(
        viewState = viewState,
        contentPadding = contentPadding,
        onPrefChanged = { key, value -> viewModel.onViewAction(SettingsViewAction.UpdatePreference(key, value)) },
    )
    // @formatter:onn
}

@Composable
private fun SettingsGeneralTabContent(
    viewState: SettingsGeneralState,
    contentPadding: PaddingValues,
    onPrefChanged: (PrefKey<Boolean>, Boolean) -> Unit = { _, _ -> }
) {
    LazyColumn(
        contentPadding = contentPadding,
        modifier = Modifier.fillMaxSize()
    ) {
        notificationsComponent()
        horizontalDivider()
        analyticsToggle(
            analyticsEnabled = viewState.analyticsEnabled,
            onPrefChanged = onPrefChanged
        )
        horizontalDivider()
    }
}

internal fun LazyListScope.notificationsComponent() {
    preferenceItem(
        iconId = coreR.drawable.ic_notification_settings,
        labelId = R.string.settings_notification,
        onClick = { openNotificationSettings(it) }
    ) {
        Icon(
            painter = painterResource(coreR.drawable.ic_open_in_new),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null
        )
    }
}

private fun LazyListScope.analyticsToggle(
    analyticsEnabled: Boolean,
    onPrefChanged: (PrefKey<Boolean>, Boolean) -> Unit = { _, _ -> }
) {
    preferenceItem(
        iconId = coreR.drawable.ic_analytics,
        labelId = R.string.settings_analytics,
        onClick = { onPrefChanged(PrefKeys.ANALYTICS_ENABLED, !analyticsEnabled) }
    ) {
        Switch(
            checked = analyticsEnabled,
            onCheckedChange = {
                onPrefChanged(PrefKeys.ANALYTICS_ENABLED, it)
            }
        )
    }
}

private fun LazyListScope.preferenceItem(
    @DrawableRes iconId: Int,
    @StringRes labelId: Int,
    onClick: (Context) -> Unit,
    content: @Composable () -> Unit = {}
) = item {
    val ctx = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onClick(ctx) }
            .padding(vertical = pad_l, horizontal = pad_xl)
    ) {
        PreferenceIcon(iconId = iconId)
        Text(
            text = stringResource(labelId),
            modifier = Modifier
                .weight(1f)
                .padding(start = pad_m)
        )
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            content()
        }
    }
}

private fun openNotificationSettings(context: Context) {
    val intent = Intent().apply {
        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        putExtra(Settings.EXTRA_CHANNEL_ID, context.applicationInfo.uid)
    }
    context.startActivity(intent)
}


@Preview
@Composable
private fun SettingsMouseTabPreview() {
    PenMouseSDevicePreview {
        SettingsGeneralTabContent(
            contentPadding = it,
            viewState = SettingsGeneralState()
        )
    }
}

