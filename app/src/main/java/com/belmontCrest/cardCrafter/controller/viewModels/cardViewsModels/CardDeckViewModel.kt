package com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels


import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.controller.cardHandlers.callError
import com.belmontCrest.cardCrafter.controller.cardHandlers.fcrUpdateCard
import com.belmontCrest.cardCrafter.controller.cardHandlers.mapACardTypeToCT
import com.belmontCrest.cardCrafter.controller.cardHandlers.returnError
import com.belmontCrest.cardCrafter.controller.cardHandlers.returnSavedCard
import com.belmontCrest.cardCrafter.model.repositories.CardTypeRepository
import com.belmontCrest.cardCrafter.model.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Card
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck
import com.belmontCrest.cardCrafter.model.uiModels.CardState
import com.belmontCrest.cardCrafter.model.uiModels.CardUpdateError
import com.belmontCrest.cardCrafter.model.uiModels.SealedDueCTs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
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

@OptIn(ExperimentalCoroutinesApi::class)
class CardDeckViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val cardTypeRepository: CardTypeRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
    }

    private val thisErrorState = MutableStateFlow<CardUpdateError?>(null)
    val errorState: StateFlow<CardUpdateError?> = thisErrorState.asStateFlow()
    private val cardState: MutableStateFlow<CardState> =
        MutableStateFlow(savedStateHandle["cardState"] ?: CardState.Idle)
    private val cardListToUpdateUiState = MutableStateFlow<List<Card>>(emptyList())
    var cardListToUpdate = cardListToUpdateUiState.asStateFlow()

    private var backupCardListState = MutableStateFlow<List<Card>>(emptyList())
    val backupCardList = backupCardListState.asStateFlow()

    /** The list of due cards will be determined by the deckId, where if the
     * deck.cardAmount change it'll be reflected on the cardsLeft,
     * which should reflect on the state too.
     */
    private val deckId = MutableStateFlow(savedStateHandle["deckId"] ?: 0)

    private val state = deckId.flatMapLatest { id ->
        flashCardRepository.getDueDeckDetails(id)
    }.flatMapLatest { dueDetails ->
        if (dueDetails == null) {
            flowOf(SealedDueCTs())
        } else {
            cardTypeRepository.getAllDueCards(dueDetails.id, dueDetails.cardsLeft, Date().time)
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

    /** Public state for the user to see */
    var cardListUiState = state

    val stateSize = state.map {
        it.allCTs.size
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = 0
    )
    private val thisIndex = MutableStateFlow(savedStateHandle["index"] ?: 0)
    val stateIndex = thisIndex.asStateFlow()

    /** This will change almost constantly. */
    fun updateWhichDeck(id: Int) {
        deckId.value = id
        savedStateHandle["deckId"] = id
    }

    fun updateIndex(index: Int) {
        thisIndex.update { index }
        savedStateHandle["index"] = index
    }

    /** As the user progresses through the deck's card,
     * more cards will be added to the cardListToUpdateUiState,
     * where it ill check if that card is already in the list and
     * replace accordingly */
    fun addCardToTheUpdateCardsList(card: Card) {
        cardListToUpdateUiState.update { cards ->
            val mutableCards = cards.toMutableList()
            if (!mutableCards.contains(card)) {
                mutableCards.add(card)
            }
            mutableCards
        }
    }

    /** For when a user wants to redo a card */
    suspend fun getRedoCardType(cardId: Int, index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                viewModelScope.launch(Dispatchers.IO) {
                    /** Forcefully creating a mutable list to override the current card. */
                    val ct = mapACardTypeToCT(cardTypeRepository.getACardType(cardId))
                    state.value.allCTs[index] = ct
                    state.value.savedCTs[index] = ct
                    clearErrorState()
                }
            } catch (e: IllegalStateException) {
                thisErrorState.value = CardUpdateError.IllegalStateError(e)
                thisErrorState.value?.let { cardUE ->
                    callError(cardUE)
                }
            }
        }
    }

    suspend fun getRedoCard(cardId: Int): Card {
        return withContext(Dispatchers.IO) {
            flashCardRepository.getCardById(cardId)
        }.also {
            addCardToUpdate(it)
        }
    }

    /** Adding/Replacing the savedCard in the DB*/
    fun addCardToUpdate(card: Card) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                flashCardRepository.insertSavedCard(returnSavedCard(card))
            }
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

    suspend fun updateCards(
        deck: Deck, cardList: List<Card>
    ): Boolean {
        return withContext(Dispatchers.Main) {
            try {
                val initVal = deck.cardsLeft
                if (cardList == state.value.allCTs) {
                    transitionTo(CardState.Finished)
                    updateWhichDeck(deck.id)
                    clearErrorState()
                    return@withContext true
                }
                try {
                    cardList.map { card ->
                        viewModelScope.launch(Dispatchers.Default) {
                            if (card.nextReview > Date()) {
                                deck.cardsLeft -= 1
                                fcrUpdateCard(flashCardRepository, card)
                            } else {
                                flashCardRepository.updateCard(card)
                            }
                        }
                    }.joinAll()
                } catch (e: Exception) {
                    Log.d("CardDeckVM", "$e")
                }
                viewModelScope.launch(Dispatchers.IO) {
                    if (initVal == deck.cardsLeft) {
                        transitionTo(CardState.Finished)
                        clearErrorState()
                        return@launch
                    }
                    flashCardRepository.updateCardsLeft(deck.id, deck.cardsLeft)
                    /** Making sure the deck does not have any due cards left
                     *  and it's still due for review */
                    if (deck.cardsLeft <= 0 && deck.nextReview <= Date()) {
                        updateNextReview(deck)
                    }
                }.join()
                transitionTo(CardState.Finished)
                clearErrorState()
                true
            } catch (e: Exception) {
                thisErrorState.value = returnError(e)
                thisErrorState.value?.let { cardUE ->
                    callError(cardUE)
                }
                false
            }
        }.also {
            cardListToUpdateUiState.update {
                emptyList()
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

    /** CardDeckView UI Variables */
    private val privateClicked: MutableStateFlow<Boolean?> =
        MutableStateFlow(savedStateHandle["clickedChoice"])
    val redoClicked = privateClicked.map {
        it == true
    }.stateIn(
        scope = viewModelScope,
        initialValue = false,
        started = SharingStarted.Eagerly
    )

    fun updateRedoClicked(clicked: Boolean) {
        privateClicked.update {
            clicked
        }
        savedStateHandle["clickedChoice"] = clicked
    }

}
