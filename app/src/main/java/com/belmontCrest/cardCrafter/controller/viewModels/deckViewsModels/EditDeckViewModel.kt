package com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EditDeckViewModel(
    private val flashCardRepository: FlashCardRepository,
) : ViewModel() {
    private val _errorMessage = MutableStateFlow("")

    companion object{
        private const val MIN_CARDS = 5
        private const val MAX_CARDS = 1000
        private const val MIN_REVIEWS = 1
        private const val MAX_REVIEWS = 40
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
        if (reviewAmount in MIN_REVIEWS..MAX_REVIEWS) {
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

    suspend fun updateDeckCardAmount(cardAmount : Int, deckId: Int): Int {
        if (cardAmount in MIN_CARDS .. MAX_CARDS) {
            return withContext(Dispatchers.IO) {
                try {
                    val row =
                        flashCardRepository.updateCardAmount(cardAmount, deckId)
                    if (row == 0) {
                        handleError("Failed to update cardAmount")
                    }
                    row
                } catch (e: SQLiteConstraintException) {
                    handleError(e.message.toString())
                } catch (e: Exception) {
                    handleError("Error updating deck cardAmount: ${e.message}")
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

