package com.belmontCrest.cardCrafter.navigation

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCDetails
import com.belmontCrest.cardCrafter.controller.view.models.ReusedFunc
import com.belmontCrest.cardCrafter.local.db.repositories.CardTypeRepository
import com.belmontCrest.cardCrafter.local.db.repositories.DeckContentRepository
import com.belmontCrest.cardCrafter.local.db.repositories.DeckListRepository
import com.belmontCrest.cardCrafter.local.db.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.local.db.tables.Card
import com.belmontCrest.cardCrafter.local.db.tables.CustomCard
import com.belmontCrest.cardCrafter.local.db.tables.deleteFiles
import com.belmontCrest.cardCrafter.model.daoHelpers.Order
import com.belmontCrest.cardCrafter.model.daoHelpers.OrderBy
import com.belmontCrest.cardCrafter.model.Type
import com.belmontCrest.cardCrafter.model.daoHelpers.reverseSort
import com.belmontCrest.cardCrafter.model.daoHelpers.toOrderedByClass
import com.belmontCrest.cardCrafter.model.ui.states.CDetails
import com.belmontCrest.cardCrafter.model.ui.states.SealedAllCTs
import com.belmontCrest.cardCrafter.model.ui.states.StringVar
import com.belmontCrest.cardCrafter.model.ui.states.SelectedCard
import com.belmontCrest.cardCrafter.model.ui.states.WhichDeck
import com.belmontCrest.cardCrafter.model.ui.states.hasNotations
import com.belmontCrest.cardCrafter.navigation.destinations.AddCardDestination
import com.belmontCrest.cardCrafter.supabase.controller.networkConnectivityFlow
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.GoogleCredentials
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.SBTablesRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo.AuthRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.ownerRepos.ExportRepository
import com.belmontCrest.cardCrafter.ui.functions.showToastMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
    private val kbRepository: KeyboardSelectionRepo,
    private val fieldParamRepo: FieldParamRepo,
    private val deckContentRepository: DeckContentRepository,
    private val deckListRepository: DeckListRepository,
    private val navHostRepo: NavHostRepo,
    private val authRepository: AuthRepository,
    private val sbTablesRepository: SBTablesRepository,
    private val exportRepository: ExportRepository,
    private val savedStateHandle: SavedStateHandle,
    application: Application
) : AndroidViewModel(application) {
    companion object {
        private const val NAV_VM = "NavViewModel"
        private const val TIMEOUT_MILLIS = 4_000L
        private const val CT_TYPE = "CT_type"
        private const val IS_SELECTING = "is_selecting"
        private const val SHOW_KB = "show_kb"
        private const val ROUTE = "route"
        private const val START_DECK_ROUTE = "start_deck_route"
        private const val START_SB_ROUTE = "start_sb_route"
    }

    private var isClientActive = true

    private val rf = ReusedFunc(flashCardRepository)
    private val cardId = MutableStateFlow(savedStateHandle["cardId"] ?: 0)

    val deckName = deckContentRepository.deckName.stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = StringVar()
    )

    val deckNav = navHostRepo.deckNav
    fun updateDeckNav(navHostController: NavHostController) =
        navHostRepo.updateDeckNav(navHostController)

    val sbNav = navHostRepo.sbNav
    fun updateSBNav(navHostController: NavHostController) =
        navHostRepo.updateSBNav(navHostController)

    val route = navHostRepo.route

    fun updateRoute(newRoute: String) {
        savedStateHandle[ROUTE] = newRoute
        navHostRepo.updateRoute(newRoute)
    }

    val startingDeckRoute = navHostRepo.startingDeckRoute

    fun updateStartingDeckRoute(newRoute: String) {
        savedStateHandle[START_DECK_ROUTE] = newRoute
        navHostRepo.updateStartingDeckRoute(newRoute)
    }

    val startingSBRoute = navHostRepo.startingSBRoute
    fun updateStartingSBRoute(newRoute: String) {
        savedStateHandle[START_SB_ROUTE] = newRoute
        navHostRepo.updateStartingSBRoute(newRoute)
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
        val cr = route.value.name
        if (ti != null && (cr == AddCardDestination.route))
            fieldParamRepo.updateCustomFields(ti)
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

    fun resetCard() = cardId.update { savedStateHandle["cardId"] = 0; 0 }

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

    fun resetFields() = fieldParamRepo.resetFields()

    private fun initialCT(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        val cd = cardTypeRepository.getACardType(id)?.toCDetails() ?: CDetails()
        fieldParamRepo.createFields(cd)
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

    /* <-- SUPABASE --> */
    val sealedAllCTs = exportRepository.sealedAllCTs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = SealedAllCTs()
    )


    fun addCardsToDisplay(cardIdentifier: String) =
        exportRepository.addCardsToDisplay(cardIdentifier)

    /** Google Oauth ID */
    suspend fun getGoogleId(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        authRepository.getGoogleCredentials().let { credentials ->
            when (credentials) {
                is GoogleCredentials.Success -> {
                    authRepository.updateGoogleId(credentials.credentials)
                    Pair(true, "")
                }

                is GoogleCredentials.Failure -> {
                    Pair(false, credentials.errorMessage)
                }
            }
        }
    }

    fun getOwner() = viewModelScope.launch { authRepository.getOwner() }

    fun updateStatus() = authRepository.getCurrentUser()

    fun disconnectSupabaseRT() = sbTablesRepository.disconnectRealtime()

    fun connectSupabase() = viewModelScope.launch { sbTablesRepository.connectRealtime() }
    fun getCurrentUserInfo() {
        updateStatus(); getOwner()
    }

    val owner = authRepository.owner


    private fun getDeckList() = viewModelScope.launch {
        sbTablesRepository.getDeckList().collectLatest { list ->
            sbTablesRepository.updateSBDeckList(list)
        }
    }

    init {
        updateQuery(savedStateHandle["query"] ?: "")
        navHostRepo.onInit(
            savedStateHandle.get<String>(ROUTE), savedStateHandle.get<String>(START_DECK_ROUTE),
            savedStateHandle.get<String>(START_SB_ROUTE)
        )
        viewModelScope.launch {
            application.networkConnectivityFlow().collectLatest { isConnected ->
                if (!isConnected && isClientActive) {
                    Log.d("NETWORK", "NETWORK HAS BEEN DISCONNECTED")
                    authRepository.closeSupabase()
                    delay(2000)
                    sbTablesRepository.updateSBDeckList(emptyList())
                    getOwner()
                    isClientActive = false
                } else if (isConnected && !isClientActive) {
                    // Reinitialize the client only if it's not active
                    authRepository.reCreateSupabase()
                    Log.d("NETWORK", "RECONNECTED!")
                    getDeckList()
                    getOwner()
                    isClientActive = true
                }
            }
        }
        getCurrentUserInfo()
    }
}