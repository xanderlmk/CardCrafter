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

    fun updateCardsLeft(deck: Deck, cardsToAdd : Int){
        viewModelScope.launch(Dispatchers.IO){
            flashCardRepository.updateCardsLeft(deck.id, (deck.cardsLeft + cardsToAdd))
        }
    }
}