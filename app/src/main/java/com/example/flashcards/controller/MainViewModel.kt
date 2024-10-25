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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.flashcards.model.Card
import com.example.flashcards.model.Deck
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import kotlin.random.Random

/**
 * ViewModel to retrieve all items in the Room database.
 */
class MainViewModel(private val flashCardRepository: FlashCardRepository) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage



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
                } catch (e: Exception){
                    _errorMessage.value = "error adding deck: ${e.message}"
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

    //this code will do a query that will check for a name of a database
    //if its in it will return 1 if not in there it will return 0




        //should return TRUE if a deck exists

    fun updateCard(card: Card){
        viewModelScope.launch {
            flashCardRepository.updateCard(card)
        }
    }

    fun getDueCards(deckId : Int){
        viewModelScope.launch {
            flashCardRepository.getDueCards(deckId)
                .collect { cards ->
                    cardUiState.value = CardUiState(cardList = cards.toMutableList())
                }
        }
    }
    fun addCard(deckId : Int, question:String, answer:String) {
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

data class CardUiState(var cardList: MutableList<Card> = mutableListOf())

fun updateCard(card: Card, isSuccess: Boolean) : Card {
    if (isSuccess) {
        card.passes += 1
    }
    card.nextReview = timeCalculator(card.passes, isSuccess)
    return card
}

private fun timeCalculator (passes : Int, isSuccess: Boolean) : Date {
    val calendar = Calendar.getInstance()
    // Determine the multiplier based on success or hard pass
    val multiplier = when {
        passes > 0 -> if (isSuccess) 1.5 else 0.5
        else -> if (isSuccess) 1.5 else 0.0
    }

    // Calculate days to add
    val daysToAdd = (passes * multiplier).toInt()

    // Add days to the current date
    calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)

    // Return the updated date
    return calendar.time
}

fun moveToNextCard(
    cardList: List<Card>,
    onNextCard: (Card?) -> Unit
) : Boolean{
    if (cardList.isEmpty()) {
        onNextCard(null) // No cards available, handle end state
        return false
    }

    // Generate a random index within the bounds of the card list
    val randomIndex = Random.nextInt(cardList.size)
    onNextCard(cardList[randomIndex])
    return true
}

// Helper to update the card state
fun handleCardUpdate(card: Card, success: Boolean, viewModel: MainViewModel) {
    val updatedCard = updateCard(card, success)
    viewModel.updateCard(updatedCard)


}

