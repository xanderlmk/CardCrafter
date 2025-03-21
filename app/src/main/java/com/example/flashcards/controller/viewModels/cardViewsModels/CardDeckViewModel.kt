package com.example.flashcards.controller.viewModels.cardViewsModels

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.controller.cardHandlers.callError
import com.example.flashcards.controller.cardHandlers.mapACardTypeToCT
import com.example.flashcards.controller.cardHandlers.mapAllCardTypesToCTs
import com.example.flashcards.model.repositories.CardTypeRepository
import com.example.flashcards.model.repositories.FlashCardRepository
import com.example.flashcards.model.tablesAndApplication.AllCardTypes
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.tablesAndApplication.SavedCard
import com.example.flashcards.model.uiModels.CardState
import com.example.flashcards.model.uiModels.CardUpdateError
import com.example.flashcards.model.uiModels.SealedDueCTs
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
import java.io.IOException
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

    private val cardState : MutableStateFlow<CardState> =
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
    private val state: StateFlow<SealedDueCTs> = deckId.flatMapLatest { id ->
        flashCardRepository.getDueDeckDetails(id)
    }.flatMapLatest { details ->
        if (details.cardsLeft == 0 || details.nextReview > Date()) {
            flowOf(SealedDueCTs())
        } else {
            cardTypeRepository.getAllDueCards(details.id, details.cardsLeft)
                .map {
                    transitionTo(CardState.Finished)
                    updateSealedUiState(it)
                }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = SealedDueCTs()
    )
    /** Public state for the user to see */
    var cardListUiState = state

    /** mapping the list of AllCardTypes to a sealed state */
    private fun updateSealedUiState(
        allCards: List<AllCardTypes>
    ): SealedDueCTs {
        var allCTs = try {
            mapAllCardTypesToCTs(allCards)
        } catch (e: IllegalStateException) {
            thisErrorState.value = CardUpdateError.IllegalStateError(e)
            thisErrorState.value?.let {
                    cardUE -> callError(cardUE)
            }
            return SealedDueCTs()
        }
        return SealedDueCTs(
            allCTs = allCTs.toMutableList(),
            savedCTs = allCTs.toMutableList(),
        ).also {
            clearErrorState()
        }
    }
    /** This will change almost constantly. */
    fun updateWhichDeck(id: Int) {
        deckId.update {
            id
        }
        savedStateHandle["deckId"] = id
    }
    /** As the user progresses through the deck's card,
     * more cards will be added to the cardListToUpdateUiState,
     * where it ill check if that card is already in the list and
     * replace accordingly
     */
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
                val ct = mapACardTypeToCT(cardTypeRepository.getACardType(cardId))
                viewModelScope.launch(Dispatchers.IO) {
                    state.value.allCTs[index] = ct
                    state.value.savedCTs[index] = ct
                    clearErrorState()
                }
            } catch (e: IllegalStateException) {
                thisErrorState.value = CardUpdateError.IllegalStateError(e)
                thisErrorState.value?.let {
                        cardUE -> callError(cardUE)
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
        val savedCard = SavedCard(
            id = card.id,
            reviewsLeft = card.reviewsLeft,
            nextReview = card.nextReview,
            prevSuccess = card.prevSuccess,
            passes = card.passes,
            totalPasses = card.totalPasses,
            partOfList = card.partOfList
        )
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                flashCardRepository.insertSavedCard(savedCard)
            }
        }
    }

    /** Getting a backup list so if the user has traversed the
     * whole deck and the list is empty, they can still manage to go back
     */
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
                    thisErrorState.value?.let {
                            cardUE -> callError(cardUE)
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
     * to the next day (++1 day).
     */
    private fun updateNextReview(deck: Deck) {
        return with(Dispatchers.IO) {
            viewModelScope.launch(Dispatchers.IO) {
                val time = Calendar.getInstance()
                time.add(Calendar.DAY_OF_YEAR, 1)
                deck.nextReview = time.time
                Log.d("${deck.name}.nextReview", "${deck.nextReview}")
                flashCardRepository.updateNextReview(
                    deck.nextReview, deck.id
                )
            }
        }
    }
    suspend fun updateCards(
        deck: Deck, cardList: List<Card>
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val jobs = cardList.map { card ->
                    if (card.nextReview > Date()) {
                        val newCard = Card(
                            id = card.id,
                            deckId = card.deckId,
                            deckUUID = card.deckUUID,
                            reviewsLeft = card.reviewsLeft,
                            nextReview = card.nextReview,
                            passes = card.passes,
                            prevSuccess = card.prevSuccess,
                            totalPasses = card.totalPasses,
                            type = card.type,
                            createdOn = card.createdOn,
                            partOfList = false,
                        )
                        deck.cardsLeft -= 1
                        viewModelScope.launch {
                            flashCardRepository.updateCard(newCard)
                        }
                    } else {
                        viewModelScope.launch {
                            flashCardRepository.updateCard(card)
                        }
                    }
                }
                jobs.joinAll().also {
                    viewModelScope.launch(Dispatchers.IO) {
                        flashCardRepository.updateCardsLeft(deck.id, deck.cardsLeft)
                        flashCardRepository.deleteSavedCards()
                    }
                }
                clearErrorState()
                true
            } catch (e: Exception) {
                val error = when (e) {
                    is IOException -> CardUpdateError.NetworkError(e)
                    is SQLiteException -> CardUpdateError.DatabaseError(e)
                    is TimeoutCancellationException -> CardUpdateError.TimeoutError(e)
                    else -> CardUpdateError.UnknownError(e)
                }
                thisErrorState.value = error
                thisErrorState.value?.let {
                    cardUE -> callError(cardUE)
                }
                false
            }
        }.also {
            viewModelScope.launch {
                cardListToUpdateUiState.update {
                    emptyList()
                }
            }
            /** Making sure the deck does not have any due cards left
             *  and it's still due for review */
            if (deck.cardsLeft <= 0 && deck.nextReview <= Date()){
                viewModelScope.launch(Dispatchers.IO){
                    updateNextReview(deck)
                }
            }
            transitionTo(CardState.Finished)
        }
    }

    suspend fun getBackupDueCards(id : Int) : Boolean {
        return withContext(Dispatchers.IO) {
            try {
                getBackupCards(flashCardRepository.getDeck(id))
                clearErrorState()
                false
            } catch (e: Exception) {
                val error = when (e) {
                    is IOException -> CardUpdateError.NetworkError(e)
                    is SQLiteConstraintException -> CardUpdateError.ConstraintError(e)
                    is SQLiteException -> CardUpdateError.DatabaseError(e)
                    is TimeoutCancellationException -> CardUpdateError.TimeoutError(e)
                    else -> CardUpdateError.UnknownError(e)
                }
                thisErrorState.value = error
                thisErrorState.value?.let {
                        cardUE -> callError(cardUE)
                }
                true
            } finally {
                transitionTo(CardState.Finished)
            }
        }
    }
}
