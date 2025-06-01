package com.belmontCrest.cardCrafter.uiFunctions.katex


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.SubmitButton

@Composable
fun SymbolDocumentation(
    pressed: MutableState<Boolean>,
    getUIStyle: GetUIStyle
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
        val scrollState = rememberScrollState()
        AlertDialog(
            onDismissRequest = { pressed.value = false },
            title = { Text("Symbol Documentation") },
            text = {
                Column(
                    Modifier.verticalScroll(scrollState)
                ) {
                    Text(
                        text = introduction, color = getUIStyle.titleColor(),
                        fontSize = 20.sp, lineHeight = 21.sp
                    )
                    Text(
                        text = clickHere, color = Color.Blue,
                        fontSize = 20.sp, lineHeight = 21.sp,
                        modifier = Modifier
                            .clickable {
                                goToExample = true
                            }
                    )
                    Text(
                        text = symbols, color = getUIStyle.titleColor(),
                        fontSize = 20.sp, lineHeight = 30.sp
                    )
                    Text(
                        text = exampleHeader, fontStyle = FontStyle.Italic,
                        color = getUIStyle.titleColor(), fontSize = 22.sp,
                        lineHeight = 22.sp,
                        modifier = Modifier
                            .onGloballyPositioned { coordinates ->
                                secondToLastPosition =
                                    coordinates.positionInParent().y.toInt()
                            }
                    )
                    Text(
                        text = example1, color = getUIStyle.titleColor(),
                        fontSize = 20.sp, lineHeight = 30.sp
                    )
                    KaTeXWebView(result, getUIStyle)
                    Text(
                        text = explanation, color = getUIStyle.titleColor(),
                        fontSize = 20.sp, lineHeight = 30.sp
                    )
                    KaTeXWebView(example2, getUIStyle)
                    Text(
                        text = returnToTop,
                        color = Color.Blue,
                        fontSize = 20.sp,
                        lineHeight = 21.sp,
                        modifier = Modifier
                            .clickable {
                                returnTo0 = true
                            }
                    )
                }
            },
            confirmButton = { },
            dismissButton = {
                SubmitButton(onClick = { pressed.value = false }, true, getUIStyle, "OK")
            },
            modifier = Modifier
                .fillMaxWidth(.975f)
                .fillMaxHeight(.80f)
        )
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
