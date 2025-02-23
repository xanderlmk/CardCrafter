package com.example.flashcards.views.miscFunctions.symbols

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.R
import com.example.flashcards.controller.specialCardConverters.buildAnnotatedStringForMC
import com.example.flashcards.controller.specialCardConverters.mapMathCardValues
import com.example.flashcards.ui.theme.GetModifier


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
fun Subscript(subscript : String, getModifier : GetModifier){
    Box{
        Text(
            text = subscript,
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = (5).dp),
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
        if (numerator.length > 8 || denominator.length > 8){
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-6).dp)
            ) {
                drawLine(
                    start = Offset(x = 0f, y = 0f),
                    end = Offset(x = 320f, y = 0f),
                    color = getModifier.titleColor(),
                    strokeWidth = 5f
                )
            }
        } else if (numerator.length > 3 || denominator.length > 3) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-6).dp)
            ) {
                drawLine(
                    start = Offset(x = 0f, y = 0f),
                    end = Offset(x = 124f, y = 0f),
                    color = getModifier.titleColor(),
                    strokeWidth = 5f
                )
            }
        } else {
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
        "()" to -100122,
        "()0" to -100122,
        "()1" to -100122,
        "()2" to -100122,
        ":>" to -100122
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