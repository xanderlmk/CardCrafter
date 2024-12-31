package com.example.flashcards.controller.viewModels

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

    private val _errorMessage = MutableStateFlow<String?>(null)
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
                _errorMessage.value = "Error checking deck existence: ${e.message}"
                0
            }
        }
    }

    fun addDeck(name: String) {
        if (name.isNotEmpty()) {
            viewModelScope.launch {
                try {
                    flashCardRepository.insertDeck(Deck(name = name))
                } catch (e: SQLiteConstraintException) {
                    _errorMessage.value = "A deck with this name already exists"
                    println(e)
                } catch (e: Exception){
                    _errorMessage.value = "error adding deck: ${e.message}"
                }
            }
        }
    }

    // Deleting a deck via the repository
    fun deleteDeck(deck: Deck) {
        viewModelScope.launch{
            flashCardRepository.deleteAllCards(deck.id)
            flashCardRepository.deleteDeck(deck)
        }
    }

    suspend fun updateDeckName(newName: String, deckId: Int): Int {
        return withContext(Dispatchers.IO) {
            try {
                val rowsUpdated = flashCardRepository.updateDeckName(newName, deckId)
                if (rowsUpdated == 0) {
                    _errorMessage.value = "Failed to update deck name - name may already exist"
                }
                rowsUpdated
            } catch (e: SQLiteConstraintException) {
                _errorMessage.value = "A deck with this name already exists"
                println(e)
                0
            } catch (e: Exception) {
                _errorMessage.value = "Error updating deck name: ${e.message}"
                0
            }
        }
    }
    suspend fun updateDeckGoodMultiplier(newMultiplier: Double, deckId: Int) : Int{
        if (newMultiplier > 1.0){
            return withContext(Dispatchers.IO) {
                    try {
                        val row =
                            flashCardRepository.updateDeckGoodMultiplier(newMultiplier, deckId)
                        if (row == 0) {
                            _errorMessage.value = "Failed to update multiplier"
                        }
                        row
                    } catch (e: SQLiteConstraintException) {
                        _errorMessage.value = e.message
                        println(e)
                        0
                    } catch (e: Exception) {
                        _errorMessage.value = "Error updating deck multiplier: ${e.message}"
                        0
                    }
                }
        }
        return 0
    }
    suspend fun updateDeckBadMultiplier(newMultiplier: Double, deckId: Int) : Int{
        if (newMultiplier < 1.0 && newMultiplier > 0.0){
            return withContext(Dispatchers.IO) {
                try {
                    val row =
                        flashCardRepository.updateDeckBadMultiplier(newMultiplier, deckId)
                    if (row == 0) {
                        _errorMessage.value = "Failed to update multiplier"
                    }
                    row
                } catch (e: SQLiteConstraintException) {
                    _errorMessage.value = e.message
                    println(e)
                    0
                } catch (e: Exception) {
                    _errorMessage.value = "Error updating deck multiplier: ${e.message}"
                    0
                }
            }
        }
        return 0
    }

    fun getDeckById(deckId : Int, cardTypes: AllViewModels) : Flow<Deck?> {
        viewModelScope.launch {
            cardTypes.basicCardViewModel.getAllBasicsForDeck(deckId)
            cardTypes.hintCardViewModel.getAllHintsForDeck(deckId)
            cardTypes.threeCardViewModel.getAllThreeForDeck(deckId)
            cardTypes.multiChoiceCardViewModel.getAllChoicesForDeck(deckId)
        }
        return flashCardRepository.getDeckStream(deckId)
    }
}

