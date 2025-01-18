package com.example.flashcards.controller.viewModels.deckViewsModels

import android.database.sqlite.SQLiteConstraintException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.repositories.FlashCardRepository
import com.example.flashcards.model.tablesAndApplication.Deck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EditDeckViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _errorMessage = MutableStateFlow("")


    var deckName by mutableStateOf(savedStateHandle["deckName"] ?: "")
        private set
    var deckGM by mutableDoubleStateOf(savedStateHandle["deckGM"] ?: 0.0)
        private set
    var deckBM by mutableDoubleStateOf(savedStateHandle["deckBM"] ?: 0.0)
        private set
    var deckRA by mutableStateOf(savedStateHandle["deckRA"] ?: "")
        private set
    var deckIA by mutableStateOf(savedStateHandle.get<Boolean>("deckIsActive"))
        private set

    fun updateGMField(good: Double) {
        deckGM = good
        savedStateHandle["deckGM"] = good
    }

    fun updateActivity() {
        deckIA = true
        savedStateHandle["deckIsActive"] = true
    }

    fun updateBMField(bad: Double) {
        deckBM = bad
        savedStateHandle["deckBM"] = bad
    }

    fun updateNameField(name: String) {
        deckName = name
        savedStateHandle["deckName"] = name
    }

    fun updateRAField(reviewAmount: String) {
        deckRA = reviewAmount
        savedStateHandle["deckRA"] = reviewAmount
    }


    suspend fun checkIfDeckExists(deckName: String): Int {
        return withContext(Dispatchers.IO) {
            try {
                flashCardRepository.checkIfDeckExists(deckName)
            } catch (e: SQLiteConstraintException) {
                handleError("Error checking deck existence: ${e.message}")
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

    suspend fun updateDeckName(deckName: String, deckId: Int): Int {
        return withContext(Dispatchers.IO) {
            try {
                val rowsUpdated = flashCardRepository.updateDeckName(deckName, deckId)
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

    suspend fun updateReviewAmount(reviewAmount: Int, deckId: Int): Int {
        if (reviewAmount in 1..10) {
            return withContext(Dispatchers.IO) {
                try {
                    val row =
                        flashCardRepository.updateDeckReviewAmount(
                            reviewAmount, deckId
                        ) + flashCardRepository.updateCardReviewAmount(
                            reviewAmount, deckId
                        )
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
        println(prefix)
        _errorMessage.value = prefix
        return 0
    }

}

