package com.belmontCrest.cardCrafter.controller.view.models.deckViewsModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.DeckListRepository
import com.belmontCrest.cardCrafter.model.ui.states.DeckUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * ViewModel to retrieve all decks and the cards due with the respective deck
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(deckListRepository: DeckListRepository) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
    }
    val deckUiState = deckListRepository.deckUiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = DeckUiState()
    )
}

