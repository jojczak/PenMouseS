package pl.jojczak.penmouses.screen.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import pl.jojczak.penmouses.core.ui.R as coreR
import pl.jojczak.penmouses.core.ui.theme.PenMouseSTheme
import pl.jojczak.penmouses.core.ui.theme.pad_l
import pl.jojczak.penmouses.core.ui.utils.openUrlClickable

@Composable
fun AppVersionComponent() {
    val context = LocalContext.current

    val versionName = if (LocalInspectionMode.current) {
        stringResource(coreR.string.settings_app_info_version_unknown)
    } else {
        context.packageManager.getPackageInfo(
            context.packageName,
            0
        ).versionName ?: stringResource(coreR.string.settings_app_info_version_unknown)
    }

    val appInfoText = stringResource(
        coreR.string.settings_app_info,
        stringResource(coreR.string.app_name),
        versionName
    )

    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(pad_l)
    ) {
        Row {
            Text(
                text = appInfoText,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = APP_INFO_TEXT_ALPHA),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(coreR.string.settings_app_info_author),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = APP_INFO_TEXT_ALPHA),
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.openUrlClickable(context, coreR.string.settings_app_info_author_url)
            )
        }
        Text(
            text = stringResource(coreR.string.settings_app_info_github),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = APP_INFO_TEXT_ALPHA),
            textDecoration = TextDecoration.Underline,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.openUrlClickable(context, coreR.string.settings_app_info_github_url)
        )
    }
}

private const val APP_INFO_TEXT_ALPHA = 0.6f

@Composable
@Preview
private fun PreviewAppVersionComponent() {
    PenMouseSTheme {
        Surface {
            AppVersionComponent()
        }
    }
}