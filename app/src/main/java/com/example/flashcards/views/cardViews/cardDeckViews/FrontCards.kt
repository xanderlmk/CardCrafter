package com.example.flashcards.views.cardViews.cardDeckViews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.model.tablesAndApplication.AllCardTypes
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCard
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import com.example.flashcards.ui.theme.GetModifier
import com.example.flashcards.R

@Composable
fun FrontCard(
    card: Pair<Card, AllCardTypes>,
    getModifier: GetModifier,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize() // Fill the entire available space
            .padding(10.dp)
    ) {
        when (card.first.type) {
            "basic" -> {
                val basicCard = card.second.basicCard
                basicCard?.let { BasicFrontCard(basicCard = it, getModifier) }
            }

            "three" -> {
                val threeCard = card.second.threeFieldCard
                threeCard?.let { ThreeFrontCard(threeCard = it, getModifier) }
            }

            "hint" -> {
                val hintCard = card.second.hintCard
                hintCard?.let { HintFrontCard(hintCard = it, getModifier) }
            }

            "multi" -> {
                val multiChoiceCard = card.second.multiChoiceCard
                multiChoiceCard?.let { ChoiceFrontCard(multiChoiceCard = it, getModifier) }
            }
        }
    }
}


@Composable
fun BasicFrontCard(
    basicCard: BasicCard,
    getModifier: GetModifier
) {
    Box {
        Text(
            text = basicCard.question,
            fontSize = 20.sp,
            lineHeight = 22.sp,
            color = getModifier.titleColor(),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 80.dp)
                .align(Alignment.TopCenter)
                .fillMaxWidth()
        )
    }
}

@Composable
fun ThreeFrontCard(
    threeCard: ThreeFieldCard,
    getModifier: GetModifier
) {
    Box {
        Text(
            text = threeCard.question,
            fontSize = 20.sp,
            lineHeight = 22.sp,
            color = getModifier.titleColor(),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 80.dp)
                .align(Alignment.TopCenter)
                .fillMaxWidth()
        )
    }
}

@Composable
fun HintFrontCard(
    hintCard: HintCard,
    getModifier: GetModifier
) {
    var isHintRevealed by remember { mutableStateOf(false) }
    Box(contentAlignment = Alignment.TopCenter) {
        Column {
            Text(
                text = hintCard.question,
                fontSize = 20.sp,
                lineHeight = 22.sp,
                color = getModifier.titleColor(),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 80.dp)
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
            )
            Text(
                text =
                if(isHintRevealed){hintCard.hint}
                else{stringResource(R.string.hint_field)},
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

@Composable
fun ChoiceFrontCard(
    multiChoiceCard: MultiChoiceCard,
    getModifier: GetModifier
) {
    Box(contentAlignment = Alignment.TopCenter) {
        Column {
            Text(
                text = multiChoiceCard.question,
                fontSize = 20.sp,
                lineHeight = 22.sp,
                color = getModifier.titleColor(),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 80.dp)
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
