package com.example.flashcards.controller

import androidx.compose.runtime.Composable
import com.example.flashcards.controller.navigation.AllTypesUiStates
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.views.cardViews.editCardViews.EditBasicCard
import com.example.flashcards.views.cardViews.editCardViews.EditHintCard
import com.example.flashcards.views.cardViews.editCardViews.EditThreeCard
import com.example.flashcards.ui.theme.GetModifier
import com.example.flashcards.views.cardViews.editCardViews.EditChoiceCard

interface CardTypeHandler {
    @Composable
    fun HandleCardEdit(
        cardId: Int,
        fields: Fields,
        state: AllTypesUiStates,
        getModifier: GetModifier
    )
}

class BasicCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        cardId: Int,
        fields: Fields,
        state: AllTypesUiStates,
        getModifier: GetModifier
    ) {
        val basicCard = state.basicCardUiState.basicCards.find {
            it.card.id == cardId
        }?.basicCard
        basicCard?.let {
            EditBasicCard(basicCard, fields)
        }
    }
}

class ThreeCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        cardId: Int,
        fields: Fields,
        state: AllTypesUiStates,
        getModifier: GetModifier
    ) {
        val threeCard =
            state.threeCardUiState.threeFieldCards.find {
                it.card.id == cardId
            }?.threeFieldCard
        threeCard?.let {
            EditThreeCard(threeCard, fields)
        }
    }
}

class HintCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        cardId: Int,
        fields: Fields,
        state: AllTypesUiStates,
        getModifier: GetModifier
    ) {
        val hintCard =
            state.hintUiStates.hintCards.find {
                it.card.id == cardId
            }?.hintCard

        hintCard?.let {
            EditHintCard(hintCard, fields)
        }
    }
}

class ChoiceCardTypeHandler : CardTypeHandler {
    @Composable
    override fun HandleCardEdit(
        cardId: Int,
        fields: Fields,
        state: AllTypesUiStates,
        getModifier: GetModifier
    ) {
        val choiceCard =
            state.multiChoiceUiCardState.multiChoiceCard.find {
                it.card.id == cardId
            }?.multiChoiceCard
        choiceCard?.let {
            EditChoiceCard(choiceCard, fields, getModifier)
        }
    }
}





