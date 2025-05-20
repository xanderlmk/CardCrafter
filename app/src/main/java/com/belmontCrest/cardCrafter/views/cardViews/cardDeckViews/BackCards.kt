package com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews

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
import com.belmontCrest.cardCrafter.localDatabase.tables.BasicCard
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.HintCard
import com.belmontCrest.cardCrafter.localDatabase.tables.NotationCard
import com.belmontCrest.cardCrafter.localDatabase.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.localDatabase.tables.ThreeFieldCard
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.symbols.KaTeXWebView

private const val line = "$$\\\\text{---}\\\\text{---}\\\\text{---}\\\\text{---}\\\\text{---}" +
        "\\\\text{---}\\\\text{---}\\\\text{---}$$"

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
            KaTeXWebView(longString, getUIStyle)
        }
    }
}

@Composable
fun BasicBackCard(
    basicCard: BasicCard,
    getUIStyle: GetUIStyle
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
                    color = getUIStyle.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = basicCard.answer,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getUIStyle.titleColor(),
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
    getUIStyle: GetUIStyle
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
                    color = getUIStyle.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = threeCard.middle,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getUIStyle.titleColor(),
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
                    color = getUIStyle.titleColor(),
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
    getUIStyle: GetUIStyle
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
                    color = getUIStyle.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = hintCard.answer,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getUIStyle.titleColor(),
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
    clickedChoice: Char,
    getUIStyle: GetUIStyle
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
                    color = getUIStyle.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
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
                        .background(
                            color =
                                if (clickedChoice == 'a' &&
                                    multiChoiceCard.correct != clickedChoice
                                ) {
                                    getUIStyle.pickedChoice()
                                } else if (
                                    multiChoiceCard.correct == 'a' &&
                                    clickedChoice != multiChoiceCard.correct
                                ) {
                                    getUIStyle.correctChoice()
                                } else if (
                                    clickedChoice == multiChoiceCard.correct &&
                                    multiChoiceCard.correct == 'a'
                                ) {
                                    getUIStyle.correctChoice()
                                } else {
                                    getUIStyle.choiceColor()
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
                        .background(
                            color =
                                if (clickedChoice == 'b' &&
                                    multiChoiceCard.correct != clickedChoice
                                ) {
                                    getUIStyle.pickedChoice()
                                } else if (
                                    multiChoiceCard.correct == 'b' &&
                                    clickedChoice != multiChoiceCard.correct
                                ) {
                                    getUIStyle.correctChoice()
                                } else if (
                                    clickedChoice == multiChoiceCard.correct &&
                                    multiChoiceCard.correct == 'b'
                                ) {
                                    getUIStyle.correctChoice()
                                } else {
                                    getUIStyle.choiceColor()
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
                            .background(
                                color =
                                    if (clickedChoice == 'c' &&
                                        multiChoiceCard.correct != clickedChoice
                                    ) {
                                        getUIStyle.pickedChoice()
                                    } else if (
                                        multiChoiceCard.correct == 'c' &&
                                        clickedChoice != multiChoiceCard.correct
                                    ) {
                                        getUIStyle.correctChoice()
                                    } else if (
                                        clickedChoice == multiChoiceCard.correct &&
                                        multiChoiceCard.correct == 'c'
                                    ) {
                                        getUIStyle.correctChoice()
                                    } else {
                                        getUIStyle.choiceColor()
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
                            .background(
                                color =
                                    if (clickedChoice == 'd' &&
                                        multiChoiceCard.correct != clickedChoice
                                    ) {
                                        getUIStyle.pickedChoice()
                                    } else if (
                                        multiChoiceCard.correct == 'd' &&
                                        clickedChoice != multiChoiceCard.correct
                                    ) {
                                        getUIStyle.correctChoice()
                                    } else if (
                                        clickedChoice == multiChoiceCard.correct &&
                                        multiChoiceCard.correct == 'd'
                                    ) {
                                        getUIStyle.correctChoice()
                                    } else {
                                        getUIStyle.choiceColor()
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
    getUIStyle: GetUIStyle,
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
                BasicBackCard(basicCard = ct.basicCard, getUIStyle)
            }

            is CT.ThreeField -> {
                ThreeBackCard(threeCard = ct.threeFieldCard, getUIStyle)
            }

            is CT.Hint -> {
                HintBackCard(hintCard = ct.hintCard, getUIStyle)
            }

            is CT.MultiChoice -> {
                ChoiceBackCard(
                    multiChoiceCard = ct.multiChoiceCard,
                    clickedChoice, getUIStyle
                )
            }

            is CT.Notation -> {
                NotationBackCard(notationCard = ct.notationCard, getUIStyle)
            }
        }
    }
}