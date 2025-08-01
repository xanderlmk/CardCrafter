package com.belmontCrest.cardCrafter.uiFunctions

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import com.belmontCrest.cardCrafter.model.TextProps
import com.belmontCrest.cardCrafter.model.setFontSize
import com.belmontCrest.cardCrafter.model.setFontWeight
import com.belmontCrest.cardCrafter.model.setMaxLines
import com.belmontCrest.cardCrafter.model.setTextAlign
import com.belmontCrest.cardCrafter.model.setTextColor
import com.belmontCrest.cardCrafter.model.setTextStyle
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle

@Composable
fun CustomText(
    text: String, getUIStyle: GetUIStyle, modifier: Modifier = Modifier,
    props: TextProps = TextProps(), fontStyle: FontStyle? = null, softWrap : Boolean = true
) {
    val fontWeight = props.setFontWeight()
    val fontSize = props.setFontSize()
    val textAlign = props.setTextAlign()
    val maxLines = props.setMaxLines()
    val textColor = props.setTextColor(getUIStyle)
    val style = props.setTextStyle()
    Text(
        text = text, color = textColor,
        textAlign = textAlign, modifier = modifier,
        fontSize = fontSize, fontWeight = fontWeight,
        maxLines = maxLines, overflow = TextOverflow.Ellipsis,
        style = style, fontStyle = fontStyle, softWrap = softWrap
    )
}

@Composable
fun CustomText(
    text: AnnotatedString, getUIStyle: GetUIStyle, modifier: Modifier = Modifier,
    props: TextProps = TextProps(), fontStyle: FontStyle? = null, softWrap: Boolean = true
) {
    val fontWeight = props.setFontWeight()
    val fontSize = props.setFontSize()
    val textAlign = props.setTextAlign()
    val maxLines = props.setMaxLines()
    val textColor = props.setTextColor(getUIStyle)
    val style = props.setTextStyle()

    Text(
        text = text, color = textColor,
        textAlign = textAlign, modifier = modifier,
        fontSize = fontSize, fontWeight = fontWeight,
        maxLines = maxLines, overflow = TextOverflow.Ellipsis,
        style = style, fontStyle = fontStyle, softWrap = softWrap
    )
}