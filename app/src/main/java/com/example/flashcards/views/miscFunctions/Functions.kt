package com.example.flashcards.views.miscFunctions

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.flashcards.R
import com.example.flashcards.controller.viewModels.CardListUiState
import com.example.flashcards.controller.viewModels.CardUiState
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import com.example.flashcards.ui.theme.textColor
import kotlinx.coroutines.delay

@Composable
fun EditTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    labelStr : String ,
    modifier: Modifier,
) {
    TextField(
        value = value,
        singleLine = true,
        modifier = modifier,
        onValueChange = onValueChanged,
        label = { Text(labelStr, color = textColor) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
    )
}

@Composable
fun LoadingText() {
    Text(
        stringResource(R.string.loading),
        fontSize = 35.sp,
        textAlign = TextAlign.Center,
        color = textColor,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun NoDueCards(getModifier: GetModifier) {
    Text(
        stringResource(R.string.no_due_cards),
        fontSize = 25.sp,
        lineHeight = 26.sp,
        textAlign = TextAlign.Center,
        color = getModifier.titleColor(),
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
fun BasicCardQuestion(basicCard: BasicCard) {
    Text(text = stringResource(R.string.question) + ": ${basicCard.question}")
}
@Composable
fun ThreeCardQuestion(threeFieldCard: ThreeFieldCard) {
    Text(text = stringResource(R.string.question) + ": ${threeFieldCard.question}")
}
@Composable
fun HintCardQuestion(hintCard: HintCard) {
    Text(text = stringResource(R.string.question) + ": ${hintCard.question}")
}

@Composable
fun ShowBackButtonAndDeckName(onNavigate: () -> Unit,
                              deck : Deck,
                              presetModifier : Modifier,
                              getModifier: GetModifier) {
    Row {
        BackButton(
            onBackClick = {
                onNavigate()
            },
            modifier = presetModifier
                .fillMaxSize(),
            getModifier = getModifier
        )
        Text(
            text = stringResource(R.string.deck) + ": ${deck.name}",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 35.sp,
            modifier = Modifier
                .padding(top = 16.dp, start = 8.dp, end = 8.dp)
                .fillMaxWidth()
            ,
            textAlign = TextAlign.Center,
            color = getModifier.buttonTextColor()
        )
    }
}

@Composable
fun CardSelector(cardListUiState : CardListUiState,
                 cardUiState: CardUiState,
                 index : Int) {
    if (cardListUiState.allCards.size == cardUiState.cardList.size) {
        when (cardUiState.cardList[index].type) {
            "basic" -> {
                val basicCard =
                    cardListUiState.allCards[index].basicCard
                basicCard?.let { BasicCardQuestion(basicCard) }
            }

            "three" -> {
                val threeCard =
                    cardListUiState.allCards[index].threeFieldCard
                threeCard?.let { ThreeCardQuestion(threeCard) }
            }

            "hint" -> {
                val hintCard =
                    cardListUiState.allCards[index].hintCard
                hintCard?.let { HintCardQuestion(hintCard) }
            }
        }
    }

}



suspend fun loading() : Boolean{
    delay(100)
    return false
}

suspend fun delayNavigate() {
    delay(75)
}
