package com.example.flashcards.controller.viewModels.deckViewsModels

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.compose.runtime.getValue
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
import java.util.Date


class AddDeckViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String>("")

    var deckName by mutableStateOf(savedStateHandle["deckName"] ?: "")
        private set
    var deckReviewAmount by mutableStateOf(savedStateHandle["deckReviewAmount"] ?: "1")
        private set
    var deckCardAmount by mutableStateOf(savedStateHandle["deckCardAmount"] ?: "20")

    companion object {
        private const val MIN_CARDS = 5
        private const val MAX_CARDS = 1000
        private const val MIN_REVIEWS = 1
        private const val MAX_REVIEWS = 40
    }

    fun updateDeckName(name: String) {
        deckName = name
        savedStateHandle["deckName"] = name
    }

    fun updateDeckReviewAmount(reviewAmount: String) {
        deckReviewAmount = reviewAmount
        savedStateHandle["deckReviewAmount"] = reviewAmount
    }

    fun updateDeckCardAmount(cardAmount : String){
        deckCardAmount = cardAmount
        savedStateHandle["deckCardAmount"] = cardAmount
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

    fun addDeck(name: String, reviewAmount: Int, cardAmount: Int) {
        if (name.isNotEmpty() &&
            reviewAmount in MIN_REVIEWS .. MAX_REVIEWS &&
            cardAmount in MIN_CARDS .. MAX_CARDS) {
            viewModelScope.launch {
                try {
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
                } catch (e: SQLiteConstraintException) {
                    handleError("A deck with this name already exists: ${e.message}")
                } catch (e: Exception) {
                    handleError("error adding deck: ${e.message}")
                }
            }
        }
    }

    private fun handleError(prefix: String): Int {
        Log.d("AddDeckViewModel", prefix)
        _errorMessage.value = prefix
        return 0
    }
}