package com.example.flashcards.views.cardViews.cardDeckViews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.CT
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.MathCard
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCard
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import com.example.flashcards.ui.theme.GetModifier
import com.example.flashcards.views.miscFunctions.symbols.RenderTextWithSymbols

@Composable
fun MathBackCard(mathCard: MathCard, getModifier: GetModifier) {
    val focusManager = LocalFocusManager.current
    Box {
        SelectionContainer(
            modifier = Modifier.clickable(
                interactionSource = null,
                indication = null
            ) { focusManager.clearFocus() }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RenderTextWithSymbols(mathCard.question, getModifier)
                if (mathCard.steps.isNotEmpty()) {
                    mathCard.steps.map {
                        RenderTextWithSymbols(it, getModifier)
                    }
                }
                RenderTextWithSymbols(mathCard.answer, getModifier)
            }

        }
    }
}

@Composable
fun BasicBackCard(
    basicCard: BasicCard,
    getModifier: GetModifier
) {
    val focusManager = LocalFocusManager.current // Get focus manager
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
                    text = basicCard.question,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getModifier.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = basicCard.answer,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getModifier.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun ThreeBackCard(
    threeCard: ThreeFieldCard,
    getModifier: GetModifier
) {
    val focusManager = LocalFocusManager.current // Get focus manager
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
                    text = threeCard.question,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getModifier.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = threeCard.middle,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getModifier.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = threeCard.answer,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getModifier.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun HintBackCard(
    hintCard: HintCard,
    getModifier: GetModifier
) {
    val focusManager = LocalFocusManager.current // Get focus manager
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
                    text = hintCard.question,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getModifier.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = hintCard.answer,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getModifier.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun ChoiceBackCard(
    multiChoiceCard: MultiChoiceCard,
    clickedChoice : Char,
    getModifier: GetModifier
) {
    val focusManager = LocalFocusManager.current // Get focus manager
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
                    color = getModifier.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = multiChoiceCard.choiceA,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getModifier.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 4.dp)
                        .background(
                            color =
                            if (clickedChoice == 'a' &&
                                multiChoiceCard.correct != clickedChoice
                            ) {
                                getModifier.pickedChoice()
                            } else if (
                                multiChoiceCard.correct == 'a' &&
                                clickedChoice != multiChoiceCard.correct
                            ) {
                                getModifier.correctChoice()
                            } else if (
                                clickedChoice == multiChoiceCard.correct &&
                                multiChoiceCard.correct == 'a'
                            ) {
                                getModifier.correctChoice()
                            } else {
                                getModifier.onTertiaryColor()
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .fillMaxWidth()
                )
                Text(
                    text = multiChoiceCard.choiceB,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getModifier.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 4.dp)
                        .background(
                            color =
                            if (clickedChoice == 'b' &&
                                multiChoiceCard.correct != clickedChoice
                            ) {
                                getModifier.pickedChoice()
                            } else if (
                                multiChoiceCard.correct == 'b' &&
                                clickedChoice != multiChoiceCard.correct
                            ) {
                                getModifier.correctChoice()
                            } else if (
                                clickedChoice == multiChoiceCard.correct &&
                                multiChoiceCard.correct == 'b'
                            ) {
                                getModifier.correctChoice()
                            } else {
                                getModifier.onTertiaryColor()
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
                        color = getModifier.titleColor(),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 4.dp)
                            .background(
                                color =
                                if (clickedChoice == 'c' &&
                                    multiChoiceCard.correct != clickedChoice
                                ) {
                                    getModifier.pickedChoice()
                                } else if (
                                    multiChoiceCard.correct == 'c' &&
                                    clickedChoice != multiChoiceCard.correct
                                ) {
                                    getModifier.correctChoice()
                                } else if (
                                    clickedChoice == multiChoiceCard.correct &&
                                    multiChoiceCard.correct == 'c'
                                ) {
                                    getModifier.correctChoice()
                                } else {
                                    getModifier.onTertiaryColor()
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
                        color = getModifier.titleColor(),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 4.dp)
                            .background(
                                color =
                                if (clickedChoice == 'd' &&
                                    multiChoiceCard.correct != clickedChoice
                                ) {
                                    getModifier.pickedChoice()
                                } else if (
                                    multiChoiceCard.correct == 'd' &&
                                    clickedChoice != multiChoiceCard.correct
                                ) {
                                    getModifier.correctChoice()
                                } else if (
                                    clickedChoice == multiChoiceCard.correct &&
                                    multiChoiceCard.correct == 'd'
                                ) {
                                    getModifier.correctChoice()
                                } else {
                                    getModifier.onTertiaryColor()
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
fun BackCard(
    ct: CT,
    getModifier: GetModifier,
    modifier: Modifier,
    clickedChoice: Char
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        when (ct) {
            is CT.Basic -> {
                BasicBackCard(basicCard = ct.basicCard, getModifier)
            }

            is CT.ThreeField -> {
                ThreeBackCard(threeCard = ct.threeFieldCard, getModifier)
            }

            is CT.Hint -> {
                HintBackCard(hintCard = ct.hintCard, getModifier)
            }

            is CT.MultiChoice -> {
                ChoiceBackCard(multiChoiceCard = ct.multiChoiceCard,
                    clickedChoice, getModifier)
            }
            is CT.Math -> {
                MathBackCard(mathCard = ct.mathCard, getModifier)
            }
        }
    }
}