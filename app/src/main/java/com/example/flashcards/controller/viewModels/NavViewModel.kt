package com.example.flashcards.controller.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.repositories.FlashCardRepository
import com.example.flashcards.model.tablesAndApplication.Deck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 * This will provide the navigation of a single deck, where it will be saved
 * by the savedStateHandle
 */
class NavViewModel(
    private val flashCardRepository: FlashCardRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val thisDeck: MutableStateFlow<Deck?> =
        MutableStateFlow(savedStateHandle["deck"])
    val deck = thisDeck.asStateFlow()

    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
    }
    fun getDeckById(
        deckId: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            withTimeout(TIMEOUT_MILLIS) {
                flashCardRepository.getDeckStream(deckId).collect { thisOne ->
                    thisDeck.update {
                        thisOne
                    }
                }
            }
        }
    }

}