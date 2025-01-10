package com.example.flashcards.controller.viewModels.deckViewsModels

import android.database.sqlite.SQLiteConstraintException
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


class AddDeckViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String>("")

    var deckName by mutableStateOf(savedStateHandle["deckName"] ?: "")

    var deckReviewAmount by mutableStateOf(savedStateHandle["deckReviewAmount"] ?: "1")

    fun updateDeckName(name: String) {
        deckName = name
        savedStateHandle["deckName"] = name
    }

    fun updateDeckReviewAmount(reviewAmount: String) {
        deckReviewAmount = reviewAmount
        savedStateHandle["deckReviewAmount"] = reviewAmount
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

    private fun handleError(prefix: String): Int {
        val message = prefix
        println(message)
        _errorMessage.value = message
        return 0
    }
}