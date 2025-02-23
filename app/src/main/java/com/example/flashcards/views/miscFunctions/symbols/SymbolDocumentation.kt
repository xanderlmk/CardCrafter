package com.example.flashcards.views.miscFunctions.symbols

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.R
import com.example.flashcards.ui.theme.GetModifier

@Composable
fun SymbolDocumentation(
    pressed: MutableState<Boolean>,
    getModifier: GetModifier
) {
    val symbolToVectorMap = mapOf(
        "/pi" to R.drawable.icons_pi,
        "/sigma" to R.drawable.icons_sigma,
        "/!=" to R.drawable.icons_not_equal,
        "/integral" to R.drawable.icons_integral,
        "/Nintegral" to R.drawable.icons_integral,
        "*Nintegral"  to R.drawable.icons_integral,
        "/^" to -100122,
        "/()" to -100122,
        "/>" to -100122
    )
    val introduction = """Mapping text to a symbol correctly is important.
        |To see examples on how to map more complex symbols:
    """.trimMargin()
    val clickHere = "Click here"
    val symbols = """:pi -> /pi 
        |:sigma -> /sigma
        |!= -> /!=
        |:integral -> /integral
        |:NintegralX1-X2 -> /NintegralX1-X2
        |Where X1 and X2 are digits and NOT letters.
        |2^x -> 2/^x
        |Where x is the superscript and can be letters and/or digits.
        |()x/z -> /()x;z 
        |Where x is the numerator and z is the denominator,
        |can be either letters or digits.
        |4:>x -> 4/>x
        |Where x is the subscript and can be letters and/or digits.
        |
    """.trimMargin()

    val exampleHeader = "EXAMPLE:"

    val examples = """You must add a space after certain symbols 
        |you are trying to make.
        |If you want to create an integral with bounds of
        |4 to 7, you must format it as the following:Nintegral4-7 
        |make sure that BOTH
        |4 and 7 are digits and not letters or symbols.
        |adding a space after 7 is important.
        |If you don't, the integral symbol won't show.
        |If done incorrectly you'll see:
        |   :Nintegral4-7x
        |If done correctly you'll see:
        |   *Nintegral4-7 x
    """.trimMargin()

    val returnToTop = "\nReturn to top"
    val annotatedString = buildAnnotatedStringForDocumentation(symbols, symbolToVectorMap)
    val inlineContent = mapValues(symbolToVectorMap, annotatedString, getModifier)

    val annotatedExamples = buildAnnotatedStringForExample(examples, symbolToVectorMap)
    val inlineExamples = mapValues(symbolToVectorMap, annotatedExamples, getModifier)

    var secondToLastPosition by rememberSaveable { mutableIntStateOf(0) }
    if (pressed.value) {
        var goToExample by rememberSaveable { mutableStateOf(false) }
        var returnTo0 by rememberSaveable { mutableStateOf(false) }
        val scrollState = rememberScrollState()
        AlertDialog(
            onDismissRequest = { pressed.value = false },
            title = { Text("Symbol Documentation") },
            text = {
                Column(
                    Modifier.verticalScroll(scrollState)
                ) {
                    Text(
                        text = introduction,
                        color = getModifier.titleColor(),
                        fontSize = 20.sp,
                        lineHeight = 21.sp
                    )
                    Text(
                        text = clickHere,
                        color = Color.Blue,
                        fontSize = 20.sp,
                        lineHeight = 21.sp,
                        modifier = Modifier
                            .clickable{
                                goToExample = true
                            }
                    )
                    Text(
                        text = annotatedString,
                        inlineContent = inlineContent,
                        color = getModifier.titleColor(),
                        fontSize = 20.sp,
                        lineHeight = 30.sp
                    )
                    Text(
                        text = exampleHeader,
                        fontStyle = FontStyle.Italic ,
                        color = getModifier.titleColor(),
                        fontSize = 22.sp,
                        lineHeight = 22.sp,
                        modifier = Modifier
                            .onGloballyPositioned { coordinates ->
                                secondToLastPosition =
                                    coordinates.positionInParent().y.toInt()
                            }
                    )
                    Text(
                        text = annotatedExamples,
                        inlineContent = inlineExamples,
                        color = getModifier.titleColor(),
                        fontSize = 20.sp,
                        lineHeight = 30.sp
                    )
                    Text(
                        text = returnToTop,
                        color = Color.Blue,
                        fontSize = 20.sp,
                        lineHeight = 21.sp,
                        modifier = Modifier
                            .clickable{
                                returnTo0 = true
                            }
                    )
                }
            },
            confirmButton = { },
            dismissButton = {
                Button(
                    onClick = { pressed.value = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getModifier.secondaryButtonColor(),
                        contentColor = getModifier.buttonTextColor()
                    )
                ) {
                    Text("OK")
                }
            },
            modifier = Modifier
                .fillMaxWidth(.975f)
                .fillMaxHeight(.80f)
        )
        LaunchedEffect(goToExample) {
            if (goToExample){
                scrollState.animateScrollTo(secondToLastPosition)
                goToExample = false
            }
        }
        LaunchedEffect(returnTo0) {
            if (returnTo0) {
                scrollState.animateScrollTo(0)
                returnTo0 = false
            }

        }
    }
}

