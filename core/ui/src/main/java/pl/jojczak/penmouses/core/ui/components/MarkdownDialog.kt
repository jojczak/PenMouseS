package pl.jojczak.penmouses.core.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.halilibo.richtext.commonmark.CommonmarkAstNodeParser
import pl.jojczak.penmouses.core.ui.theme.pad_xl
import java.io.BufferedReader

@Composable
fun MarkdownDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    @DrawableRes iconId: Int,
    @StringRes titleId: Int,
    @RawRes markdownTextId: Int,
) {
    val resources = LocalResources.current
    val markdown = remember {
        val rawMarkdown = resources.openRawResource(markdownTextId)
            .bufferedReader()
            .use(BufferedReader::readText)

        CommonmarkAstNodeParser().parse(rawMarkdown)
    }

    AlertDialog(
        icon = {
            Icon(painter = painterResource(iconId), contentDescription = null)
        },
        title = {
            Text(text = stringResource(titleId))
        },
        text = {
            PenMouseSMarkdown(astNode = markdown)
        },
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        ),
        modifier = Modifier.padding(horizontal = pad_xl)
    )
}