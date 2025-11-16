package pl.jojczak.penmouses.core.ui.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import com.halilibo.richtext.markdown.BasicMarkdown
import com.halilibo.richtext.markdown.node.AstNode
import com.halilibo.richtext.ui.RichTextStyle
import com.halilibo.richtext.ui.RichTextThemeProvider
import com.halilibo.richtext.ui.material3.RichText
import com.halilibo.richtext.ui.string.RichTextStringStyle

@Composable
fun PenMouseSMarkdown(
    astNode: AstNode,
    modifier: Modifier = Modifier,
    align: TextAlign = TextAlign.Justify,
) = RichText(
    style = richTextStyle(),
    modifier = modifier
) {
    RichTextThemeProvider(
        textStyleProvider = { paragraphStyle(align) },
        textStyleBackProvider = { nS, c -> MergedTextStyle(newStyle = nS, content = c, align) },
        contentColorProvider = { MaterialTheme.colorScheme.onBackground },
        content = { BasicMarkdown(astNode = astNode) }
    )
}

@Composable
private fun paragraphStyle(textAlign: TextAlign) = LocalTextStyle.current.copy(
    textAlign = textAlign,
    letterSpacing = TextUnit.Unspecified,
)

@Composable
private fun MergedTextStyle(
    newStyle: TextStyle,
    content: @Composable () -> Unit,
    textAlign: TextAlign
) = CompositionLocalProvider(
    value = LocalTextStyle provides newStyle.copy(textAlign = textAlign),
    content = content
)

@Composable
private fun richTextStyle(): RichTextStyle {
    val linkColor = MaterialTheme.colorScheme.onBackground

    val defaultLinkTextStyle = SpanStyle(
        color = linkColor.copy(alpha = 0.7f),
        textDecoration = TextDecoration.Underline,
        fontWeight = FontWeight.Bold
    )

    val linkStyle = TextLinkStyles(
        style = defaultLinkTextStyle,
        hoveredStyle = defaultLinkTextStyle.copy(linkColor.copy(0.5f)),
        pressedStyle = defaultLinkTextStyle.copy(linkColor.copy(0.3f))
    )

    return RichTextStyle.Default.copy(stringStyle = RichTextStringStyle(linkStyle = linkStyle))
}