fun buildAnnotatedStringForDocumentation(
    text: String, symbolToVectorMap: Map<String, Int>
): AnnotatedString {
    return buildAnnotatedString {
        var currentIndex = 0
        while (currentIndex < text.length) {
            var matchFound = false
            for ((symbol, _) in symbolToVectorMap) {
                /** Get the string that will map to which Image vector to display. */
                if (text.startsWith(symbol, currentIndex)) {
                    if (symbol == "/Nintegral") {
                        val lower = "X1"
                        val upper = "X2"
                        // Append bounds as annotations
                        pushStringAnnotation(tag = "x1", annotation = lower)
                        pushStringAnnotation(tag = "x2", annotation = upper)
                        appendInlineContent(symbol, symbol)
                        pop()
                        pop()
                        currentIndex += symbol.length + lower.length + upper.length + 1
                        matchFound = true
                        break
                    } else if (symbol == "/^") {
                        val exponent = text.substringAfter(
                            "/^"
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
                    } else if (symbol == "/()") {
                        val numerator = text.substringAfter(
                            "/()"
                        ).takeWhile { !it.isWhitespace() && it != ';' }
                        val denominator = text.substringAfter(
                            ";"
                        ).takeWhile { !it.isWhitespace() }
                        if (numerator.isBlank() || denominator.isBlank()) {
                            break
                        }
                        pushStringAnnotation(tag = "numerator", annotation = numerator)
                        pushStringAnnotation(tag = "denominator", annotation = denominator)
                        appendInlineContent(symbol, symbol)
                        pop()
                        pop()
                        currentIndex += symbol.length +
                                numerator.length + denominator.length + 1
                        matchFound = true
                        break
                    } else if (symbol == "/>") {
                        val subscript = text.substringAfter(
                            "/>"
                        ).takeWhile { !it.isWhitespace() }
                        if (subscript.isBlank()) {
                            break
                        }
                        pushStringAnnotation(tag = "subscript", annotation = subscript)
                        appendInlineContent(symbol, symbol)
                        pop()
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

fun mapValues(
    symbolToVectorMap: Map<String, Int>,
    annotatedString: AnnotatedString, getModifier: GetModifier
): Map<String, InlineTextContent> {
    return symbolToVectorMap.mapValues { (value, vectorResId) ->
        if (value == "/Nintegral") {
            InlineTextContent(
                Placeholder(32.sp, 32.sp, PlaceholderVerticalAlign.TextCenter)
            ) {
                val lower = annotatedString.getStringAnnotations(
                    "x1", 0, annotatedString.length
                ).firstOrNull()?.item
                val upper = annotatedString.getStringAnnotations(
                    "x2", 0, annotatedString.length
                ).firstOrNull()?.item
                IntegralWithBounds(lower ?: "", upper ?: "", getModifier)
            }
        } else if (value == "*Nintegral") {
            InlineTextContent(
                Placeholder(32.sp, 32.sp, PlaceholderVerticalAlign.TextCenter)
            ) {
                val lower = annotatedString.getStringAnnotations(
                    "e.g. lower", 0, annotatedString.length
                ).firstOrNull()?.item
                val upper = annotatedString.getStringAnnotations(
                    "e.g. upper", 0, annotatedString.length
                ).firstOrNull()?.item
                IntegralWithBounds(lower ?: "", upper ?: "", getModifier)
            }
        }else if (value == "/^") {
            val exponent = annotatedString.getStringAnnotations(
                "exponent", 0, annotatedString.length
            ).firstOrNull()?.item
            InlineTextContent(
                Placeholder(24.sp, 24.sp, PlaceholderVerticalAlign.TextCenter)
            ) {
                ExponentNumber(exponent ?: "", getModifier)
            }
        } else if (value == "/()") {
            val numerator = annotatedString.getStringAnnotations(
                "numerator", 0, annotatedString.length
            ).firstOrNull()?.item
            val denominator = annotatedString.getStringAnnotations(
                "denominator", 0, annotatedString.length
            ).firstOrNull()?.item
            InlineTextContent(
                Placeholder(32.sp, 48.sp, PlaceholderVerticalAlign.TextCenter)
            ) {
                DocumentationFraction(
                    numerator ?: "", denominator ?: "", getModifier
                )
            }
        } else if (value == "/>") {
            InlineTextContent(
                Placeholder(24.sp, 24.sp, PlaceholderVerticalAlign.TextCenter)
            ) {
                val subscript = annotatedString.getStringAnnotations(
                    "subscript", 0, annotatedString.length
                ).firstOrNull()?.item
                Subscript(subscript ?: "", getModifier)
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

fun buildAnnotatedStringForExample(
    text: String, symbolToVectorMap: Map<String, Int>
): AnnotatedString {
    return buildAnnotatedString {
        var currentIndex = 0
        while (currentIndex < text.length) {
            var matchFound = false
            for ((symbol, _) in symbolToVectorMap) {
                /** Get the string that will map to which Image vector to display. */
                if (text.startsWith(symbol, currentIndex)) {
                    if (symbol == "*Nintegral") {
                        val lower = text.substringAfter(
                            "*Nintegral"
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
                        pushStringAnnotation(tag = "e.g. lower", annotation = lower)
                        pushStringAnnotation(tag = "e.g. upper", annotation = upper)
                        appendInlineContent(symbol, symbol)
                        pop()
                        pop()
                        currentIndex += symbol.length + lower.length + upper.length + 1
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

@Composable
fun DocumentationFraction(
    numerator: String,
    denominator: String,
    getModifier: GetModifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = numerator,
            fontSize = 14.sp,
            modifier = Modifier
                .offset(x = (-2).dp, y = (-5).dp)
                .padding(vertical = 1.dp),
            color = getModifier.titleColor()
        )
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-6).dp)
        ) {
            drawLine(
                start = Offset(x = 0f, y = 0f),
                end = Offset(x = 48f, y = 0f),
                color = getModifier.titleColor(),
                strokeWidth = 5f
            )
        }
    }
    Text(
        text = denominator,
        fontSize = 14.sp,
        modifier = Modifier
            .offset(x = 10.dp, y = (24).dp)
            .padding(vertical = 1.dp),
        color = getModifier.titleColor()
    )

}