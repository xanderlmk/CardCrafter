package com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.zIndex
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.AnswerParam
import com.belmontCrest.cardCrafter.localDatabase.tables.CustomCard
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.MiddleParam
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.Param
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.katex.KaTeXWebView
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.customCardParams.AudioPlayerButton
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.customCardParams.BackChoiceListView
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.customCardParams.CardStringView
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.customCardParams.FrontChoiceListView
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.customCardParams.HintStringView
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.customCardParams.ImageView
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.customCardParams.ParamPair
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.customCardParams.StringListView

@Composable
fun CustomFrontCard(
    customCard: CustomCard, getUIStyle: GetUIStyle, clickedChoice: MutableState<Char>
) {
    val focusManager = LocalFocusManager.current // Get focus manager
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        SelectionContainer(
            modifier = Modifier.clickable(
                interactionSource = null, indication = null
            ) { focusManager.clearFocus() }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ParamedView(customCard.question, getUIStyle)
                CustomCardMiddle(customCard.middle, getUIStyle, true, clickedChoice)
            }
        }
    }
}


@Composable
fun CustomBackCard(
    customCard: CustomCard, getUIStyle: GetUIStyle, clickedChoice: MutableState<Char>
) {
    val focusManager = LocalFocusManager.current // Get focus manager
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        SelectionContainer(
            modifier = Modifier.clickable(
                interactionSource = null, indication = null
            ) { focusManager.clearFocus() }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ParamedView(customCard.question, getUIStyle)
                CustomCardMiddle(customCard.middle, getUIStyle, false, clickedChoice)
                CustomCardAnswer(customCard.answer, getUIStyle)
            }
        }
    }
}

private const val line = "$$\\\\text{---}\\\\text{---}\\\\text{---}\\\\text{---}\\\\text{---}" +
        "\\\\text{---}\\\\text{---}\\\\text{---}$$"

@Composable
fun CustomCardAnswer(answer: AnswerParam, getUIStyle: GetUIStyle) {
    when (val a = answer) {
        is AnswerParam.NotationList -> {
            val longString = buildString {
                append(line)
                a.steps.mapIndexed { index, step ->
                    append("<p>Step ${index + 1}: $step</p>")
                    if (index == a.steps.lastIndex) {
                        append(line)
                    }
                }
                append(a.a)
            }
            KaTeXWebView(
                longString, getUIStyle, Modifier
                    .zIndex(-1f)
                    .fillMaxWidth()
            )
        }

        is AnswerParam.WithParam -> {
            ParamedView(a.param, getUIStyle)
        }
    }
}

@Composable
fun CustomCardMiddle(
    middle: MiddleParam, getUIStyle: GetUIStyle,
    front: Boolean, clickedChoice: MutableState<Char>
) {
    val focusManager = LocalFocusManager.current

    when (val m = middle) {
        is MiddleParam.Choice -> {
            if (front) FrontChoiceListView(m.choices, clickedChoice, getUIStyle, focusManager)
            else BackChoiceListView(m.choices, clickedChoice.value, m.correct, getUIStyle)
        }

        MiddleParam.Empty -> return
        is MiddleParam.Hint -> {
            if (front) HintStringView(m.h, getUIStyle, focusManager) else return
        }

        is MiddleParam.WithParam -> ParamedView(m.param, getUIStyle)
    }
}

@Composable
fun ParamedView(param: Param, getUIStyle: GetUIStyle) {
    when (param) {
        is Param.Type.Audio -> AudioPlayerButton(param.uri, getUIStyle)
        is Param.Type.Image -> ImageView(param.uri)
        is Param.Pair -> ParamPair(param, getUIStyle)
        is Param.Type.String -> CardStringView(param.s, getUIStyle)
        is Param.Type.StringList -> StringListView(param.list, getUIStyle)
        is Param.Type.Notation -> KaTeXWebView(
            param.s, getUIStyle, Modifier
                .zIndex(-1f)
                .fillMaxWidth()
        )
    }
}

