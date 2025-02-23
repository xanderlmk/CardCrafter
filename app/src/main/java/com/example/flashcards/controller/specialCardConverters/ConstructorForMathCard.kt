package com.example.flashcards.controller.specialCardConverters

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.sp
import com.example.flashcards.ui.theme.GetModifier
import com.example.flashcards.views.miscFunctions.symbols.ExponentNumber
import com.example.flashcards.views.miscFunctions.symbols.Fraction
import com.example.flashcards.views.miscFunctions.symbols.IntegralWithBounds
import com.example.flashcards.views.miscFunctions.symbols.Subscript
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator

fun buildAnnotatedStringForMC(
    text: String, symbolToVectorMap: Map<String, Int>
): AnnotatedString {
    return buildAnnotatedString {
        var currentIndex = 0
        var subscriptCounter = 0
        Log.d("Text", text)
        while (currentIndex < text.length) {
            var matchFound = false
            for ((symbol, _) in symbolToVectorMap) {
                /** Get the string that will map to which Image vector to display. */
                if (text.startsWith(symbol, currentIndex)) {
                    if (symbol == ":Nintegral") {
                        /** How :Nintegral works,
                         *  the user will type :Nintegral15-20.
                         *  the following digits and stop once it
                         *  reaches the "-". Then it will get the
                         *  following digits and append the lower(After :Nintegral and before -)
                         *  upper(After -, stopped by a space) limits
                         *  and pop them so they can be pulled.*/
                        val lower = text.substringAfter(
                            ":Nintegral"
                        ).takeWhile { it.isDigit() }
                        val upper = text.substringAfter(
                            "-"
                        ).takeWhile { it.isDigit() }
                        /** We have to make sure that if the user wants to use
                         *  a symbol, it has to be in valid format. */
                        if (lower.isBlank() || upper.isBlank()) {
                            break
                        }
                        // Append bounds as annotations
                        pushStringAnnotation(
                            tag = "integral",
                            annotation = "$lower-$upper"
                        )
                        appendInlineContent(symbol, symbol)
                        pop()
                        currentIndex += symbol.length + lower.length + upper.length + 1
                        matchFound = true
                        break
                    } else if (symbol == "^") {
                        val exponent = text.substringAfter(
                            "^"
                        ).takeWhile { !it.isWhitespace() }
                        if (exponent.isBlank()) {
                            break
                        }
                        pushStringAnnotation(tag = "exponent", annotation = exponent)
                        appendInlineContent(symbol, symbol)
                        pop()
                        currentIndex += symbol.length + exponent.length
                        matchFound = true
                        break
                    } else if (symbol == "()") {
                        val numerator = text.substringAfter(
                            "()"
                        ).takeWhile { !it.isWhitespace() && it != '/' }
                        val denominator = text.substringAfter(
                            "/"
                        ).takeWhile { !it.isWhitespace() }
                        if (numerator.isBlank() || denominator.isBlank()) {
                            break
                        }
                        val size = returnLineSize(numerator.length, denominator.length)
                        pushStringAnnotation(
                            tag = "fraction",
                            annotation = "$numerator-$denominator"
                        )
                        appendInlineContent("$symbol$size")
                        pop()
                        currentIndex += symbol.length +
                                numerator.length + denominator.length + 1
                        matchFound = true
                        break
                    } else if (symbol == ":>") {
                        val subStart = currentIndex + symbol.length
                        val subscript = text.substring(subStart)
                            .takeWhile { !it.isWhitespace() }
                        println(subscript)
                        if (subscript.isBlank()) {
                            break
                        }
                        pushStringAnnotation(
                            tag = "subscript",
                            annotation = subscript
                        )
                        appendInlineContent(symbol, symbol)
                        pop()
                        subscriptCounter += 1
                        currentIndex += symbol.length + subscript.length
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
    var subscriptCounter = 0
    return symbolToVectorMap.mapValues { (value, vectorResId) ->
        if (value == ":Nintegral") {
            InlineTextContent(
                Placeholder(32.sp, 32.sp, PlaceholderVerticalAlign.TextCenter)
            ) {
                val integralList = annotatedString.getStringAnnotations(
                    "integral", 0, annotatedString.length
                )
                integralList.map { integral ->
                    val lower = integral.item.substringBefore('-')
                    val upper = integral.item.substringAfter('-')
                    IntegralWithBounds(lower, upper, getModifier)
                }
            }
        } else if (value == "^") {
            val exponent = annotatedString.getStringAnnotations(
                "exponent", 0, annotatedString.length
            ).firstOrNull()?.item
            InlineTextContent(
                Placeholder(24.sp, 24.sp, PlaceholderVerticalAlign.TextCenter)
            ) {
                ExponentNumber(exponent ?: "", getModifier)
            }
        } else if (value == "()0") {
            val fractionList = annotatedString.getStringAnnotations(
                "fraction", 0, annotatedString.length
            )
            InlineTextContent(
                Placeholder(32.sp, 48.sp, PlaceholderVerticalAlign.TextCenter)
            ) {
                fractionList.map { fraction ->
                    val numerator = fraction.item.substringBefore('-')
                    val denominator = fraction.item.substringAfter('-')
                    Fraction(numerator, denominator, getModifier)
                }
            }

        } else if (value == "()1") {
            val fractionList = annotatedString.getStringAnnotations(
                "fraction", 0, annotatedString.length
            )
            InlineTextContent(
                Placeholder(64.sp, 48.sp, PlaceholderVerticalAlign.TextCenter)
            ) {
                fractionList.map { fraction ->
                    val numerator = fraction.item.substringBefore('-')
                    val denominator = fraction.item.substringAfter('-')
                    Fraction(numerator, denominator, getModifier)
                }
            }

        } else if (value == "()2") {
            val fractionList = annotatedString.getStringAnnotations(
                "fraction", 0, annotatedString.length
            )
            InlineTextContent(
                Placeholder(96.sp, 48.sp, PlaceholderVerticalAlign.TextCenter)
            ) {
                fractionList.map { fraction ->
                    val numerator = fraction.item.substringBefore('-')
                    val denominator = fraction.item.substringAfter('-')
                    Fraction(numerator, denominator, getModifier)
                }
            }
        } else if (value == ":>") {
            /**val subscriptList = annotatedString.getStringAnnotations(
                "subscript$subscriptCounter", 0, annotatedString.length
            ) //"subscript$subscriptCounter"
            InlineTextContent(
                Placeholder(24.sp, 24.sp, PlaceholderVerticalAlign.TextCenter)
            ) {
                /*if (subscriptCounter in 0.. subscriptList.lastIndex) {
                    Subscript(subscriptList[subscriptCounter].item, getModifier)
                }*/

                subscriptList.map { subscript ->
                    Subscript(subscript.item, getModifier)
                    subscriptCounter += 1
                }
            }*/
            InlineTextContent(
                Placeholder(24.sp, 24.sp, PlaceholderVerticalAlign.TextCenter)
            ) { scope ->
                // Get annotations at the inline content's position
                val annotations = annotatedString.getStringAnnotations(
                    tag = "subscript",
                    start = 0,
                    end = annotatedString.length
                )
                // Render the subscript text for this position
                annotations.forEach { annotation ->
                    Subscript(annotation.item, getModifier)
                }
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
