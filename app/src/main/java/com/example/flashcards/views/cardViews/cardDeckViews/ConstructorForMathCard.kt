package com.example.flashcards.views.cardViews.cardDeckViews

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.R
import com.example.flashcards.ui.theme.GetModifier
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator

fun buildAnnotatedStringForMC(
    text: String, symbolToVectorMap: Map<String, Int>
): AnnotatedString {
    return buildAnnotatedString {
        var currentIndex = 0
        while (currentIndex < text.length) {
            var matchFound = false
            for ((symbol, _) in symbolToVectorMap) {
                /** Get the string that will map to which Image vector to display. */
                if (text.startsWith(symbol, currentIndex)) {
                    if (symbol == ":Nintegral") {
                        /** How :Nintegral works,
                         *  the user will type Nintegral15-20.
                         *  the following digits and stop once it
                         *  reaches the "-". Then it will get the
                         *  following digits and append the lower(After Nintegral and before -)
                         *  upper(After -, stopped by a space) limits
                         *  and pop them so they can be pulled.*/
                        val lower = text.substringAfter(
                            ":Nintegral"
                        ).takeWhile { it.isDigit() }
                        val upper = text.substringAfter(
                            "-"
                        ).takeWhile { it.isDigit() }
                        // Append bounds as annotations
                        pushStringAnnotation(tag = "lower", annotation = lower)
                        pushStringAnnotation(tag = "upper", annotation = upper)
                        appendInlineContent(symbol, symbol)
                        pop()
                        pop()
                        currentIndex += symbol.length + lower.length + upper.length + 1
                        matchFound = true
                        break
                    } else if (symbol == "^") {
                        val exponent = text.substringAfter(
                            "^"
                        ).takeWhile { it.isDigit() }
                        pushStringAnnotation(tag = "exponent", annotation = exponent)
                        appendInlineContent(symbol, symbol)
                        pop()
                        currentIndex += symbol.length + exponent.length
                        matchFound = true
                        break
                    } else if (symbol == "()") {
                        val numerator = text.substringAfter(
                            "()"
                        ).takeWhile { it.isDigit() }
                        val denominator = text.substringAfter(
                            "/"
                        ).takeWhile { it.isDigit() }
                        pushStringAnnotation(tag = "numerator", annotation = numerator)
                        pushStringAnnotation(tag = "denominator", annotation = denominator)
                        appendInlineContent(symbol, symbol)
                        pop()
                        pop()
                        currentIndex += symbol.length +
                                numerator.length + denominator.length + 1
                        matchFound = true
                        break
                    } else {
                        // Add an inline image for the symbol
                        appendInlineContent(symbol, symbol)
                        currentIndex += symbol.length
                        matchFound = true
                        break
                    }
                }
            }
            if (!matchFound) {
                append(text[currentIndex].toString())  // Append the next character
                currentIndex++
            }
        }
    }
}

fun mapMathCardValues(
    symbolToVectorMap: Map<String, Int>,
    annotatedString: AnnotatedString, getModifier: GetModifier
): Map<String, InlineTextContent> {
    return symbolToVectorMap.mapValues { (value, vectorResId) ->
        if (value == ":Nintegral") {
            InlineTextContent(
                Placeholder(32.sp, 32.sp, PlaceholderVerticalAlign.TextCenter)
            ) {
                val lower = annotatedString.getStringAnnotations(
                    "lower", 0, annotatedString.length
                ).firstOrNull()?.item
                val upper = annotatedString.getStringAnnotations(
                    "upper", 0, annotatedString.length
                ).firstOrNull()?.item
                IntegralWithBounds(lower ?: "", upper ?: "", getModifier)
            }
        } else if (value == "^") {
            InlineTextContent(
                Placeholder(24.sp, 24.sp, PlaceholderVerticalAlign.TextCenter)
            ) {
                val exponent = annotatedString.getStringAnnotations(
                    "exponent", 0, annotatedString.length
                ).firstOrNull()?.item
                ExponentNumber(exponent ?: "", getModifier)
            }
        } else if (value == "()") {
            InlineTextContent(
                Placeholder(32.sp, 48.sp, PlaceholderVerticalAlign.TextCenter)
            ) {
                val numerator = annotatedString.getStringAnnotations(
                    "numerator", 0, annotatedString.length
                ).firstOrNull()?.item
                val denominator = annotatedString.getStringAnnotations(
                    "denominator", 0, annotatedString.length
                ).firstOrNull()?.item
                Fraction(numerator ?: "", denominator ?: "", getModifier)
            }
        } else {
            InlineTextContent(
                Placeholder(24.sp, 24.sp, PlaceholderVerticalAlign.TextCenter)
            ) {
                Image(
                    painter = painterResource(id = vectorResId),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(getModifier.titleColor())
                )
            }
        }
    }
}

@Composable
fun ExponentNumber(exponent: String, getModifier: GetModifier) {
    Box {
        Text(
            text = exponent,
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = (-5).dp),
            color = getModifier.titleColor()
        )
    }
}

@Composable
fun Fraction(
    numerator: String,
    denominator: String,
    getModifier: GetModifier
) {
    println(denominator)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = numerator,
            fontSize = 14.sp,
            modifier = Modifier
                .offset(x = (1).dp, y = (-6.25).dp)
                .padding(vertical = 1.dp),
            color = getModifier.titleColor()
        )
        Canvas(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .offset(y = (-6).dp)) {
            drawLine(
                start = Offset(x = 0f, y = 0f),
                end = Offset(x = 48f, y = 0f),
                color = getModifier.titleColor(),
                strokeWidth = 5f
            )
        }
        Text(
            text = denominator,
            fontSize = 14.sp,
            modifier = Modifier
                .offset(x = 1.dp, y = (-5.75).dp)
                .padding(vertical = 1.dp),
            color = getModifier.titleColor()
        )
    }
}

@Composable
fun IntegralWithBounds(
    lower: String,
    upper: String,
    getModifier: GetModifier
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.icons_integral),
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            colorFilter = ColorFilter.tint(getModifier.titleColor())
        )
        // Lower bound (bottom-left)
        Text(
            text = lower,
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(y = 10.dp, x = (-10).dp),
            color =  getModifier.titleColor()
        )

        // Upper bound (top-right)
        Text(
            text = upper,
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = (-10).dp, x = 10.dp),
            color = getModifier.titleColor()
        )
    }
}

@Composable
fun RenderTextWithSymbols(text: String, getModifier: GetModifier) {
    val symbolToVectorMap = mapOf(
        ":pi" to R.drawable.icons_pi,
        ":sigma" to R.drawable.icons_sigma,
        "!=" to R.drawable.icons_not_equal,
        ":integral" to R.drawable.icons_integral,
        ":Nintegral" to R.drawable.icons_integral,
        "^" to -100122,
        "()" to -100122
    )
    val annotatedString = buildAnnotatedStringForMC(text, symbolToVectorMap)
    // Define the inline content for each symbol
    val inlineContent = mapMathCardValues(
        symbolToVectorMap, annotatedString, getModifier
    )
    Text(
        text = annotatedString,
        inlineContent = inlineContent,
        fontSize = 20.sp,
        lineHeight = 22.sp,
        color = getModifier.titleColor(),
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    )
}