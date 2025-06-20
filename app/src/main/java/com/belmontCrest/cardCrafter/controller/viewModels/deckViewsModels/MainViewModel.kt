package com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.model.ui.states.DeckUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.belmontCrest.cardCrafter.model.ui.states.CardListUiCount
import com.belmontCrest.cardCrafter.model.ui.CardUpdateError
import com.belmontCrest.cardCrafter.model.ui.states.SavedCardUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Date
import kotlin.collections.map


/**
 * ViewModel to retrieve all decks and the cards due with the respective deck
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    /** Updating this value whenever a user adds a cards and goes back to the
     * DeckOptionsDestination route */
    private val currentTime = time.asStateFlow()

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val uiState: StateFlow<DeckUiState> =
        flashCardRepository.getAllDecksStream().map { DeckUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DeckUiState()
            )

    private val thisCardCountUiState: StateFlow<CardListUiCount> = currentTime
        .flatMapLatest {
            flashCardRepository.getCardCount(it)
        }.map {
            CardListUiCount(it)
        }
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

    private fun updateActivity() {
        appStarted.value = true
        savedStateHandle["appStarted"] = true
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

