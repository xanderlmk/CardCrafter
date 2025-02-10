package com.example.flashcards.views.cardViews.cardDeckViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.CT
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCard
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import com.example.flashcards.ui.theme.GetModifier


@Composable
fun BasicBackCard(basicCard: BasicCard,
                  getModifier: GetModifier){
    Column {
        Text(
            text = basicCard.question,
            fontSize = 20.sp,
            lineHeight = 22.sp,
            color = getModifier.titleColor(),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 80.dp)
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

@Composable
fun ThreeBackCard(threeCard: ThreeFieldCard,
                  getModifier: GetModifier){
    Column {
        Text(
            text = threeCard.question,
            fontSize = 20.sp,
            lineHeight = 22.sp,
            color = getModifier.titleColor(),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 80.dp)
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

@Composable
fun HintBackCard(hintCard: HintCard,
                 getModifier: GetModifier){
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

@Composable
fun ChoiceBackCard(multiChoiceCard: MultiChoiceCard,
                  getModifier: GetModifier){
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
                .background(color =
                if (getModifier.clickedChoice.value == 'a' &&
                    multiChoiceCard.correct != getModifier.clickedChoice.value) {
                    getModifier.pickedChoice()
                } else if (getModifier.clickedChoice.value == 'a' &&
                    multiChoiceCard.correct == getModifier.clickedChoice.value){
                    getModifier.correctChoice()
                } else if (multiChoiceCard.correct == 'a' &&
                    multiChoiceCard.correct != getModifier.clickedChoice.value){
                    getModifier.correctChoice()
                } else{ getModifier.onTertiaryColor()},
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
                .background(color =
                if (getModifier.clickedChoice.value == 'b' &&
                    multiChoiceCard.correct != getModifier.clickedChoice.value) {
                    getModifier.pickedChoice()
                } else if (getModifier.clickedChoice.value == 'b' &&
                    multiChoiceCard.correct == getModifier.clickedChoice.value){
                    getModifier.correctChoice()
                } else if (multiChoiceCard.correct == 'b' &&
                    multiChoiceCard.correct != getModifier.clickedChoice.value){
                    getModifier.correctChoice()
                } else { getModifier.onTertiaryColor()},
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
                        if (getModifier.clickedChoice.value == 'c' &&
                            multiChoiceCard.correct != getModifier.clickedChoice.value
                        ) {
                            getModifier.pickedChoice()
                        } else if (getModifier.clickedChoice.value == 'c' &&
                            multiChoiceCard.correct == getModifier.clickedChoice.value
                        ) {
                            getModifier.correctChoice()
                        } else if (multiChoiceCard.correct == 'c' &&
                            multiChoiceCard.correct != getModifier.clickedChoice.value
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
                        if (getModifier.clickedChoice.value == 'd' &&
                            multiChoiceCard.correct != getModifier.clickedChoice.value
                        ) {
                            getModifier.pickedChoice()
                        } else if (getModifier.clickedChoice.value == 'd' &&
                            multiChoiceCard.correct == getModifier.clickedChoice.value
                        ) {
                            getModifier.correctChoice()
                        } else if (multiChoiceCard.correct == 'd' &&
                            multiChoiceCard.correct != getModifier.clickedChoice.value
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

@Composable
fun BackCard(ct : CT,
             getModifier: GetModifier,
             modifier: Modifier) {
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
               ChoiceBackCard(multiChoiceCard = ct.multiChoiceCard, getModifier)
            }
        }
    }
}