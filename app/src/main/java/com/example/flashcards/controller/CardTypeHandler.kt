package com.example.flashcards.controller

import androidx.compose.runtime.Composable
import com.example.flashcards.controller.viewModels.BasicCardViewModel
import com.example.flashcards.controller.viewModels.CardListUiState
import com.example.flashcards.controller.viewModels.HintCardViewModel
import com.example.flashcards.controller.viewModels.ThreeCardViewModel
import com.example.flashcards.model.Fields
import com.example.flashcards.model.tablesAndApplication.AllCardTypes
import com.example.flashcards.views.editCardViews.EditBasicCard
import com.example.flashcards.views.editCardViews.EditHintCard
import com.example.flashcards.views.editCardViews.EditThreeCard
import com.example.flashcards.views.miscFunctions.NullCardLoading

interface CardTypeHandler {
    @Composable
    fun HandleCardEdit(
        cardListUiState: CardListUiState,
        cardId: Int,
        onDismiss: () -> Unit,
        cardTypes : Triple<BasicCardViewModel,
                ThreeCardViewModel, HintCardViewModel>,
        fields: Fields
    )
}

class BasicCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        cardListUiState: CardListUiState,
        cardId: Int,
        onDismiss: () -> Unit,
        cardTypes : Triple<BasicCardViewModel,
                ThreeCardViewModel, HintCardViewModel>,
        fields: Fields
    ) {
        // Logic specific to handling a basic card
        val basicCard = cardListUiState.allCards.find {
            it.card.id == cardId }?.basicCard
        basicCard?.let { basicCard ->
            EditBasicCard(basicCard, onDismiss, cardTypes.first, fields)
        }
    }
}

class ThreeCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        cardListUiState: CardListUiState,
        cardId: Int,
        onDismiss: () -> Unit,
        cardTypes : Triple<BasicCardViewModel,
                ThreeCardViewModel, HintCardViewModel>,
        fields: Fields
    ) {
        // Logic specific to handling a basic card
        val threeCard = cardListUiState.allCards.find {
            it.card.id == cardId }?.threeFieldCard
        threeCard?.let { threeCard ->
            EditThreeCard(threeCard, onDismiss, cardTypes.second, fields)
        }
    }
}

class HintCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        cardListUiState: CardListUiState,
        cardId: Int,
        onDismiss: () -> Unit,
        cardTypes : Triple<BasicCardViewModel,
                ThreeCardViewModel, HintCardViewModel>,
        fields: Fields
    ) {
        // Logic specific to handling a basic card
        val hintCard = cardListUiState.allCards.find {
            it.card.id == cardId }?.hintCard

        hintCard?.let { hintCard ->
            EditHintCard(hintCard, onDismiss, cardTypes.third, fields)
        }
    }
}

class NullType : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        cardListUiState: CardListUiState,
        cardId: Int,
        onDismiss: () -> Unit,
        cardTypes: Triple<BasicCardViewModel, ThreeCardViewModel, HintCardViewModel>,
        fields: Fields
    ) {
        NullCardLoading()
    }
}



