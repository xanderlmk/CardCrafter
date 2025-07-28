package com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.zIndex
import com.belmontCrest.cardCrafter.localDatabase.tables.NotationCard
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.katex.KaTeXWebView

private const val line = "$$\\\\text{---}\\\\text{---}\\\\text{---}\\\\text{---}\\\\text{---}" +
        "\\\\text{---}\\\\text{---}\\\\text{---}$$"

@Composable
fun NotationFrontCard(notationCard: NotationCard, getUIStyle: GetUIStyle) {
    val focusManager = LocalFocusManager.current
    Box(contentAlignment = Alignment.TopCenter) {
        SelectionContainer(
            modifier = Modifier.clickable(
                interactionSource = null,
                indication = null
            ) { focusManager.clearFocus() }
        ) {
            KaTeXWebView(
                notationCard.question, getUIStyle, Modifier
                    .zIndex(-1f)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun NotationBackCard(notationCard: NotationCard, getUIStyle: GetUIStyle) {
    val focusManager = LocalFocusManager.current
    Box(contentAlignment = Alignment.TopCenter) {
        SelectionContainer(
            modifier = Modifier.clickable(
                interactionSource = null,
                indication = null
            ) { focusManager.clearFocus() }
        ) {
            val longString = buildString {
                append(notationCard.question)
                append(line)
                if (notationCard.steps.isNotEmpty()) {
                    notationCard.steps.mapIndexed { index, step ->
                        append("<p>Step ${index + 1}: $step</p>")
                        if (index == notationCard.steps.lastIndex) {
                            append(line)
                        }
                    }
                }
                append("<p>${notationCard.answer}</p>")
            }
            KaTeXWebView(
                longString, getUIStyle, Modifier
                    .zIndex(-1f)
                    .fillMaxWidth()
            )
        }
    }
}