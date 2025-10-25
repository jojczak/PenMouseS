package pl.jojczak.penmouses.screen.settings.tab.general

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import pl.jojczak.penmouses.core.ui.theme.LINK_ICON_SIZE
import pl.jojczak.penmouses.core.ui.theme.PenMouseSDevicePreview
import pl.jojczak.penmouses.core.ui.theme.pad_l
import pl.jojczak.penmouses.core.ui.theme.pad_m
import pl.jojczak.penmouses.core.ui.theme.pad_xl
import pl.jojczak.penmouses.screen.settings.R
import pl.jojczak.penmouses.core.ui.R as coreR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsGeneralTab(
    contentPadding: PaddingValues = PaddingValues(),
) = LazyColumn(
    contentPadding = contentPadding,
    modifier = Modifier.fillMaxSize()
) {
    notificationsComponent()
}

internal fun LazyListScope.notificationsComponent() = item {
    val context = LocalContext.current

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {
                    openNotificationSettings(context)
                }
                .padding(
                    vertical = pad_l,
                    horizontal = pad_xl
                )
        ) {
            Icon(
                painter = painterResource(coreR.drawable.ic_notification_settings),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                contentDescription = null,
                modifier = Modifier.height(LINK_ICON_SIZE)
            )
            Text(
                text = stringResource(R.string.settings_notification),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = pad_m)
            )
            Icon(
                painter = painterResource(coreR.drawable.open_in_new_24px),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null
            )
        }
        HorizontalDivider()
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
        SettingsGeneralTab(
            contentPadding = it
        )
    }
}

