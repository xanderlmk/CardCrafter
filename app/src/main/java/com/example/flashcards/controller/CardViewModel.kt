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
        private const val TIMEOUT_MILLIS = 7_000L
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
                    CardUiState(cardList = cards)
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

data class CardUiState(var cardList: List<Card> = emptyList())

fun updateCard(card: Card, isSuccess: Boolean) : Card {
    if (isSuccess) {
        card.passes += 1
        card.prevSuccess = true
    } else { card.prevSuccess = false}
    card.nextReview = timeCalculator(card.passes, isSuccess)
    card.totalPasses += 1

    if (!isSuccess && !card.prevSuccess && card.passes > 0){
        card.passes -=1
    }
    return card
}

private fun timeCalculator (passes : Int, isSuccess: Boolean) : Date {
    val calendar = Calendar.getInstance()
    // Determine the multiplier based on success or hard pass
    val multiplier = when {
        passes == 1 -> if (isSuccess) 1.5 else 1.0 // passes == 1
        passes >= 2 -> if (isSuccess) 1.5 else 0.5
        else -> if (isSuccess) 1.5 else 0.0
    }

    // Calculate days to add
    val daysToAdd = (passes * multiplier).toInt()

    // Add days to the current date
    calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)

    // Return the updated date
    return calendar.time
}

// Helper to update the card state
fun handleCardUpdate(card: Card, success: Boolean, viewModel: CardViewModel) {
    val updatedCard = updateCard(card, success)
    viewModel.updateCard(updatedCard)
}