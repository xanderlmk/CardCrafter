package com.example.flashcards.controller.viewModels.deckViewsModels

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.controller.navigation.AllViewModels
import com.example.flashcards.model.uiModels.DeckUiState
import com.example.flashcards.model.repositories.FlashCardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.flashcards.model.tablesAndApplication.Deck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

/**
 * ViewModel to retrieve all items in the Room database.
 */
class DeckViewModel(private val flashCardRepository: FlashCardRepository) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String>("")
    private val uiState: StateFlow<DeckUiState> =
        flashCardRepository.getAllDecksStream().map { DeckUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DeckUiState()
            )

    val deckUiState: StateFlow<DeckUiState> = uiState


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    suspend fun checkIfDeckExists(name: String): Int {
        return withContext(Dispatchers.IO) {
            try {
                flashCardRepository.checkIfDeckExists(name)
            } catch (e: SQLiteConstraintException) {
                handleError("Error checking deck existence: ${e.message}")
            }
        }
    }

    fun addDeck(name: String, reviewAmount: Int) {
        if (name.isNotEmpty() && reviewAmount > 0 && reviewAmount < 10) {
            viewModelScope.launch {
                try {
                    flashCardRepository.insertDeck(
                        Deck(
                            name = name,
                            reviewAmount = reviewAmount
                        )
                    )
                } catch (e: SQLiteConstraintException) {
                    handleError("A deck with this name already exists: ${e.message}")
                } catch (e: Exception) {
                    handleError("error adding deck: ${e.message}")
                }
            }
        }
    }

    // Deleting a deck via the repository
    fun deleteDeck(deck: Deck) {
        viewModelScope.launch {
            flashCardRepository.deleteAllCards(deck.id)
            flashCardRepository.deleteDeck(deck)
        }
    }

    suspend fun updateDeckName(newName: String, deckId: Int): Int {
        return withContext(Dispatchers.IO) {
            try {
                val rowsUpdated = flashCardRepository.updateDeckName(newName, deckId)
                if (rowsUpdated == 0) {
                    handleError("Failed to update deck name - name already exists")
                }
                rowsUpdated
            } catch (e: SQLiteConstraintException) {
                handleError("A deck with this name already exists: ${e.message}")

            } catch (e: Exception) {
                handleError("Error updating deck name: ${e.message}")
            }
        }
    }

    suspend fun updateDeckGoodMultiplier(newMultiplier: Double, deckId: Int): Int {
        if (newMultiplier > 1.0) {
            return withContext(Dispatchers.IO) {
                try {
                    val row =
                        flashCardRepository.updateDeckGoodMultiplier(newMultiplier, deckId)
                    if (row == 0) {
                        handleError("Failed to update multiplier")
                    }
                    row
                } catch (e: SQLiteConstraintException) {
                    handleError(e.message.toString())
                } catch (e: Exception) {
                    handleError("Error updating deck multiplier: ${e.message}")
                }
            }
        }
        return 0
    }

    suspend fun updateDeckBadMultiplier(newMultiplier: Double, deckId: Int): Int {
        if (newMultiplier < 1.0 && newMultiplier > 0.0) {
            return withContext(Dispatchers.IO) {
                try {
                    val row =
                        flashCardRepository.updateDeckBadMultiplier(newMultiplier, deckId)
                    if (row == 0) {
                        handleError("Failed to update multiplier")
                    }
                    row
                } catch (e: SQLiteConstraintException) {
                    handleError(e.message.toString())
                } catch (e: Exception) {
                    handleError("Error updating deck multiplier: ${e.message}")
                }
            }
        }
        return 0
    }

    suspend fun updateReviewAmount(newReviewAmount: Int, deckId: Int): Int {
        if (newReviewAmount > 0 && newReviewAmount < 11) {
            return withContext(Dispatchers.IO) {
                try {
                    val row =
                        flashCardRepository.updateDeckReviewAmount(newReviewAmount, deckId) +
                                flashCardRepository.updateCardReviewAmount(newReviewAmount, deckId)
                    if (row == 0) {
                        handleError("Failed to update Review Amount")
                    }
                    row
                } catch (e: SQLiteConstraintException) {
                    handleError(e.message.toString())
                } catch (e: Exception) {
                    handleError("Error updating deck Review Amount: ${e.message}")
                }
            }
        }
        return 0
    }

    private fun handleError(prefix: String): Int {
        val message = prefix
        println(message)
        _errorMessage.value = message
        return 0
    }

    fun getDeckById(deckId: Int, cardTypes: AllViewModels): Flow<Deck?> {
        viewModelScope.launch {
            cardTypes.basicCardViewModel.getAllBasicsForDeck(deckId)
            cardTypes.hintCardViewModel.getAllHintsForDeck(deckId)
            cardTypes.threeCardViewModel.getAllThreeForDeck(deckId)
            cardTypes.multiChoiceCardViewModel.getAllChoicesForDeck(deckId)
        }
        return flashCardRepository.getDeckStream(deckId)
    }
}

