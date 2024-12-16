package com.example.flashcards.controller

import androidx.compose.runtime.Composable
import com.example.flashcards.controller.viewModels.CardListUiState
import com.example.flashcards.model.Fields
import com.example.flashcards.views.editCardViews.EditBasicCard
import com.example.flashcards.views.editCardViews.EditHintCard
import com.example.flashcards.views.editCardViews.EditThreeCard
import com.example.flashcards.views.miscFunctions.GetModifier

interface CardTypeHandler {
    @Composable
    fun HandleCardEdit(
        cardListUiState: CardListUiState,
        cardId: Int,
        fields: Fields,
        getModifier: GetModifier
    )
}

class BasicCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        cardListUiState: CardListUiState,
        cardId: Int,
        fields: Fields,
        getModifier : GetModifier
    ) {
        // Logic specific to handling a basic card
        val basicCard = cardListUiState.allCards.find {
            it.card.id == cardId }?.basicCard
        basicCard?.let {
            EditBasicCard(basicCard, fields, getModifier)
        }
    }
}

class ThreeCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        cardListUiState: CardListUiState,
        cardId: Int,
        fields: Fields,
        getModifier: GetModifier
    ) {
        // Logic specific to handling a basic card
        val threeCard = cardListUiState.allCards.find {
            it.card.id == cardId }?.threeFieldCard
        threeCard?.let {
            EditThreeCard(threeCard, fields,getModifier)
        }
    }
}

class HintCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        cardListUiState: CardListUiState,
        cardId: Int,
        fields: Fields,
        getModifier: GetModifier
    ) {
        // Logic specific to handling a basic card
        val hintCard = cardListUiState.allCards.find {
            it.card.id == cardId }?.hintCard

        hintCard?.let {
            EditHintCard(hintCard, fields, getModifier)
        }
    }
}




