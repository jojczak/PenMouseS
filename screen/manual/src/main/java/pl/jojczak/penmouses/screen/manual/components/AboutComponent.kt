package pl.jojczak.penmouses.screen.manual.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import pl.jojczak.penmouses.core.ui.theme.PenMouseSTheme
import pl.jojczak.penmouses.core.ui.theme.pad_l
import pl.jojczak.penmouses.core.ui.theme.pad_s
import pl.jojczak.penmouses.core.ui.theme.pad_xl
import pl.jojczak.penmouses.core.ui.utils.openUrlClickable
import pl.jojczak.penmouses.screen.manual.R

internal fun LazyListScope.appInfoComponent(ctx: Context) = item {
    val inspection = LocalInspectionMode.current

    // @formatter:off
    val drawable = remember {
        if (inspection) ctx.getDrawable(R.drawable.ic_spen)
        else ctx.packageManager.getApplicationIcon(ctx.packageName)
    }
    val appName = remember {
        if (inspection) "PenMouse S"
        else ctx.applicationInfo.loadLabel(ctx.packageManager).toString()
    }
    val appVersion = remember {
        if (inspection) ctx.getString(R.string.manual_app_info_version_unknown)
        else ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName ?: ctx.getString(R.string.manual_app_info_version_unknown)
    }
    // @formatter:on

    Row(
        modifier = Modifier
            .padding(top = pad_xl, bottom = pad_s)
            .padding(horizontal = pad_l),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberDrawablePainter(drawable = drawable),
            contentDescription = null,
        )
        Column(
            modifier = Modifier
                .padding(start = pad_xl)
                .weight(1f)
        ) {
            Text(
                text = appName,
                style = MaterialTheme.typography.headlineMedium,
            )
            Row {
                Text(
                    text = stringResource(R.string.manual_app_info_version, appVersion),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = stringResource(R.string.manual_app_info_github),
                    textDecoration = TextDecoration.Underline,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.openUrlClickable(
                        ctx,
                        R.string.manual_app_info_github_url
                    )
                )
            }
            Row {
                Text(
                    text = stringResource(R.string.manual_app_info_by),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(R.string.manual_app_info_author),
                    style = MaterialTheme.typography.bodyMedium,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.openUrlClickable(
                        ctx,
                        R.string.manual_app_info_github_url
                    )
                )
            }
        }
    }
}

@Composable
@Preview
private fun PreviewDonateComponent() {
    PenMouseSTheme {
        Surface {
            val ctx = LocalContext.current
            LazyColumn {
                appInfoComponent(ctx)
            }
        }
    }
}
