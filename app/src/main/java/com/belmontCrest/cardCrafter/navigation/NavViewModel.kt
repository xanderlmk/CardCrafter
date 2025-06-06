package com.belmontCrest.cardCrafter.navigation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.CardTypeRepository
import com.belmontCrest.cardCrafter.navigation.destinations.DeckViewDestination
import com.belmontCrest.cardCrafter.navigation.destinations.MainNavDestination
import com.belmontCrest.cardCrafter.navigation.destinations.SupabaseDestination
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.model.uiModels.StringVar
import com.belmontCrest.cardCrafter.model.uiModels.SelectedCard
import com.belmontCrest.cardCrafter.model.uiModels.SelectedKeyboard
import com.belmontCrest.cardCrafter.model.uiModels.WhichDeck
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 * This will provide the navigation of a single deck, where it will be saved
 * by the savedStateHandle
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NavViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val cardTypeRepository: CardTypeRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
    }

    private val deckId = MutableStateFlow(savedStateHandle["id"] ?: 0)
    private val cardId = MutableStateFlow(savedStateHandle["cardId"] ?: 0)

    val deckName = deckId.flatMapLatest { id ->
        if (id == 0) {
            flowOf(StringVar())
        } else {
            flashCardRepository.getDeckName(id).map {
                StringVar(it ?: "")
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = StringVar()
    )

    private val _deckNav: MutableStateFlow<NavHostController?> = MutableStateFlow(null)
    val deckNav = _deckNav.asStateFlow()
    fun updateDeckNav(navHostController: NavHostController) {
        _deckNav.update { navHostController }
    }

    private val _sbNav: MutableStateFlow<NavHostController?> = MutableStateFlow(null)
    val sbNav = _sbNav.asStateFlow()
    fun updateSBNav(navHostController: NavHostController) {
        _sbNav.update { navHostController }
    }

    private val _route = MutableStateFlow(
        StringVar(savedStateHandle["route"] ?: MainNavDestination.route)
    )
    val route = _route.asStateFlow()

    fun updateRoute(newRoute: String) {
        savedStateHandle["route"] = newRoute
        _route.update { StringVar(newRoute) }
    }

    private val _startingDeckRoute = MutableStateFlow(
        StringVar(savedStateHandle["startDeckRoute"] ?: DeckViewDestination.route)
    )
    val startingDeckRoute = _startingDeckRoute.asStateFlow()

    fun updateStartingDeckRoute(newRoute: String) {
        savedStateHandle["startDeckRoute"] = newRoute
        _startingDeckRoute.update { StringVar(newRoute) }
    }

    private val _startingSBRoute = MutableStateFlow(
        StringVar(savedStateHandle["startSBRoute"] ?: SupabaseDestination.route)
    )
    val startingSBRoute = _startingSBRoute.asStateFlow()

    fun updateStartingSBRoute(newRoute: String) {
        savedStateHandle["startSBRoute"] = newRoute
        _startingSBRoute.update {
            StringVar(newRoute)
        }
    }

    val wd: StateFlow<WhichDeck> = deckId.flatMapLatest { id ->
        if (id == 0) {
            flowOf(WhichDeck())
        } else {
            flashCardRepository.getDeckStream(id).map {
                WhichDeck(it)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = WhichDeck()
    )

    private val _type = MutableStateFlow(savedStateHandle["type"] ?: "basic")
    val type = _type.asStateFlow()
    fun updateType(newType: String) {
        savedStateHandle["type"] = newType
        _type.update { newType }
    }

    val card = cardId.flatMapLatest { id ->
        if (id == 0) {
            flowOf(SelectedCard(null))
        } else {
            cardTypeRepository.getACardTypeStream(id).map {
                Log.d("CARD STATUS", "$it")
                SelectedCard(it)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue =
            if (cardId.value == 0) {
                SelectedCard(null)
            } else {
                SelectedCard(cardTypeRepository.getACardType(cardId.value))
            }
    )
    private val _showKatexKeyboard = MutableStateFlow(false)
    val showKatexKeyboard = _showKatexKeyboard.asStateFlow()
    private val _selectedKB: MutableStateFlow<SelectedKeyboard?> = MutableStateFlow(null)
    val selectedKB = _selectedKB.asStateFlow()
    private val _resetOffset = MutableStateFlow(false)
    val resetOffset = _resetOffset.asStateFlow()

    fun getDeckById(id: Int) {
        savedStateHandle["id"] = id
        deckId.update { id }

    }

    fun deleteCard(card: Card) {
        viewModelScope.launch {
            flashCardRepository.deleteCard(card)
        }
    }

    fun getCardById(id: Int) {
        savedStateHandle["cardId"] = id
        cardId.update { id }
    }

    fun resetCard() {
        savedStateHandle["cardId"] = 0
        cardId.update { 0 }
    }

    /** Value to check is the user is syncing the deck */
    private val _isBlocking = MutableStateFlow(false)
    val isBlocking = _isBlocking.asStateFlow()
    fun updateIsBlocking() {
        _isBlocking.update { true }
    }

    fun resetIsBlocking() {
        _isBlocking.update { false }
    }

    fun updateSelectedKB(selectedKeyboard: SelectedKeyboard) {
        _selectedKB.update { selectedKeyboard }
    }

    fun resetSelectedKB() {
        _selectedKB.update { null }
    }

    fun toggleKeyboard() {
        _showKatexKeyboard.update { !it }
    }

    fun resetOffset() {
        _resetOffset.update { true }
    }

    fun resetDone() {
        _resetOffset.update { false }
    }

    /** Reset the selected keyboard to null and don't show the keyboard */
    fun resetKeyboardStuff() {
        resetSelectedKB()
        _showKatexKeyboard.update { false }
    }
}