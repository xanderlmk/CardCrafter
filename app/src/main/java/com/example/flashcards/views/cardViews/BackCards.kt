package com.example.flashcards.views.cardViews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.model.tablesAndApplication.AllCardTypes
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import com.example.flashcards.ui.theme.textColor


@Composable
fun BasicBackCard(basicCard: BasicCard){
    Column {
        Text(
            text = basicCard.question,
            fontSize = 30.sp,
            color = textColor,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 80.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = basicCard.answer,
            fontSize = 30.sp,
            color = textColor,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun ThreeBackCard(threeCard: ThreeFieldCard){
    Column {
        Text(
            text = threeCard.question,
            fontSize = 30.sp,
            color = textColor,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 80.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = threeCard.middle,
            fontSize = 30.sp,
            color = textColor,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = threeCard.answer,
            fontSize = 30.sp,
            color = textColor,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun HintBackCard(hintCard: HintCard){
    Column {
        Text(
            text = hintCard.question,
            fontSize = 30.sp,
            color = textColor,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 80.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = hintCard.answer,
            fontSize = 30.sp,
            color = textColor,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun BackCard(card : Pair<Card,AllCardTypes>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        when (card.first.type) {
            "basic" -> {
                val basicCard = card.second.basicCard
                basicCard?.let { BasicBackCard(basicCard = it) }
            }
            "three" -> {
                val threeCard = card.second.threeFieldCard
                threeCard?.let { ThreeBackCard(threeCard = it) }
            }
            "hint" ->{
                val hintCard = card.second.hintCard
                hintCard?.let { HintBackCard(hintCard = it) }
            }
        }
    }
}