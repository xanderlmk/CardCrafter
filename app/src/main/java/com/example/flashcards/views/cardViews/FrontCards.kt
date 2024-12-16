package com.example.flashcards.views.cardViews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.R
import com.example.flashcards.model.tablesAndApplication.AllCardTypes
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import com.example.flashcards.views.miscFunctions.GetModifier

@Composable
fun frontCard(card : Pair<Card, AllCardTypes>,
              getModifier : GetModifier) : Boolean {
    var clicked by remember { mutableStateOf(false ) }
    Box(
        modifier = Modifier
            .fillMaxSize() // Fill the entire available space
            .padding(16.dp)
    ) {
        when (card.first.type) {
            "basic" -> {
                val basicCard = card.second.basicCard
                basicCard?.let { BasicFrontCard(basicCard = it, getModifier) }
            }
            "three" -> {
                val threeCard = card.second.threeFieldCard
                threeCard?.let{ ThreeFrontCard(threeCard = it, getModifier)}
            }
            "hint" -> {
                val hintCard = card.second.hintCard
                hintCard?.let { HintFrontCard(hintCard = it,getModifier) }
            }
        }
        Button(
            onClick = {
                clicked = true
            },
            modifier = Modifier
                .align(Alignment.BottomCenter) // Align to the bottom center
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = getModifier.secondaryButtonColor(),
                contentColor = getModifier.buttonTextColor()
            )
        ) {
            Text(stringResource(R.string.show_answer))
        }
    }
    return clicked
}


@Composable
fun BasicFrontCard(basicCard: BasicCard,
                   getModifier: GetModifier){
    Box {
        Text(
            text = basicCard.question,
            fontSize = 30.sp,
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
fun ThreeFrontCard(threeCard: ThreeFieldCard,
                   getModifier: GetModifier){
    Box {
        Text(
            text = threeCard.question,
            fontSize = 30.sp,
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
fun HintFrontCard(hintCard: HintCard,
                  getModifier: GetModifier){
    var isHintRevealed by remember { mutableStateOf(false) }
    // The text to display based on whether the hint is revealed or not
    val textToShow = if (isHintRevealed) hintCard.hint else "Hint"

    Box(contentAlignment = Alignment.TopCenter) {
        Column {
            Text(
                text = hintCard.question,
                fontSize = 30.sp,
                color = getModifier.titleColor(),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 80.dp)
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
            )
            Text(
                text = textToShow,
                fontSize = 30.sp,
                color = getModifier.titleColor(),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        // Toggle the hint visibility
                        isHintRevealed = !isHintRevealed
                    }
                    .background(color = Color.LightGray)
            )
        }
    }
}

