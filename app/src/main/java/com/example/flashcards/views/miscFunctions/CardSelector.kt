package com.example.flashcards.views.miscFunctions

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.flashcards.R
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCard
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import com.example.flashcards.model.uiModels.CardListUiState

@Composable
fun BasicCardQuestion(basicCard: BasicCard) {
    Text(
        text = stringResource(R.string.question) + ": ${basicCard.question}",
        maxLines = 3,
        overflow = TextOverflow.Ellipsis

    )
}

@Composable
fun ThreeCardQuestion(threeFieldCard: ThreeFieldCard) {
    Text(
        text = stringResource(R.string.question) + ": ${threeFieldCard.question}",
        maxLines = 3,
        overflow = TextOverflow.Companion.Ellipsis

    )
}

@Composable
fun HintCardQuestion(hintCard: HintCard) {
    Text(
        text = stringResource(R.string.question) + ": ${hintCard.question}",
        maxLines = 3,
        overflow = TextOverflow.Companion.Ellipsis

    )
}

@Composable
fun ChoiceCardQuestion(multiChoiceCard: MultiChoiceCard) {
    Text(
        text = stringResource(R.string.question) + ": ${multiChoiceCard.question}",
        maxLines = 3,
        overflow = TextOverflow.Companion.Ellipsis
    )
}

@Composable
fun CardSelector(
    cardListUiState: CardListUiState,
    index: Int
) {
    when (cardListUiState.allCards[index].card.type) {
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

        "multi" -> {
            val choiceCard =
                cardListUiState.allCards[index].multiChoiceCard
            choiceCard?.let { ChoiceCardQuestion(choiceCard) }
        }
    }
}
