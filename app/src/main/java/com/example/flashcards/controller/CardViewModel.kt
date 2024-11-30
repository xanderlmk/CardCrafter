package com.example.flashcards.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.Card
import com.example.flashcards.model.DeckWithCards
import com.example.flashcards.model.FlashCardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.util.Calendar
import java.util.Date

class CardViewModel(private val flashCardRepository: FlashCardRepository) : ViewModel() {
    var cardUiState = MutableStateFlow(CardUiState())

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun updateCard(card: Card){
        viewModelScope.launch {
            flashCardRepository.updateCard(card)
        }
    }

    fun getDueCards(deckId : Int){
        viewModelScope.launch {
            withTimeout(TIMEOUT_MILLIS) {
                flashCardRepository.getDueCards(deckId).map { cards ->
                    CardUiState(cardList = cards.toMutableList())
                }
                    .collect { uiState ->
                        cardUiState.value = uiState
                    }
            }
        }
    }
    fun getDeckWithCards(deckId: Int): Flow<DeckWithCards> {
        return flashCardRepository.getDeckWithCards(deckId)
    }

    fun updateCardDetails(cardId: Int, answer: String, question: String) {
        viewModelScope.launch {
            flashCardRepository.updateCardDetails(cardId, answer, question)
        }
    }
}

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

/*fun moveToNextCard(
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
}*/

// Helper to update the card state
fun handleCardUpdate(card: Card, success: Boolean, viewModel: CardViewModel) {
    val updatedCard = updateCard(card, success)
    viewModel.updateCard(updatedCard)
}