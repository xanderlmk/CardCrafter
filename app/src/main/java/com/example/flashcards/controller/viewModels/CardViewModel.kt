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
    var cardUiState : StateFlow<CardUiState> = uiState.asStateFlow()

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
                //getCards(deckId)
                transitionTo(CardState.Finished)
                clearErrorMessage()
                false
            }
            catch (e : Exception){
                errorMessage.value = "Something went wrong: $e"
                setErrorMessage(errorMessage.value?: "$e")
                true
            }
            catch (e: SQLiteConstraintException){
                errorMessage.value = "SQLite Exception $e"
                setErrorMessage(errorMessage.value?: "$e")
                true
            }
        }
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
        cardTypes: AllViewModels) {
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
}
