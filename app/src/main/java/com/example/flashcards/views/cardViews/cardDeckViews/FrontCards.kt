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
import com.example.flashcards.ui.theme.GetModifier
import com.example.flashcards.R
import com.example.flashcards.model.tablesAndApplication.CT
import com.example.flashcards.model.tablesAndApplication.MathCard

@Composable
fun FrontCard(
    ct: CT,
    getModifier: GetModifier,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        when (ct) {
            is CT.Basic -> {
                BasicFrontCard(basicCard = ct.basicCard, getModifier)
            }

            is CT.ThreeField -> {
                ThreeFrontCard(threeCard = ct.threeFieldCard, getModifier)
            }

            is CT.Hint -> {
                HintFrontCard(hintCard = ct.hintCard, getModifier)
            }

            is CT.MultiChoice -> {
                ChoiceFrontCard(multiChoiceCard = ct.multiChoiceCard, getModifier)
            }
            is CT.Math -> {
                MathFrontCard(mathCard = ct.mathCard, getModifier)
            }
        }
    }
}

@Composable
fun BasicFrontCard(
    basicCard: BasicCard,
    getModifier: GetModifier
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
                color = getModifier.titleColor(),
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
    getModifier: GetModifier
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
                color = getModifier.titleColor(),
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
    getModifier: GetModifier
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
                    color = getModifier.titleColor(),
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
                    color = getModifier.titleColor(),
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
                            color = getModifier.onTertiaryColor(),
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
    getModifier: GetModifier
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
                    color = getModifier.titleColor(),
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
                    color = getModifier.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 4.dp)
                        .clickable {
                            getModifier.clickedChoice.value = 'a'
                            focusManager.clearFocus()
                        }
                        .background(
                            color = if (getModifier.clickedChoice.value == 'a') {
                                getModifier.pickedChoice()
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
                        .clickable {
                            getModifier.clickedChoice.value = 'b'
                            focusManager.clearFocus()
                        }
                        .background(
                            color = if (getModifier.clickedChoice.value == 'b') {
                                getModifier.pickedChoice()
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
                            .clickable {
                                getModifier.clickedChoice.value = 'c'
                                focusManager.clearFocus()
                            }
                            .background(
                                color = if (getModifier.clickedChoice.value == 'c') {
                                    getModifier.pickedChoice()
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
                            .clickable {
                                getModifier.clickedChoice.value = 'd'
                                focusManager.clearFocus()
                            }
                            .background(
                                color = if (getModifier.clickedChoice.value == 'd') {
                                    getModifier.pickedChoice()
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
fun MathFrontCard(mathCard: MathCard, getModifier: GetModifier) {
    val focusManager = LocalFocusManager.current
    Box {
        SelectionContainer(
            modifier = Modifier.clickable(
                interactionSource = null,
                indication = null
            ) { focusManager.clearFocus() }
        ) {
            RenderTextWithSymbols(mathCard.question, getModifier)
        }
    }
}

