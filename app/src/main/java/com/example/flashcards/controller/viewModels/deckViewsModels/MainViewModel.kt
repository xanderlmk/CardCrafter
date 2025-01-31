package com.example.flashcards.controller.viewModels.deckViewsModels

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.uiModels.DeckUiState
import com.example.flashcards.model.repositories.FlashCardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.uiModels.CardListUiCount
import com.example.flashcards.model.uiModels.CardUpdateError
import com.example.flashcards.model.uiModels.SavedCardUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Date
import kotlin.collections.map


/**
 * ViewModel to retrieve all items in the Room database.
 */
class MainViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val uiState: StateFlow<DeckUiState> =
        flashCardRepository.getAllDecksStream().map { DeckUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DeckUiState()
            )

    private val thisCardCountUiState: StateFlow<CardListUiCount> =
        flashCardRepository.getCardCount().map { CardListUiCount(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CardListUiCount()
            )

    val deckUiState: StateFlow<DeckUiState> = uiState
    val cardCountUiState: StateFlow<CardListUiCount> = thisCardCountUiState

    private val savedCardUiState =
        MutableStateFlow(SavedCardUiState())
    val appStarted = mutableStateOf(savedStateHandle.get<Boolean>("appStarted"))

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private fun updateActivity() {
        appStarted.value = true
        savedStateHandle["appStarted"] = true
    }

    suspend fun getDeckById(
        deckId: Int,
    ): Deck {
        return withContext(Dispatchers.IO) {
            flashCardRepository.getDeckStream(deckId)
        }
    }

    suspend fun performDatabaseUpdate() {
        return withContext(Dispatchers.IO) {
            try {
                var completed = false
                viewModelScope.launch(Dispatchers.IO) {
                    flashCardRepository.getAllSavedCards().map {
                        SavedCardUiState(it)
                    }.collect {
                        savedCardUiState.value = it
                        completed = true
                    }
                }
                while (!completed) {
                    delay(20)
                }
                // Process cards in batches of 50
                savedCardUiState.value.savedCards.chunked(50).map { cardBatch ->
                    viewModelScope.launch(Dispatchers.IO) {
                        cardBatch.forEach { card ->
                            Log.d("Updating cards", "Processing batch")
                            flashCardRepository.updateSavedCards(
                                cardId = card.id,
                                reviewsLeft = card.reviewsLeft,
                                nextReview = card.nextReview.time,
                                passes = card.passes,
                                prevSuccess = card.prevSuccess,
                                totalPasses = card.totalPasses,
                                partOfList = card.nextReview <= Date()
                            )
                        }
                    }
                }.joinAll().also {
                    resetCardList()
                    viewModelScope.launch(Dispatchers.IO) {
                        flashCardRepository.deleteSavedCards()
                    }
                }
            } catch (e: Exception) {
                val error = when (e) {
                    is IOException -> CardUpdateError.NetworkError(e)
                    is SQLiteException -> CardUpdateError.DatabaseError(e)
                    else -> CardUpdateError.UnknownError(e)
                }
                println(error)
            }
        }.also {
            updateActivity()
        }
    }

    suspend fun resetCardList() {
        return withContext(Dispatchers.IO) {
            viewModelScope.launch(Dispatchers.IO) {
                flashCardRepository.resetCardLefts()
            }
        }
    }
}

