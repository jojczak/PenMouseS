package pl.jojczak.penmouses.ui.home

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.jojczak.penmouses.R
import pl.jojczak.penmouses.ui.theme.PenMouseSTheme
import pl.jojczak.penmouses.ui.theme.elevation_1
import pl.jojczak.penmouses.ui.theme.elevation_2
import pl.jojczak.penmouses.ui.theme.pad_l
import pl.jojczak.penmouses.ui.theme.pad_m
import pl.jojczak.penmouses.ui.theme.pad_s
import pl.jojczak.penmouses.ui.theme.pad_xl
import pl.jojczak.penmouses.ui.theme.pad_xs
import pl.jojczak.penmouses.ui.theme.pad_xxl

@Composable
fun HomeScreen() {
    HomeScreenContent()
}

@Composable
fun HomeScreenContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(pad_l)
    ) {
        AppLogo()
        StepsContainer()
    }
}

@Composable
private fun AppLogo() {
    Image(
        painter = painterResource(R.drawable.logo_light),
        contentDescription = stringResource(R.string.home_logo_alt),
        modifier = Modifier.padding(horizontal = pad_xxl)
    )
}

@Composable
private fun StepsContainer() {
    Surface(
        tonalElevation = elevation_1,
        shape = RoundedCornerShape(pad_s),
        modifier = Modifier.padding(top = pad_xl)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(pad_s),
            modifier = Modifier.padding(pad_s)
        ) {
            Step(
                stepTextResId = R.string.home_steps_1,
                stepDescResId = R.string.home_steps_1_des,
                stepMoreResId = R.string.home_steps_1_more,
                stepSettingsResId = R.string.home_steps_1_settings
            ) {
                openSettings(it)
            }
            Step(
                stepTextResId = R.string.home_steps_2,
                stepDescResId = R.string.home_steps_2_des,
                stepSettingsResId = R.string.home_steps_2_settings
            ) {
                openAccessibilitySettings(it)
            }
            Step(
                stepTextResId = R.string.home_steps_3,
                stepDescResId = R.string.home_steps_3_des,
                stepMoreResId = R.string.home_steps_3_more,
                isButton = true
            )
        }
    }
}

@Composable
private fun Step(
    @StringRes stepTextResId: Int,
    @StringRes stepDescResId: Int,
    @StringRes stepMoreResId: Int? = null,
    @StringRes stepSettingsResId: Int? = null,
    isButton: Boolean = false,
    settingsCallback: (Context) -> Unit = { },
) {
    Surface(
        tonalElevation = elevation_2,
        shape = RoundedCornerShape(pad_s),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(pad_m)
        ) {
            StepHeader(
                stepTextResId = stepTextResId,
                stepSettingsResId = stepSettingsResId,
                settingsCallback = settingsCallback
            )
            if (isButton) {
                StepButton(
                    stepDescResId = stepDescResId,
                    stepMoreResId = stepMoreResId
                )
            } else {
                LabeledSwitch(
                    stepDescResId = stepDescResId,
                    stepMoreResId = stepMoreResId
                )
            }
        }
    }
}

@Composable
private fun StepHeader(
    @StringRes stepTextResId: Int,
    @StringRes stepSettingsResId: Int? = null,
    settingsCallback: (Context) -> Unit = { },
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(bottom = pad_m)
    ) {
        Text(
            text = stringResource(stepTextResId),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = STEP_TITLE_ALPHA)
        )
        if (stepSettingsResId != null) {
            SettingsLink(
                stepSettingsResId = stepSettingsResId,
                settingsCallback = settingsCallback
            )
        }
    }
}

@Composable
private fun RowScope.SettingsLink(
    @StringRes stepSettingsResId: Int,
    settingsCallback: (Context) -> Unit = { },
) {
    val context = LocalContext.current

    Spacer(
        modifier = Modifier.weight(1f)
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(pad_xs),
        modifier = Modifier.clickable { settingsCallback(context) }
    ) {
        Text(
            text = stringResource(stepSettingsResId),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = STEP_TITLE_ALPHA),
            style = MaterialTheme.typography.bodySmall
        )
        Icon(
            painter = painterResource(R.drawable.open_in_new_24px),
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = STEP_TITLE_ALPHA),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun StepButton(
    @StringRes stepDescResId: Int,
    @StringRes stepMoreResId: Int? = null
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = {}
        ) {
            Text(
                text = stringResource(stepDescResId).uppercase(),
            )
        }
    }
    StepTextMore(
        stepMoreResId = stepMoreResId,
        modifier = Modifier.padding(top = pad_m)
    )
}

@Composable
private fun LabeledSwitch(
    @StringRes stepDescResId: Int,
    @StringRes stepMoreResId: Int? = null
) {
    val switchState = remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(pad_l)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = stringResource(stepDescResId))
            StepTextMore(stepMoreResId = stepMoreResId)
        }
        CompositionLocalProvider(
            LocalMinimumInteractiveComponentSize provides 0.dp,
        ) {
            Switch(
                checked = switchState.value,
                onCheckedChange = { switchState.value = !switchState.value }
            )
        }
    }
}

@Composable
private fun StepTextMore(
    @StringRes stepMoreResId: Int?,
    modifier: Modifier = Modifier
) {
    stepMoreResId?.let {
        Text(
            text = stringResource(it),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = STEP_MORE_ALPHA),
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Justify,
            modifier = modifier
        )
    }
}

private fun openSettings(context: Context) {
    context.startActivity(Intent(Settings.ACTION_SETTINGS))
}

private fun openAccessibilitySettings(context: Context) {
    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
}

private const val STEP_TITLE_ALPHA = 0.7f
private const val STEP_MORE_ALPHA = 0.5f

@Preview(
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = false, device = "spec:width=411dp,height=891dp"
)
@Composable
private fun HomeScreenPreview() {
    PenMouseSTheme {
        Surface {
            HomeScreenContent()
        }
    }
}

