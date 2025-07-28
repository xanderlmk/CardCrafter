package com.belmontCrest.cardCrafter.uiFunctions.katex


import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.belmontCrest.cardCrafter.model.FSProp
import com.belmontCrest.cardCrafter.model.FWProp
import com.belmontCrest.cardCrafter.model.TSProp
import com.belmontCrest.cardCrafter.model.TextProps
import com.belmontCrest.cardCrafter.model.toTextProp
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.CustomText
import com.belmontCrest.cardCrafter.uiFunctions.buttons.SubmitButton

@Composable
fun SymbolDocumentation(
    pressed: MutableState<Boolean>, getUIStyle: GetUIStyle,
    scrollState: ScrollState, modifier: Modifier
) {
    val introduction =
        """|Mapping text to an equation/notation correctly is important.
        |To see examples on how to map more complex symbols:
    """.trimMargin()
    val clickHere = "Click here"
    val symbols =
        """|$$ your equation here $$
        | \\int 
        | \\frac{x}{y} is a fraction where x is the numerator 
    """.trimMargin()

    val exampleHeader = "EXAMPLE:"

    val example1 =
        """|Suppose you want to create a fraction with the following content:
            |4pi/3 r^3
            |But how do you do that?
            |Well let me help you out a little :)
            |First make sure to always put your expressions in between
            |$$ here $$ 
            |or
            |\\( here \\)
            |Now we have:
            |$$\\frac{4\\pi}{3} r^{3}$$
            |This should give you the following:
        """.trimMargin()


    val result = "$$\\\\frac{4\\\\pi}{3}r^{3}$$"

    val explanation = """
        |$$$$ is to make a new line and isolate the notation/function, but
        |\\(\\) is for making inline content. let's see the difference.
    """.trimMargin()

    val example2 = """
        Hey look at this alpha symbol! $$\\alpha$$
        Pretty cool right?<br>
        Now look at this sentence:<br> Hey look at this majestic pi symbol! \\(\\pi\\)
        Pretty cool right?
    """.trimIndent()


    val returnToTop = "\nReturn to top"

    var secondToLastPosition by rememberSaveable { mutableIntStateOf(0) }
    if (pressed.value) {
        var goToExample by rememberSaveable { mutableStateOf(false) }
        var returnTo0 by rememberSaveable { mutableStateOf(false) }
        Dialog(
            onDismissRequest = { pressed.value = false },
            properties = DialogProperties(
                decorFitsSystemWindows = false, usePlatformDefaultWidth = false
            )
        ) {
            Column(
                modifier = modifier.zIndex(2f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                CustomText(
                    "Symbol Documentation", getUIStyle, Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                        .align(Alignment.Start),
                    props = TextProps(fs = FSProp.Font20, ts = TSProp.MediumTitle)
                )

                CustomText(text = introduction, getUIStyle = getUIStyle, Modifier.fillMaxWidth())
                CustomText(
                    text = clickHere, getUIStyle = getUIStyle, props = FWProp.SemiBold.toTextProp(),
                    modifier = Modifier.clickable { goToExample = true }
                )
                CustomText(text = symbols, getUIStyle = getUIStyle)
                CustomText(
                    text = exampleHeader, getUIStyle = getUIStyle,
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        secondToLastPosition = coordinates.positionInParent().y.toInt()
                    }, fontStyle = FontStyle.Italic,
                    props = TSProp.MediumTitle.toTextProp()
                )

                CustomText(text = example1, getUIStyle = getUIStyle, Modifier.fillMaxWidth())
                KaTeXWebView(
                    result, getUIStyle, Modifier
                        .zIndex(-2f)
                        .fillMaxWidth()
                )
                CustomText(text = explanation, getUIStyle = getUIStyle)
                KaTeXWebView(
                    example2, getUIStyle, Modifier
                        .zIndex(-2f)
                        .fillMaxWidth()
                )
                CustomText(
                    returnToTop, getUIStyle = getUIStyle, props = FWProp.SemiBold.toTextProp(),
                    modifier = Modifier.clickable { returnTo0 = true }
                )
                SubmitButton(onClick = { pressed.value = false }, true, getUIStyle, "OK")
            }
        }
        LaunchedEffect(goToExample) {
            if (goToExample) {
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
