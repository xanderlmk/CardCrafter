package com.example.flashcards.controller.viewModels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.controller.navigation.AllViewModels
import com.example.flashcards.model.uiModels.CardUiState
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.repositories.FlashCardRepository
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class CardViewModel(
    private val flashCardRepository: FlashCardRepository
) : ViewModel() {
    private val uiState = MutableStateFlow(CardUiState())
    var cardUiState: StateFlow<CardUiState> = uiState.asStateFlow()

    private val errorMessage = MutableStateFlow<String?>(null)
    private val _errorState = MutableStateFlow<CardUpdateError?>(null)
    val errorState: StateFlow<CardUpdateError?> = _errorState.asStateFlow()

    fun setErrorMessage(message: String) {
        uiState.value = uiState.value.copy(errorMessage = message)
    }

    fun clearErrorMessage() {
        uiState.value = uiState.value.copy(errorMessage = "")
    }

    fun clearErrorState() {
        _errorState.value = null
    }

    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
    }


    fun getDeckWithCards(
        deckId: Int,
        cardTypeViewModel: EditingCardListViewModel,
        cardTypes: AllViewModels
    ) {
        viewModelScope.launch {
            cardTypes.basicCardViewModel.getAllBasicsForDeck(deckId)
            cardTypes.hintCardViewModel.getAllHintsForDeck(deckId)
            cardTypes.threeCardViewModel.getAllThreeForDeck(deckId)
            cardTypes.multiChoiceCardViewModel.getAllChoicesForDeck(deckId)
        }
        cardTypeViewModel.getAllTypesForDeck(deckId)
        getAllCards(deckId)
    }

    fun getAllCards(deckId: Int) {
        try {
            viewModelScope.launch {
                withTimeout(TIMEOUT_MILLIS) {
                    flashCardRepository.getDeckWithCards(deckId).map { decks ->
                        CardUiState(cardList = decks.cards)
                    }.collect { state ->
                        uiState.value = state
                    }
                }
            }
        } catch (e: TimeoutCancellationException) {
            errorMessage.value = "Request timed out. Please try again."
            println(e)
        }
    }

    fun updateCardType(
        cardId: Int, type: String, question: String,
        answer: String,
        basicCardViewModel: BasicCardViewModel
    ) {
        viewModelScope.launch {
            flashCardRepository.updateCardType(cardId, type)
        }
        basicCardViewModel.updateBasicCard(cardId, question, answer)
    }

    fun deleteCard(card: Card) {
        viewModelScope.launch {
            flashCardRepository.deleteCard(card)
        }
    }
}

sealed class CardUpdateError(val exception: Exception) : Exception(exception) {
    class NetworkError(exception: Exception) : CardUpdateError(exception)
    class DatabaseError(exception: Exception) : CardUpdateError(exception)
    class UnknownError(exception: Exception) : CardUpdateError(exception)
}
