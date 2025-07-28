package com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.belmontCrest.cardCrafter.localDatabase.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.customCardParams.BackChoiceView
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.customCardParams.FrontChoiceView

@Composable
fun ChoiceFrontCard(
    multiChoiceCard: MultiChoiceCard,
    getUIStyle: GetUIStyle,
    clickedChoice: MutableState<Char>
) {
    val focusManager = LocalFocusManager.current // Get focus manager
    Box(contentAlignment = Alignment.TopCenter) {
        SelectionContainer(
            modifier = Modifier
                .clickable(
                    interactionSource = null,
                    indication = null
                ) { focusManager.clearFocus() }
        ) {
            Column {
                Text(
                    text = multiChoiceCard.question,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getUIStyle.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                )
                FrontChoiceView(multiChoiceCard.choiceA, clickedChoice.value, 'a', getUIStyle) {
                    clickedChoice.value = 'a'; focusManager.clearFocus()
                }
                FrontChoiceView(multiChoiceCard.choiceB, clickedChoice.value, 'b', getUIStyle) {
                    clickedChoice.value = 'b'; focusManager.clearFocus()
                }
                if (multiChoiceCard.choiceC.isNotBlank()) {
                    FrontChoiceView(multiChoiceCard.choiceC, clickedChoice.value, 'c', getUIStyle) {
                        clickedChoice.value = 'c'; focusManager.clearFocus()
                    }
                }
                if (multiChoiceCard.choiceD.isNotBlank()) {
                    FrontChoiceView(multiChoiceCard.choiceD, clickedChoice.value, 'd', getUIStyle) {
                        clickedChoice.value = 'd'; focusManager.clearFocus()
                    }
                }
            }
        }
    }
}

@Composable
fun ChoiceBackCard(multiChoiceCard: MultiChoiceCard, clickedChoice: Char, getUIStyle: GetUIStyle) {
    val focusManager = LocalFocusManager.current
    Box {
        SelectionContainer(
            modifier = Modifier
                .clickable(
                    interactionSource = null,
                    indication = null
                ) { focusManager.clearFocus() }
        ) {
            Column {
                Text(
                    text = multiChoiceCard.question,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getUIStyle.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                BackChoiceView(
                    choice = multiChoiceCard.choiceA, clickedChoice = clickedChoice,
                    correct = multiChoiceCard.correct,
                    letter = 'a', getUIStyle = getUIStyle
                )
                BackChoiceView(
                    choice = multiChoiceCard.choiceB, clickedChoice = clickedChoice,
                    correct = multiChoiceCard.correct,
                    letter = 'b', getUIStyle = getUIStyle
                )
                if (multiChoiceCard.choiceC.isNotBlank()) {
                    BackChoiceView(
                        choice = multiChoiceCard.choiceC, clickedChoice = clickedChoice,
                        correct = multiChoiceCard.correct,
                        letter = 'c', getUIStyle = getUIStyle
                    )
                }
                if (multiChoiceCard.choiceD.isNotBlank()) {
                    BackChoiceView(
                        choice = multiChoiceCard.choiceD,
                        clickedChoice = clickedChoice,
                        correct = multiChoiceCard.correct,
                        letter = 'd', getUIStyle = getUIStyle
                    )
                }
            }
        }
    }
}
