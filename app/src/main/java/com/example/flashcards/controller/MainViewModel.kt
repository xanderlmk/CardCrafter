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
import kotlinx.coroutines.flow.MutableStateFlow
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

    var cardUiState = MutableStateFlow(CardUiState())

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

    fun getCards(deckId : Int){
        viewModelScope.launch {
            flashCardRepository.getDeckWithCards(deckId)
        }
    }

    fun getDueCards(deckId : Int){
        viewModelScope.launch {
            flashCardRepository.getDueCards(deckId)
                .collect { cards ->
                    cardUiState.value = CardUiState(cardList = cards)
                }
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

data class CardUiState(var cardList: List<Card> = listOf())
/*
data class CardDetails(
    val id: Int = 0,
    val deckId : Int = 0,
    val question: String = "",
    val answer: String = "",
    val nextReview: Date? = null,
    val passes: Int = 0
)

fun CardDetails.toCard(): Card = Card(
    id = id,
    deckId = deckId,
    question = question,
    answer = answer,
    nextReview = nextReview,
    passes = passes
)

fun Card.toCardDetails(): CardDetails = CardDetails(
    id = id,
    deckId = deckId,
    question = question,
    answer = answer,
    nextReview = nextReview,
    passes = passes
)*/



