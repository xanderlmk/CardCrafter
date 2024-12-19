package com.example.flashcards.controller.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.CardUiState
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.repositories.FlashCardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class CardViewModel(private val flashCardRepository: FlashCardRepository) : ViewModel() {
    var cardUiState = MutableStateFlow(CardUiState())
    private val errorMessage = MutableStateFlow<String?>(null)

    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
    }

    suspend fun updateCard(card: Card) : Boolean{
        return withContext(Dispatchers.IO) {
            flashCardRepository.updateCard(card)
            true
        }
    }

    suspend fun getDueCards(deckId : Int, cardTypeViewModel: CardTypeViewModel) : Boolean{
        return withContext(Dispatchers.IO) {
            try{
                cardTypeViewModel.getDueTypesForDeck(deckId)
                getCards(deckId)
                delay(120)
                false
            }
            catch (e : Exception){
                true
            }
        }
    }
    fun getCards(deckId: Int){
        try {
            viewModelScope.launch {
                withTimeout(TIMEOUT_MILLIS) {
                    flashCardRepository.getDueCards(deckId).map { cards ->
                        CardUiState(cardList = cards)
                    }.collect { uiState ->
                        cardUiState.value = uiState
                    }
                }
            }
        } catch (e : TimeoutCancellationException){
            errorMessage.value = "Request timed out. Please try again."
            println(e)
        }
    }

    fun getDeckWithCards(deckId: Int, cardTypeViewModel: CardTypeViewModel) {
        cardTypeViewModel.getAllTypesForDeck(deckId)
        getAllCards(deckId)

    }
    fun getAllCards(deckId: Int) {
        try {
            viewModelScope.launch {
                withTimeout(TIMEOUT_MILLIS) {
                    flashCardRepository.getDeckWithCards(deckId).map { decks ->
                        CardUiState(cardList = decks.cards)
                    }.collect { uiState ->
                        cardUiState.value = uiState
                    }
                }
            }
        } catch(e : TimeoutCancellationException) {
            errorMessage.value = "Request timed out. Please try again."
            println(e)
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

    suspend fun getCardById(cardId : Int) : Card? {
        return flashCardRepository.getCardById(cardId)
    }
}
