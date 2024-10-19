package com.example.flashcards.controller
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.FlashCardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.util.Log
import com.example.flashcards.model.Card
import com.example.flashcards.model.Deck
import java.util.Date

/**
 * ViewModel to retrieve all items in the Room database.
 */
class MainViewModel(private val flashCardRepository: FlashCardRepository) : ViewModel() {


    val mainUiState: StateFlow<MainUiState> =
        flashCardRepository.getAllDecksStream().map { MainUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = MainUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun addDeck(name: String) {
        if (name.isNotEmpty()) {
            viewModelScope.launch {
                try {
                    flashCardRepository.insertDeck(Deck(name = name))
                } catch (e: SQLiteConstraintException) {
                    Log.e("DatabaseError", "Deck with this name already exists.")
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
    fun getCards(card: Card,deckId : Int){
        viewModelScope.launch {
            flashCardRepository.getDeckWithCards(deckId)
        }
    }
    fun addCard(deckId : Int, question:String, answer:String,) {
        if(question.isNotEmpty() && answer.isNotEmpty()) {
            viewModelScope.launch {
                flashCardRepository.insertCard(
                    Card(
                        deckId = deckId,
                        question = question,
                        answer = answer,
                        nextReview = Date(),
                        passes = 0
                    )
                )
            }
        }
    }
}


data class MainUiState(val deckList: List<Deck> = listOf())



