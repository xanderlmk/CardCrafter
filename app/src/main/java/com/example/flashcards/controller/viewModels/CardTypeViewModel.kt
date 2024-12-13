package com.example.flashcards.controller.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.repositories.CardTypeRepository
import com.example.flashcards.model.tablesAndApplication.AllCardTypes
import com.example.flashcards.model.tablesAndApplication.BasicCardType
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.HintCardType
import com.example.flashcards.model.tablesAndApplication.ThreeCardType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CardTypeViewModel(private val cardTypeRepository: CardTypeRepository): ViewModel() {

    var basicCardUiState = MutableStateFlow(BasicCardUiState())

    var threeCardUiState = MutableStateFlow(ThreeCardUiState())

    var hintCardUiState = MutableStateFlow(HintCardUiState())

    var cardListUiState = MutableStateFlow(CardListUiState())

    fun getDueTypesForDeck(deckId: Int){
        viewModelScope.launch {
            cardTypeRepository.getDueAllCardTypes(deckId).map { allCards ->
                CardListUiState(allCards =allCards)
            }.collect { uiState ->
                cardListUiState.value = uiState
            }
        }
    }
    fun getAllTypesForDeck(deckId: Int){
        viewModelScope.launch {
            cardTypeRepository.getAllCardTypes(deckId).map { allCards ->
                CardListUiState(allCards =allCards)
            }.collect { uiState ->
                cardListUiState.value = uiState
            }
        }
    }
}

data class BasicCardUiState(var basicCards: List<BasicCardType> = emptyList())

data class ThreeCardUiState(var threeFieldCards: List<ThreeCardType> = emptyList())

data class HintCardUiState(var hintCards: List<HintCardType> = emptyList())

data class CardListUiState(var allCards: List<AllCardTypes> = emptyList())

