package pl.jojczak.penmouses.screen.manual.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.jojczak.penmouses.core.common.types.ManualPageType
import pl.jojczak.penmouses.core.common.utils.openAccessibilitySettings
import pl.jojczak.penmouses.core.common.utils.openSettings
import pl.jojczak.penmouses.core.ui.theme.pad_l
import pl.jojczak.penmouses.core.ui.theme.pad_m
import pl.jojczak.penmouses.core.ui.theme.pad_s
import pl.jojczak.penmouses.screen.manual.R
import pl.jojczak.penmouses.core.ui.R as coreR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ManualTopAppBar(
    manualPage: ManualPageType,
    modifier: Modifier = Modifier,
    onMenuIconClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = stringResource(R.string.manual_title))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        navigationIcon = {
            IconButton(
                onClick = {
                    onMenuIconClicked()
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_menu),
                    contentDescription = stringResource(R.string.manual_menu_content_description)
                )
            }
        },
        actions = {
            HandleManualPage(manualPage)
        },
        modifier = modifier
    )
}

@Composable
private fun HandleManualPage(
    manualPage: ManualPageType,
) = CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
    val context = LocalContext.current

    when (manualPage) {
        ManualPageType.PreparationStep1 -> {
            OutlinedButton(
                onClick = { openSettings(context) },
                modifier = Modifier.padding(end = pad_s)
            ) {
                Text(text = stringResource(coreR.string.screen_settings))
                Icon(
                    painter = painterResource(coreR.drawable.ic_open_in_new),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = pad_m)
                        .height(pad_l)
                )
            }
        }

        ManualPageType.PreparationStep3 -> {
            OutlinedButton(
                onClick = { openAccessibilitySettings(context) },
                modifier = Modifier.padding(end = pad_s)
            ) {
                Text(text = stringResource(coreR.string.common_accessibility))
                Icon(
                    painter = painterResource(coreR.drawable.ic_open_in_new),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = pad_m)
                        .height(pad_l)
                )
            }
        }

        else -> Unit
    }
}