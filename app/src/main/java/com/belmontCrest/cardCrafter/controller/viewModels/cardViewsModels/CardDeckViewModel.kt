package com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.controller.cardHandlers.callError
import com.belmontCrest.cardCrafter.controller.cardHandlers.returnError
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCard
import com.belmontCrest.cardCrafter.controller.cardHandlers.toSavedCard
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.CardTypeRepository
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.model.ui.CardState
import com.belmontCrest.cardCrafter.model.ui.CardUpdateError
import com.belmontCrest.cardCrafter.model.ui.SavedCardUiState
import com.belmontCrest.cardCrafter.model.ui.SealedDueCTs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.Calendar
import java.util.Date
import kotlin.collections.chunked

@OptIn(ExperimentalCoroutinesApi::class)
class CardDeckViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val cardTypeRepository: CardTypeRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
        private const val DECK_ID = "CardDeckVMDeckId"
        private const val INDEX = "CardDeckVMIndex"
    }

    private val thisErrorState = MutableStateFlow<CardUpdateError?>(null)
    val errorState: StateFlow<CardUpdateError?> = thisErrorState.asStateFlow()
    private val cardState: MutableStateFlow<CardState> =
        MutableStateFlow(savedStateHandle["cardState"] ?: CardState.Idle)

    private var backupCardListState = MutableStateFlow<List<Card>>(emptyList())
    val backupCardList = backupCardListState.asStateFlow()

    /** The list of due cards will be determined by the deckId, where if the
     * deck.cardAmount change it'll be reflected on the cardsLeft,
     * which should reflect on the state too.
     */
    private val _deckId = MutableStateFlow(savedStateHandle[DECK_ID] ?: 0)

    private val state = _deckId.flatMapLatest { id ->
            flashCardRepository.getDueDeckDetails(id)
        }.flatMapLatest { dueDetails ->
            if (dueDetails == null) {
                flowOf(SealedDueCTs())
            } else {
                cardTypeRepository.getAllDueCardsStream(dueDetails.id, dueDetails.cardsLeft, Date().time)
                    .map {
                        transitionTo(CardState.Finished)
                        SealedDueCTs(
                            allCTs = it.toMutableList(),
                            savedCTs = it.toMutableList()
                        )
                    }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = SealedDueCTs()
        )

    /** Our saved cards to be updated.
     * This will clear once the cards are updated.
     */
    private val savedCardUiState = MutableStateFlow(SavedCardUiState())

    /** Public state for the user to see */
    var cardListUiState = state

    val stateSize = state.map {
        it.allCTs.size
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = 0
    )
    private val _stateIndex = MutableStateFlow(savedStateHandle[INDEX] ?: 0)
    val stateIndex = _stateIndex.asStateFlow()

    /** This will change almost constantly. */
    fun updateWhichDeck(id: Int) {
        _deckId.update { id }
        savedStateHandle[DECK_ID] = id
    }

    fun updateIndex(index: Int) {
        _stateIndex.update { index }
        savedStateHandle[INDEX] = index
    }

    /** For when a user wants to redo a card */
    suspend fun getRedoCardType(cardId: Int, index: Int): Card {
        return withContext(Dispatchers.IO) {
            try {
                /** Forcefully creating a mutable list to override the current card. */
                val ct = cardTypeRepository.getACardType(cardId)
                val card = ct.toCard()
                viewModelScope.launch {
                    addCardToUpdate(card)
                    state.value.allCTs[index] = ct
                    state.value.savedCTs[index] = ct
                }
                clearErrorState()
                card
            } catch (e: IllegalStateException) {
                thisErrorState.value = CardUpdateError.IllegalStateError(e)
                thisErrorState.value?.let { cardUE ->
                    callError(cardUE)
                }
                state.value.allCTs[index].toCard()
            }
        }
    }

    /** Adding/Replacing the savedCard in the DB*/
    fun addCardToUpdate(card: Card) {
        viewModelScope.launch(Dispatchers.IO) {
            flashCardRepository.insertSavedCard(card.toSavedCard())
        }
    }

    /** Getting a backup list so if the user has traversed the
     * whole deck and the list is empty, they can still manage to go back */
    private suspend fun getBackupCards(deck: Deck) {
        return withContext(Dispatchers.IO) {
            if (deck.nextReview <= Date()) {
                try {
                    withTimeout(TIMEOUT_MILLIS) {
                        viewModelScope.launch {
                            backupCardListState.update {
                                flashCardRepository.getBackupDueCards(
                                    deck.id, deck.cardAmount
                                )
                            }
                        }
                    }
                    clearErrorState()
                } catch (e: TimeoutCancellationException) {
                    thisErrorState.value = CardUpdateError.TimeoutError(e)
                    thisErrorState.value?.let { cardUE ->
                        callError(cardUE)
                    }
                }
            }
        }
    }

    /** CardState functions */
    fun transitionTo(newState: CardState) {
        cardState.value = newState
        savedStateHandle["cardState"] = newState
    }

    fun getState(): CardState = cardState.value

    private fun clearErrorState() {
        thisErrorState.value = null
    }

    /** Updating the nextReview for the deck which will only be
     * to the next day (++1 day). */
    private fun updateNextReview(deck: Deck) {
        return with(Dispatchers.IO) {
            viewModelScope.launch(Dispatchers.IO) {
                val time = Calendar.getInstance()
                time.add(Calendar.DAY_OF_YEAR, 1)
                deck.nextReview = time.time
                flashCardRepository.updateNextReview(
                    deck.nextReview, deck.id
                )
            }
        }
    }

    suspend fun getBackupDueCards(id: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                getBackupCards(flashCardRepository.getDeck(id))
                clearErrorState()
                false
            } catch (e: Exception) {
                thisErrorState.value = returnError(e)
                thisErrorState.value?.let { cardUE ->
                    callError(cardUE)
                }
                true
            } finally {
                transitionTo(CardState.Finished)
            }
        }
    }

    suspend fun updateCards(deck: Deck): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val initVal = deck.cardsLeft
                // Process cards in batches of 50
                var completed = false
                viewModelScope.launch(Dispatchers.IO) {
                    flashCardRepository.getAllSavedCards().map {
                        SavedCardUiState(it)
                    }.collect {
                        savedCardUiState.value = it
                        completed = true
                    }
                }
                while (!completed) {
                    delay(20)
                }
                savedCardUiState.value.savedCards.chunked(50).map { cardBatch ->
                    viewModelScope.launch(Dispatchers.IO) {
                        cardBatch.forEach { card ->
                            /**
                             * If the card has been completed(nextReview > Date()),
                             * update it to not be part of the list and
                             * --cardsLeft.
                             * */
                            if (card.nextReview > Date()) {
                                deck.cardsLeft -= 1
                                deck.cardsDone += 1
                                flashCardRepository.updateSavedCards(
                                    cardId = card.id,
                                    reviewsLeft = card.reviewsLeft,
                                    nextReview = card.nextReview.time,
                                    passes = card.passes,
                                    prevSuccess = card.prevSuccess,
                                    totalPasses = card.totalPasses,
                                    partOfList = false
                                )
                            } else {
                                flashCardRepository.updateSavedCards(
                                    cardId = card.id,
                                    reviewsLeft = card.reviewsLeft,
                                    nextReview = card.nextReview.time,
                                    passes = card.passes,
                                    prevSuccess = card.prevSuccess,
                                    totalPasses = card.totalPasses,
                                    partOfList = card.partOfList
                                )
                            }
                        }
                    }
                }.joinAll().also {
                    viewModelScope.launch(Dispatchers.IO) {
                        /** Delete all the saved cards. */
                        flashCardRepository.deleteSavedCards()
                    }
                }
                if (initVal == deck.cardsLeft) {
                    println("initVal == deck.cardsLeft")
                    transitionTo(CardState.Finished)
                    clearErrorState()
                    return@withContext true
                } else {
                    viewModelScope.launch(Dispatchers.IO) {
                        flashCardRepository.updateCardsLeft(deck.id, deck.cardsLeft, deck.cardsDone
                        )
                    }
                }
                /** Making sure the deck does not have any due cards left
                 *  and it's still due for review */
                if (deck.cardsLeft <= 0 && deck.nextReview <= Date()) {
                    updateNextReview(deck)
                }
                transitionTo(CardState.Finished)
                clearErrorState()
                true
            } catch (e: Exception) {
                thisErrorState.value = returnError(e)
                thisErrorState.value?.let { cardUE -> callError(cardUE) }
                false
            }
        }
    }

    /** CardDeckView UI Variables */
    private val _redoClicked: MutableStateFlow<Boolean?> =
        MutableStateFlow(savedStateHandle["clickedChoice"])
    val redoClicked = _redoClicked.map {
        it == true
    }.stateIn(
        scope = viewModelScope,
        initialValue = false,
        started = SharingStarted.Eagerly
    )

    fun updateRedoClicked(clicked: Boolean) {
        _redoClicked.update { clicked }
        savedStateHandle["clickedChoice"] = clicked
    }

}
