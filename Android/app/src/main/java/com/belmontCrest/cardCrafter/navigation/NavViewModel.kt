package com.belmontCrest.cardCrafter.navigation

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCDetails
import com.belmontCrest.cardCrafter.controller.view.models.ReusedFunc
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.CardTypeRepository
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.DeckContentRepository
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.DeckListRepository
import com.belmontCrest.cardCrafter.navigation.destinations.DeckViewDestination
import com.belmontCrest.cardCrafter.navigation.destinations.MainNavDestination
import com.belmontCrest.cardCrafter.navigation.destinations.SupabaseDestination
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.CustomCard
import com.belmontCrest.cardCrafter.localDatabase.tables.deleteFiles
import com.belmontCrest.cardCrafter.model.daoHelpers.Order
import com.belmontCrest.cardCrafter.model.daoHelpers.OrderBy
import com.belmontCrest.cardCrafter.model.Type
import com.belmontCrest.cardCrafter.model.daoHelpers.reverseSort
import com.belmontCrest.cardCrafter.model.daoHelpers.toOrderedByClass
import com.belmontCrest.cardCrafter.model.ui.states.StringVar
import com.belmontCrest.cardCrafter.model.ui.states.SelectedCard
import com.belmontCrest.cardCrafter.model.ui.states.WhichDeck
import com.belmontCrest.cardCrafter.model.ui.states.hasNotations
import com.belmontCrest.cardCrafter.navigation.destinations.AddCardDestination
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * This will provide the navigation of a single deck, where it will be saved
 * by the savedStateHandle
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NavViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val cardTypeRepository: CardTypeRepository,
    private val kbRepository: KeyboardSelectionRepository,
    private val fieldParamRepository: FieldParamRepository,
    private val deckContentRepository: DeckContentRepository,
    private val deckListRepository: DeckListRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        private const val NAV_VM = "NavViewModel"
        private const val TIMEOUT_MILLIS = 4_000L
        private const val CT_TYPE = "CT_type"
        private const val IS_SELECTING = "is_selecting"
        private const val SHOW_KB = "show_kb"
    }

    private val rf = ReusedFunc(flashCardRepository)
    private val cardId = MutableStateFlow(savedStateHandle["cardId"] ?: 0)

    val deckName = deckContentRepository.deckName.stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
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

    val wd: StateFlow<WhichDeck> = deckContentRepository.wd.stateIn(
        scope = viewModelScope, started = SharingStarted.Lazily,
        initialValue = WhichDeck()
    )

    val type = kbRepository.type

    fun updateType(newType: String) {
        if (newType == type.value) return
        savedStateHandle[CT_TYPE] = newType
        val ti = kbRepository.customTypes.value.ts.find { it.t == newType }
        val cr = _route.value.name
        if (ti != null && (cr == AddCardDestination.route))
            fieldParamRepository.updateCustomFields(ti)
        kbRepository.updateType(newType)
    }

    val card = cardId.flatMapLatest { id ->
        if (id == 0) flowOf(SelectedCard(null))
        else cardTypeRepository.getACardTypeStream(id).map {
            Log.d("CARD STATUS", "$it"); SelectedCard(it)
        }
    }.stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue =
            if (cardId.value == 0) SelectedCard(null)
            else SelectedCard(cardTypeRepository.getACardType(cardId.value))
    )

    val localDecks = deckListRepository.deckUiState.map { it.deckList }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    /** Update the deckId to get the deck */
    fun getDeckById(id: Int) {
        deckContentRepository.updateDeckId(id)
        viewModelScope.launch { deckContentRepository.updateDeckNextReview(id) }
    }

    fun deleteCard(card: Card) = viewModelScope.launch { flashCardRepository.deleteCard(card) }

    fun deleteFiles(customCard: CustomCard, context: Context): Boolean {
        val (message, success) = customCard.deleteFiles()
        if (!success) showToastMessage(context, message)
        return success
    }

    suspend fun deleteCardList(): Boolean = withContext(Dispatchers.IO) {
        try {
            cardTypeRepository.deleteCTs()
            deselectAll()
            return@withContext true
        } catch (e: Exception) {
            Log.e(NAV_VM, "Error deleting card list: $e")
            return@withContext false
        }
    }

    suspend fun copyCardList(deckId: Int): Pair<Boolean, String> = withContext(Dispatchers.IO) {
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

    suspend fun moveCardList(deckId: Int): Pair<Boolean, String> = withContext(Dispatchers.IO) {
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

    fun getCardById(id: Int) {
        savedStateHandle["cardId"] = id
        initialCT(id)
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

    val showKatexKeyboard = kbRepository.showKatexKeyboard
    val selectedKB = kbRepository.selectedKB
    val selectable = cardTypeRepository.selectedCards.map { it.isNotEmpty() }.stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = false
    )

    fun toggleKeyboard() {
        savedStateHandle[SHOW_KB] = !showKatexKeyboard.value
        kbRepository.toggleKeyboardIcon()
    }

    fun onCreate() {
        val showKB = savedStateHandle.get<Boolean>(SHOW_KB) == true
        val newType = savedStateHandle.get<String>(CT_TYPE) ?: "basic"
        kbRepository.onCreate(showKB, newType)
    }

    fun resetOffset() = kbRepository.resetOffset()

    /** Reset the selected keyboard to null and don't show the keyboard */
    fun resetKeyboardStuff() {
        savedStateHandle[SHOW_KB] = false
        kbRepository.resetKeyboardStuff()
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

    private val _orderBy = deckListRepository.orderedBy
    val direction = deckListRepository.orderedBy.map {
        it is OrderBy.NameASC || it is OrderBy.CreatedOnASC || it is OrderBy.CardsLeftASC
    }.stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = _orderBy.value is OrderBy.NameASC || _orderBy.value is OrderBy.CreatedOnASC
    )

    fun updateOrder(order: Order) {
        val orderBy = order.toOrderedByClass(direction.value)
        deckListRepository.updateOrder(orderBy)
    }

    fun reverseOrder() = deckListRepository.updateOrder(_orderBy.value.reverseSort())

    val searchQuery = cardTypeRepository.searchQuery.stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = ""
    )

    fun updateQuery(query: String) {
        savedStateHandle["query"] = query
        cardTypeRepository.updateQuery(query)
    }

    val customTypes = kbRepository.customTypes

    val isNotationType = combine(
        kbRepository.notationParamSelected, type, customTypes
    ) { selected, type, types ->
        val stringTypes = types.ts.mapNotNull { if (it.hasNotations()) it.t else null }.toSet()
        val isIt =
            (selected && (type == Type.CREATE_NEW) || type in stringTypes || type == Type.NOTATION)
        Log.d(NAV_VM, "isNotationType: $isIt")
        isIt
    }.stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = false
    )

    fun resetFields() = fieldParamRepository.resetFields()

    private fun initialCT(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        val ct = cardTypeRepository.getACardType(id)
        fieldParamRepository.createFields(ct.toCDetails())
    }

    fun updateTime() = deckListRepository.updateTime()

    val savedCardUiState = deckContentRepository.savedCards.stateIn(
        scope = viewModelScope, started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )
    val dueCardSize = deckContentRepository.dueCardsState.map { it.allCTs.size }.stateIn(
        scope = viewModelScope, started = SharingStarted.Lazily,
        initialValue = 0
    )

    fun updateRedoClicked(clicked: Boolean) = deckContentRepository.updateRedoClicked(clicked)

    /**
     * Upon going into a new deck (or due cards view), reset all saved cards
     */
    fun clearSavedCards() = viewModelScope.launch { deckContentRepository.deleteSavedCards() }

    init {
        updateQuery(savedStateHandle["query"] ?: "")
    }
}