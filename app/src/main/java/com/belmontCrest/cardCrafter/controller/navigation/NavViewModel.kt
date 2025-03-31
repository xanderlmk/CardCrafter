package com.belmontCrest.cardCrafter.controller.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.belmontCrest.cardCrafter.model.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Card
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update


/**
 * This will provide the navigation of a single deck, where it will be saved
 * by the savedStateHandle
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NavViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
    }

    private val deckId = MutableStateFlow(savedStateHandle["id"] ?: 0)
    private val cardId = MutableStateFlow(savedStateHandle["cardId"] ?: 0)

    val name = deckId.flatMapLatest {
        flashCardRepository.getDeckName(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = ""
    )

    val deckNav: MutableStateFlow<NavHostController?> = MutableStateFlow(null)
    fun updateDeckNav(navHostController: NavHostController) {
        deckNav.update {
            navHostController
        }
    }
    val route = MutableStateFlow(savedStateHandle["route"]?: MainNavDestination.route)
    fun updateRoute(newRoute : String) {
        route.update {
            newRoute
        }
    }

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

    private val thisCard: StateFlow<Card?> = cardId
        .flatMapLatest {
            if (it == 0) {
                flowOf(null)
            } else {
                flashCardRepository.getCardStream(it)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = null
        )
    val card = thisCard
    fun getDeckById(id: Int) {
        deckId.value = id
        savedStateHandle["id"] = id
    }

    fun getCardById(id: Int) {
        cardId.value = id
        savedStateHandle["cardId"] = id
    }

    fun resetCard() {
        cardId.value = 0
    }
}