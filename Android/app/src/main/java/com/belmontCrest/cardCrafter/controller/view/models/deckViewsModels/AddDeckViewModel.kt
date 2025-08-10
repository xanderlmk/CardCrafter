package com.belmontCrest.cardCrafter.controller.view.models.deckViewsModels

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date


class AddDeckViewModel(
    private val flashCardRepository: FlashCardRepository,
) : ViewModel() {

    private val _errorMessage = MutableStateFlow("")


    companion object {
        private const val MIN_CARDS = 5
        private const val MAX_CARDS = 1000
        private const val MIN_REVIEWS = 1
        private const val MAX_REVIEWS = 40
    }


    suspend fun checkIfDeckExists(name: String): Int {
        return withContext(Dispatchers.IO) {
            checkIfDeckExists(name, flashCardRepository)
        }
    }

    fun addDeck(name: String, reviewAmount: Int, cardAmount: Int) {
        if (name.isNotEmpty() &&
            reviewAmount in MIN_REVIEWS..MAX_REVIEWS &&
            cardAmount in MIN_CARDS..MAX_CARDS
        ) {
            try {
                viewModelScope.launch(Dispatchers.IO) {
                    flashCardRepository.insertDeck(
                        Deck(
                            name = name,
                            reviewAmount = reviewAmount,
                            cardAmount = cardAmount,
                            cardsLeft = 0, // No cards in deck yet .
                            nextReview = Date(),
                            lastUpdated = Date()
                        )
                    )
                }
            } catch (e: SQLiteConstraintException) {
                handleError("A deck with this name already exists: ${e.message}")
            } catch (e: Exception) {
                handleError("error adding deck: ${e.message}")
            }
        }
    }

    private fun handleError(prefix: String): Int {
        Log.e("AddDeckViewModel", prefix)
        _errorMessage.value = prefix
        return 0
    }
}