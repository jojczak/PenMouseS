package pl.jojczak.penmouses.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import pl.jojczak.penmouses.ui.theme.pad_m
import pl.jojczak.penmouses.ui.theme.pad_xl
import pl.jojczak.penmouses.ui.theme.pad_xxl

val DIALOG_ELEVATION = 3.dp

@Composable
fun MoreInfoDialog(
    @StringRes titleResId: Int,
    showDialog: Boolean,
    dialogId: Int,
    changeDialogState: (Int, Boolean) -> Unit = { _, _ -> },
    content: @Composable () -> Unit = {},
    buttons: @Composable () -> Unit = {}
) {
    if (showDialog) {
        val density = LocalDensity.current
        val configuration = LocalWindowInfo.current.containerSize

        val screenHeight = with(density) { configuration.height.toDp() }
        val screenWidth = with(density) { configuration.width.toDp() }
        val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        val navBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

        val maxHeight = screenHeight - statusBarHeight - navBarHeight - pad_xxl * 2
        val maxWidth = screenWidth - pad_xl * 2

        Dialog(
            onDismissRequest = { changeDialogState(dialogId, false) },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            )
        ) {
            Surface(
                tonalElevation = DIALOG_ELEVATION,
                shape = RoundedCornerShape(pad_xl),
                modifier = Modifier
                    .heightIn(max = maxHeight)
                    .widthIn(max = maxWidth)
                    .fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = stringResource(titleResId),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(pad_xl)
                    )
                    HorizontalDivider()
                    Box(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .padding(horizontal = pad_xl)
                    ) {
                        ScrollComponent(
                            showDivider = false,
                            modifier = Modifier.padding(vertical = pad_xl)
                        ) {
                            content()
                        }
                    }
                    HorizontalDivider()
                    Box(
                        modifier = Modifier
                            .padding(horizontal = pad_xl, vertical = pad_m)
                    ) {
                        buttons()
                    }
                }
            }
        }
    }
}