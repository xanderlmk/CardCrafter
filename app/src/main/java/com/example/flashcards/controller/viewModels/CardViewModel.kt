package com.example.flashcards.controller.viewModels

import android.database.sqlite.SQLiteConstraintException
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.controller.navigation.AllViewModels
import com.example.flashcards.model.uiModels.CardState
import com.example.flashcards.model.uiModels.CardUiState
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.repositories.FlashCardRepository
import com.example.flashcards.model.tablesAndApplication.Deck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class CardViewModel(private val flashCardRepository: FlashCardRepository) : ViewModel() {
    private val uiState = MutableStateFlow(CardUiState())
    var cardUiState: StateFlow<CardUiState> = uiState.asStateFlow()

    private val errorMessage = MutableStateFlow<String?>(null)

    private val cardState: MutableState<CardState> = mutableStateOf(CardState.Idle)

    fun transitionTo(newState: CardState) {
        cardState.value = newState
    }

    fun getState(): CardState = cardState.value

    fun setErrorMessage(message: String) {
        uiState.value = uiState.value.copy(errorMessage = message)
    }

    fun clearErrorMessage() {
        uiState.value = uiState.value.copy(errorMessage = "")
    }

    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
    }

    suspend fun updateCards(
        deck: Deck, cardList: List<Card>,
        cardTypeViewModel: CardTypeViewModel
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val jobs = cardList.map { card ->
                    viewModelScope.launch {
                        flashCardRepository.updateCard(card)
                    }
                }.also {
                    getDueCards(deck.id, cardTypeViewModel)
                }
                jobs.forEach { it.join() }
                true
            } catch (e: Exception) {
                handleError(e, "Something went wrong")
                false
            }
        }
    }

    suspend fun getDueCards(deckId: Int, cardTypeViewModel: CardTypeViewModel) {
        return withContext(Dispatchers.IO) {
            try {
                cardTypeViewModel.getDueTypesForDeck(deckId)
                //getCards(deckId)
                clearErrorMessage()
            } catch (e: Exception) {
                handleError(e, "Something went wrong")
            } catch (e: SQLiteConstraintException) {
                handleError(e, "SQLite Exception")
            } finally {
                transitionTo(CardState.Finished)
            }
        }
    }

    private fun handleError(e: Exception, prefix: String): Boolean {
        val message = "$prefix: $e"
        errorMessage.value = message
        setErrorMessage(message)
        return true
    }

    /* IN CASE WE NEED IT AGAIN
    fun getCards(deckId: Int){
        try {
            viewModelScope.launch {
                withTimeout(TIMEOUT_MILLIS) {
                    flashCardRepository.getDueCards(deckId).map { cards ->
                        CardUiState(cardList = cards)
                    }.collect { state ->
                        uiState.value = state
                    }
                }
            }
        } catch (e : TimeoutCancellationException){
            errorMessage.value = "Request timed out. Please try again."
            println(e)
        }
    }
    */
    fun getDeckWithCards(
        deckId: Int,
        cardTypeViewModel: CardTypeViewModel,
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
