package com.example.flashcards.controller.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.repositories.FlashCardRepository
import com.example.flashcards.model.tablesAndApplication.Deck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.Date


/**
 * This will provide the navigation of a single deck, where it will be saved
 * by the savedStateHandle
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NavViewModel(
    private val flashCardRepository: FlashCardRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
    }

    private val deckId = MutableStateFlow(savedStateHandle["id"] ?: 0)
    private val thisDeck: StateFlow<Deck?> = deckId
        .flatMapLatest { id ->
            flashCardRepository.getDeckStream(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = null
        )
    val deck = thisDeck
    fun getDeckById(id: Int) {
        deckId.value = id
    }

    suspend fun updateCardsLeft(deck: Deck, cardsToAdd: Int) {
        return withContext(Dispatchers.IO) {
            /** Only add the cards if the deck's review is due */
            if (deck.nextReview <= Date()) {
                /** Make sure the cardsLeft + cardsAdded don't
                 * exceed the deck's cardAmount
                 */
                viewModelScope.launch(Dispatchers.IO) {
                    withTimeout(TIMEOUT_MILLIS) {
                        if ((deck.cardsLeft + cardsToAdd) < deck.cardAmount) {
                            flashCardRepository.updateCardsLeft(
                                deck.id,
                                (deck.cardsLeft + cardsToAdd)
                            )
                        } else {
                            flashCardRepository.updateCardsLeft(deck.id, deck.cardAmount)
                        }
                    }
                }
            }
        }
    }
}