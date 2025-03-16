package com.example.flashcards.views.cardViews.cardDeckViews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCard
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import com.example.flashcards.ui.theme.GetUIStyle
import com.example.flashcards.R
import com.example.flashcards.model.tablesAndApplication.CT
import com.example.flashcards.model.tablesAndApplication.MathCard
import com.example.flashcards.views.miscFunctions.symbols.RenderTextWithSymbols

@Composable
fun FrontCard(
    ct: CT,
    getUIStyle: GetUIStyle,
    clickedChoice: MutableState<Char>,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        when (ct) {
            is CT.Basic -> {
                BasicFrontCard(basicCard = ct.basicCard, getUIStyle)
            }

            is CT.ThreeField -> {
                ThreeFrontCard(threeCard = ct.threeFieldCard, getUIStyle)
            }

            is CT.Hint -> {
                HintFrontCard(hintCard = ct.hintCard, getUIStyle)
            }

            is CT.MultiChoice -> {
                ChoiceFrontCard(
                    multiChoiceCard = ct.multiChoiceCard,
                    clickedChoice = clickedChoice,
                    getUIStyle = getUIStyle
                )
            }

            is CT.Math -> {
                MathFrontCard(mathCard = ct.mathCard, getUIStyle)
            }
        }
    }
}

@Composable
fun BasicFrontCard(
    basicCard: BasicCard,
    getUIStyle: GetUIStyle
) {
    val focusManager = LocalFocusManager.current // Get focus manager
    Box {
        SelectionContainer(
            modifier = Modifier.clickable(
                interactionSource = null,
                indication = null
            ) { focusManager.clearFocus() }
        ) {
            Text(
                text = basicCard.question,
                fontSize = 20.sp,
                lineHeight = 22.sp,
                color = getUIStyle.titleColor(),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun ThreeFrontCard(
    threeCard: ThreeFieldCard,
    getUIStyle: GetUIStyle
) {
    val focusManager = LocalFocusManager.current // Get focus manager
    Box {
        SelectionContainer(
            modifier = Modifier.clickable(
                interactionSource = null,
                indication = null
            ) { focusManager.clearFocus() }
        ) {
            Text(
                text = threeCard.question,
                fontSize = 20.sp,
                lineHeight = 22.sp,
                color = getUIStyle.titleColor(),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun HintFrontCard(
    hintCard: HintCard,
    getUIStyle: GetUIStyle
) {
    val focusManager = LocalFocusManager.current // Get focus manager
    var isHintRevealed by rememberSaveable { mutableStateOf(false) }
    Box(
        contentAlignment = Alignment.TopCenter,
    ) {
        SelectionContainer(
            modifier = Modifier
                .clickable(
                    interactionSource = null,
                    indication = null
                ) { focusManager.clearFocus() }
        ) {
            Column {
                Text(
                    text = hintCard.question,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getUIStyle.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                )
                Text(
                    text =
                    if (isHintRevealed) {
                        hintCard.hint
                    } else {
                        stringResource(R.string.hint_field)
                    },
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getUIStyle.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            // Toggle the hint visibility
                            isHintRevealed = !isHintRevealed
                            focusManager.clearFocus()
                        }
                        .background(
                            color = getUIStyle.onTertiaryColor(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ChoiceFrontCard(
    multiChoiceCard: MultiChoiceCard,
    getUIStyle: GetUIStyle,
    clickedChoice: MutableState<Char>
) {
    val focusManager = LocalFocusManager.current // Get focus manager
    Box(
        contentAlignment = Alignment.TopCenter,
    ) {
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
                Text(
                    text = multiChoiceCard.choiceA,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getUIStyle.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 4.dp)
                        .clickable {
                            clickedChoice.value = 'a'
                            focusManager.clearFocus()
                        }
                        .background(
                            color = if (clickedChoice.value == 'a') {
                                getUIStyle.pickedChoice()
                            } else {
                                getUIStyle.onTertiaryColor()
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .fillMaxWidth()
                )
                Text(
                    text = multiChoiceCard.choiceB,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getUIStyle.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 4.dp)
                        .clickable {
                            clickedChoice.value = 'b'
                            focusManager.clearFocus()
                        }
                        .background(
                            color = if (clickedChoice.value == 'b') {
                                getUIStyle.pickedChoice()
                            } else {
                                getUIStyle.onTertiaryColor()
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .fillMaxWidth()

                )
                if (multiChoiceCard.choiceC.isNotBlank()) {
                    Text(
                        text = multiChoiceCard.choiceC,
                        fontSize = 20.sp,
                        lineHeight = 22.sp,
                        color = getUIStyle.titleColor(),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 4.dp)
                            .clickable {
                                clickedChoice.value = 'c'
                                focusManager.clearFocus()
                            }
                            .background(
                                color = if (clickedChoice.value == 'c') {
                                    getUIStyle.pickedChoice()
                                } else {
                                    getUIStyle.onTertiaryColor()
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .fillMaxWidth()
                    )
                }
                if (multiChoiceCard.choiceD.isNotBlank()) {
                    Text(
                        text = multiChoiceCard.choiceD,
                        fontSize = 20.sp,
                        lineHeight = 22.sp,
                        color = getUIStyle.titleColor(),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 4.dp)
                            .clickable {
                                clickedChoice.value = 'd'
                                focusManager.clearFocus()
                            }
                            .background(
                                color = if (clickedChoice.value == 'd') {
                                    getUIStyle.pickedChoice()
                                } else {
                                    getUIStyle.onTertiaryColor()
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun MathFrontCard(mathCard: MathCard, getUIStyle: GetUIStyle) {
    val focusManager = LocalFocusManager.current
    Box {
        SelectionContainer(
            modifier = Modifier.clickable(
                interactionSource = null,
                indication = null
            ) { focusManager.clearFocus() }
        ) {
            RenderTextWithSymbols(mathCard.question, getUIStyle)
        }
    }
}

