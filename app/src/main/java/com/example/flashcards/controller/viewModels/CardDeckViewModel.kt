package com.example.flashcards.controller.viewModels

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.repositories.FlashCardRepository
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.uiModels.CardState
import com.example.flashcards.model.uiModels.CardUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class CardDeckViewModel(
    private val flashCardRepository: FlashCardRepository
) : ViewModel() {
    private val uiState = MutableStateFlow(CardUiState())

    private val errorMessage = MutableStateFlow<String?>(null)
    private val _errorState = MutableStateFlow<CardUpdateError?>(null)
    val errorState: StateFlow<CardUpdateError?> = _errorState.asStateFlow()

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

    fun clearErrorState() {
        _errorState.value = null
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
                }
                jobs.joinAll().also {
                    getDueCards(deck.id, cardTypeViewModel)
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

    fun overrideUpdateCards(
        cardList: List<Card>,
    ) {
        try {
            viewModelScope.launch {
                cardList.map { card ->
                    flashCardRepository.updateCard(card)
                }
            }
            clearErrorState()
        } catch (e: Exception) {
            val error = when (e) {
                is IOException -> CardUpdateError.NetworkError(e)
                is SQLiteException -> CardUpdateError.DatabaseError(e)
                else -> CardUpdateError.UnknownError(e)
            }
            _errorState.value = error
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
}