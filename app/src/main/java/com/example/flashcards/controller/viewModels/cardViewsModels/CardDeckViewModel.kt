package com.example.flashcards.controller.viewModels.cardViewsModels

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.repositories.CardTypeRepository
import com.example.flashcards.model.repositories.FlashCardRepository
import com.example.flashcards.model.tablesAndApplication.AllCardTypes
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.tablesAndApplication.SavedCard
import com.example.flashcards.model.uiModels.CardListUiState
import com.example.flashcards.model.uiModels.CardState
import com.example.flashcards.model.uiModels.CardUpdateError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.IOException

class CardDeckViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val cardTypeRepository: CardTypeRepository,
) : ViewModel() {

    private val errorMessage: MutableStateFlow<String> = MutableStateFlow("")
    private val _errorState = MutableStateFlow<CardUpdateError?>(null)
    val errorState: StateFlow<CardUpdateError?> = _errorState.asStateFlow()

    private val cardState: MutableState<CardState> = mutableStateOf(CardState.Idle)

    private val uiState = MutableStateFlow(CardListUiState())
    private val savedUiState = MutableStateFlow(CardListUiState())
    var savedCardList = savedUiState.asStateFlow()
    var cardListUiState = uiState.asStateFlow()

    private val backupCardListState = MutableStateFlow<List<Card>>(emptyList())
    val backupCardList = backupCardListState.asStateFlow()


    /** for future
    @OptIn(SavedStateHandleSaveableApi::class)
    var cardsToUpdate: List<SavedCard> by
    savedStateHandle.saveable {
    mutableStateOf(emptyList())

    }
     */

    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
    }

    /*suspend fun restoreBackup(index: Int){
        uiState.map {
            it.copy(allCards = backupCardList)
        }.collect {
            uiState.value = it
        }
    }*/

    suspend fun getRedoCards(cardId: Int) : List<AllCardTypes>{
        return withContext(Dispatchers.IO) {
            var complete = false
            try {
                viewModelScope.launch {
                    cardTypeRepository.getACardType(cardId).let { card ->
                        uiState.update { currentState ->
                            currentState.copy(allCards = currentState.allCards + card)
                        }
                        savedUiState.update { currentState ->
                            currentState.copy(allCards = currentState.allCards + card)
                        }
                    }.also {
                        complete = true
                    }
                }
                while (!complete) {
                    delay(25)
                }
                uiState.value.allCards
            } catch (e: Exception) {
                handleError(e, "Something went wrong")
                uiState.value.allCards
            }
        }
    }

    suspend fun getRedoCard(cardId: Int) : Card {
        return withContext(Dispatchers.IO) {
            flashCardRepository.getCardById(cardId)
        }
    }

    fun replaceCard(index: Int) : Card {
        viewModelScope.launch {
            if (index in uiState.value.allCards.indices &&
                index in backupCardListState.value.indices &&
                index in savedUiState.value.allCards.indices) {
                val tempUiState = uiState.value.allCards.run {
                    toMutableList().apply {
                        this[index].card = backupCardListState.value[index]
                    }
                }
                uiState.update { currentState ->
                    currentState.copy(allCards = tempUiState)
                }
                savedUiState.update { currentState ->
                    currentState.copy(allCards = tempUiState)
                }
            } else {
                uiState.update { currentState ->
                    currentState.copy(errorMessage = "Index out of bounds")
                }
                savedUiState.update { currentState ->
                    currentState.copy(errorMessage = "Index out of bounds")
                }
            }
        }
        return backupCardListState.value[index]
    }

    suspend fun getBackupCards(deckId: Int) {
        return withContext(Dispatchers.IO) {
            viewModelScope.launch {
              backupCardListState.update {
                  flashCardRepository.getBackupDueCards(deckId)
              }
            }
        }
    }

    private suspend fun getDueTypesForDeck(deckId: Int) {
        return withContext(Dispatchers.IO)
        {
            var complete = false
            try {
                viewModelScope.launch {
                    //    viewModelScope.launch(Dispatchers.IO){
                    withTimeout(TIMEOUT_MILLIS) {
                        cardTypeRepository.getDueAllCardTypes(deckId).map { allCards ->
                            CardListUiState(allCards = allCards)
                        }.collect { state ->
                            uiState.value = state
                            savedUiState.value = state
                            complete = true
                        }
                    }
                }
                while (!complete) {
                    delay(20)
                }
                return@withContext
            } catch (e: TimeoutCancellationException) {
                errorMessage.value = "Request timed out. Please try again."
                println(e)
                return@withContext
            }
        }
    }


    fun transitionTo(newState: CardState) {
        cardState.value = newState
    }

    fun getState(): CardState = cardState.value

    fun setErrorMessage(message: String) {
        uiState.value = uiState.value.copy(errorMessage = message)
    }

    private fun clearErrorMessage() {
        uiState.value = uiState.value.copy(errorMessage = "")
    }

    private fun clearErrorState() {
        _errorState.value = null
    }

    suspend fun updateCards(
        deck: Deck, cardList: List<Card>
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val jobs = cardList.map { card ->
                    viewModelScope.launch {
                        flashCardRepository.updateCard(card)
                    }
                }
                jobs.joinAll().also {
                    getDueCards(deck.id)
                }
                clearErrorState()
                true
            } catch (e: Exception) {
                val error = when (e) {
                    is IOException -> CardUpdateError.NetworkError(e)
                    is SQLiteException -> CardUpdateError.DatabaseError(e)
                    else -> CardUpdateError.UnknownError(e)
                }
                _errorState.value = error
                false
            }
        }
    }


    suspend fun getDueCards(deckId: Int) {
        return withContext(Dispatchers.IO) {
            try {
                getDueTypesForDeck(deckId)
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

    fun addCardToUpdate(card: Card) {
        val savedCard = SavedCard(
            id = card.id,
            reviewsLeft = card.reviewsLeft,
            nextReview = card.nextReview,
            prevSuccess = card.prevSuccess,
            passes = card.passes,
            totalPasses = card.totalPasses
        )
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                flashCardRepository.insertSavedCard(savedCard)
            }
        }
    }
    /** for future
    fun addCardToUpdate(card: Card) {
    val savedCard = SavedCard(
    id = card.id,
    reviewsLeft = card.reviewsLeft,
    nextReview = card.nextReview?: Date(),
    prevSuccess = card.prevSuccess,
    passes = card.passes,
    totalPasses = card.totalPasses
    )
    withMutableSnapshot {
    cardsToUpdate+= savedCard
    }
    // Save the updated list to SavedStateHandle
    Log.d("CardsToUpDate", "$cardsToUpdate")
    }*/
}