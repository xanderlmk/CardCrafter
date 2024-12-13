package com.example.flashcards.controller.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcards.controller.AppViewModelProvider
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.DeckWithCards
import com.example.flashcards.model.repositories.FlashCardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class CardViewModel(private val flashCardRepository: FlashCardRepository) : ViewModel() {
    var cardUiState = MutableStateFlow(CardUiState())

    companion object {
        private const val TIMEOUT_MILLIS = 7_000L
    }

    fun updateCard(card: Card){
        viewModelScope.launch {
            flashCardRepository.updateCard(card)
        }
    }

    fun getDueCards(deckId : Int, cardTypeViewModel: CardTypeViewModel){
        cardTypeViewModel.getDueTypesForDeck(deckId)
        getCards(deckId)
    }
    fun getCards(deckId: Int){
        viewModelScope.launch {
            withTimeout(TIMEOUT_MILLIS) {
                flashCardRepository.getDueCards(deckId).map { cards ->
                    CardUiState(cardList = cards)
                }.collect { uiState ->
                    cardUiState.value = uiState
                }
            }
        }
    }
    fun getDeckWithCards(deckId: Int, cardTypeViewModel: CardTypeViewModel) {
        cardTypeViewModel.getAllTypesForDeck(deckId)
        getAllCards(deckId)

    }
    fun getAllCards(deckId: Int) {
        viewModelScope.launch {
            withTimeout(TIMEOUT_MILLIS) {
                flashCardRepository.getDeckWithCards(deckId).map { decks ->
                    CardUiState(cardList = decks.cards)
                }.collect { uiState ->
                    cardUiState.value = uiState
                }
            }
        }
    }

    fun updateCardType(cardId: Int, type:String, question:String,
                       answer: String,
                       basicCardViewModel: BasicCardViewModel) {
        viewModelScope.launch {
            flashCardRepository.updateCardType(cardId, type)
        }
        basicCardViewModel.updateBasicCard(cardId,question,answer)
    }
}

data class CardUiState(var cardList: List<Card> = emptyList())

