package com.example.flashcards.controller.viewModels.deckViewsModels

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.controller.viewModels.cardViewsModels.EditingCardListViewModel
import com.example.flashcards.model.uiModels.DeckUiState
import com.example.flashcards.model.repositories.FlashCardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.uiModels.CardUpdateError
import com.example.flashcards.model.uiModels.SavedCardUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.collections.map


/**
 * ViewModel to retrieve all items in the Room database.
 */
class DeckViewModel(
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

    val deckUiState: StateFlow<DeckUiState> = uiState
    private val savedCardUiState =
        MutableStateFlow(SavedCardUiState())
    val appStarted = mutableStateOf(savedStateHandle.get<Boolean>("appStarted"))

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun updateActivity() {
        appStarted.value = true
        savedStateHandle["appStarted"] = true
    }

    fun getDeckById(
        deckId: Int,
        editingCardListVM: EditingCardListViewModel
    ): Flow<Deck?> {
        viewModelScope.launch {
            editingCardListVM.getAllBasicsForDeck(deckId)
            editingCardListVM.getAllHintsForDeck(deckId)
            editingCardListVM.getAllThreeForDeck(deckId)
            editingCardListVM.getAllChoicesForDeck(deckId)
        }
        return flashCardRepository.getDeckStream(deckId)
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
                    delay(50)
                }
                savedCardUiState.value.savedCards.map { card ->
                    viewModelScope.launch(Dispatchers.IO) {
                        Log.d("Updating cards", "Almost there")
                        flashCardRepository.updateSavedCards(
                            cardId = card.id,
                            reviewsLeft = card.reviewsLeft,
                            nextReview = card.nextReview.time,
                            passes = card.passes,
                            prevSuccess = card.prevSuccess,
                            totalPasses = card.totalPasses
                        )
                    }
                }.joinAll().also {
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
        }

    }
}

