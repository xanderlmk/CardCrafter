package com.example.flashcards.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.Card
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import com.example.flashcards.model.FlashCardRepository
import java.util.Date

class CardViewModel (
        savedStateHandle: SavedStateHandle,
        private val flashCardRepository: FlashCardRepository
    ) : ViewModel() {
    var cardUiState by mutableStateOf(CardUiState())
        private set

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
        }
}/*
data class CardUiState(
    val cardDetails: CardDetails = CardDetails()
)

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