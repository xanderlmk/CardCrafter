package com.belmontCrest.cardCrafter.navigation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.belmontCrest.cardCrafter.controller.viewModels.ReusedFunc
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.CardTypeRepository
import com.belmontCrest.cardCrafter.navigation.destinations.DeckViewDestination
import com.belmontCrest.cardCrafter.navigation.destinations.MainNavDestination
import com.belmontCrest.cardCrafter.navigation.destinations.SupabaseDestination
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.model.ui.StringVar
import com.belmontCrest.cardCrafter.model.ui.SelectedCard
import com.belmontCrest.cardCrafter.model.ui.SelectedKeyboard
import com.belmontCrest.cardCrafter.model.ui.WhichDeck
import kotlinx.coroutines.Dispatchers
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
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json


/**
 * This will provide the navigation of a single deck, where it will be saved
 * by the savedStateHandle
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NavViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val cardTypeRepository: CardTypeRepository,
    private val kbRepository: KeyboardSelectionRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        private const val NAV_VM = "NavViewModel"
        private const val TIMEOUT_MILLIS = 4_000L
        private const val SHOW_KB = "showKB"
        private const val CT_TYPE = "CT_type"
        private const val KEY_SELECTED_KB = "selected_kb"
        private const val IS_SELECTING = "is_selecting"
    }

    private val rf = ReusedFunc(flashCardRepository)
    private val deckId = MutableStateFlow(savedStateHandle["id"] ?: 0)
    private val cardId = MutableStateFlow(savedStateHandle["cardId"] ?: 0)

    val deckName = deckId.flatMapLatest { id ->
        if (id == 0) flowOf(StringVar())
        else flashCardRepository.getDeckName(id).map { StringVar(it ?: "") }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = StringVar()
    )

    private val _deckNav: MutableStateFlow<NavHostController?> = MutableStateFlow(null)
    val deckNav = _deckNav.asStateFlow()
    fun updateDeckNav(navHostController: NavHostController) = _deckNav.update { navHostController }

    private val _sbNav: MutableStateFlow<NavHostController?> = MutableStateFlow(null)
    val sbNav = _sbNav.asStateFlow()
    fun updateSBNav(navHostController: NavHostController) = _sbNav.update { navHostController }

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
        _startingSBRoute.update { StringVar(newRoute) }
    }

    val wd: StateFlow<WhichDeck> = deckId.flatMapLatest { id ->
        if (id == 0) flowOf(WhichDeck())
        else flashCardRepository.getDeckStream(id).map { WhichDeck(it) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = WhichDeck()
    )

    private val _type = MutableStateFlow(savedStateHandle[CT_TYPE] ?: "basic")
    val type = _type.asStateFlow()
    fun updateType(newType: String) {
        savedStateHandle[CT_TYPE] = newType
        _type.update { newType }
    }

    val card = cardId.flatMapLatest { id ->
        if (id == 0) flowOf(SelectedCard(null))
        else cardTypeRepository.getACardTypeStream(id).map {
            Log.d("CARD STATUS", "$it"); SelectedCard(it)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue =
            if (cardId.value == 0) SelectedCard(null)
            else SelectedCard(cardTypeRepository.getACardType(cardId.value))
    )

    fun getDeckById(id: Int) {
        savedStateHandle["id"] = id
        deckId.update { id }
    }

    fun deleteCard(card: Card) = viewModelScope.launch { flashCardRepository.deleteCard(card) }

    suspend fun deleteCardList(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                cardTypeRepository.deleteCTs()
                deselectAll()
                return@withContext true
            } catch (e: Exception) {
                Log.e(NAV_VM, "Error deleting card list: $e")
                return@withContext false
            }
        }
    }

    suspend fun copyCardList(deckId: Int): Pair<Boolean, String> {
        return withContext(Dispatchers.IO) {
            val deck = flashCardRepository.getDeck(deckId)
            try {
                val size = cardTypeRepository.selectedCards.value.size
                cardTypeRepository.copyCardList(deck)
                rf.updateCardsLeft(deck, size)
                deselectAll()
                return@withContext Pair(true, deck.name)
            } catch (e: Exception) {
                Log.e(NAV_VM, "Error copying card list: $e")
                return@withContext Pair(false, deck.name)
            }
        }
    }

    suspend fun moveCardList(deckId: Int): Pair<Boolean, String> {
        return withContext(Dispatchers.IO) {
            val deck = flashCardRepository.getDeck(deckId)
            try {
                val size = cardTypeRepository.selectedCards.value.size
                cardTypeRepository.moveCardList(deck)
                rf.updateCardsLeft(deck, size)
                deselectAll()
                return@withContext Pair(true, deck.name)
            } catch (e: Exception) {
                Log.e(NAV_VM, "Error moving card list: $e")
                return@withContext Pair(false, deck.name)
            }
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

    fun updateIsBlocking() = _isBlocking.update { true }

    fun resetIsBlocking() = _isBlocking.update { false }

    val showKatexKeyboard = kbRepository.showKatexKeyboard.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = savedStateHandle[SHOW_KB] as Boolean? == true
    )
    val selectedKB = kbRepository.selectedKB
    val resetOffset = kbRepository.resetOffset
    val selectable = cardTypeRepository.selectedCards.map { it.isNotEmpty() }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = false
    )

    fun updateSelectedKB(selectedKeyboard: SelectedKeyboard) {
        kbRepository.updateSelectedKB(selectedKeyboard)
        savedStateHandle[KEY_SELECTED_KB] =
            Json.encodeToString(SelectedKeyboard.serializer(), selectedKeyboard)
    }

    fun resetSelectedKB() = kbRepository.resetSelectedKB()

    fun retrieveKB() {
        val kb = savedStateHandle.get<String>(KEY_SELECTED_KB)
            ?.let { Json.decodeFromString(SelectedKeyboard.serializer(), it) }
        kbRepository.retrieveKB(kb)
    }

    fun toggleKeyboard() {
        savedStateHandle[SHOW_KB] = !showKatexKeyboard.value
        kbRepository.toggleKeyboard()
    }

    fun resetOffset() = kbRepository.resetOffset()

    fun resetDone() = kbRepository.resetDone()

    /** Reset the selected keyboard to null and don't show the keyboard */
    fun resetKeyboardStuff() {
        kbRepository.resetKeyboardStuff()
        savedStateHandle[SHOW_KB] = false
    }

    private val _isSelecting =
        MutableStateFlow(savedStateHandle[IS_SELECTING] as Boolean? == true)
    val isSelecting = _isSelecting.asStateFlow()

    fun isSelectingTrue() = _isSelecting.update { true }

    fun selectAll() = wd.value.deck?.let { deck ->
        viewModelScope.launch { cardTypeRepository.toggleAllCards(deck.id) }
    }

    fun deselectAll() = cardTypeRepository.deselectAll()

    fun resetSearchQuery() = cardTypeRepository.resetQuery()

    fun resetSelection() {
        cardTypeRepository.deselectAll()
        _isSelecting.update { false }
    }

    suspend fun getAllDecks(): List<Deck> = withContext(Dispatchers.IO) {
        flashCardRepository.getAllDecks()
    }

    init {
        val show = savedStateHandle[SHOW_KB] as Boolean? == true
        kbRepository.retrieveShowKB(show)
    }
}