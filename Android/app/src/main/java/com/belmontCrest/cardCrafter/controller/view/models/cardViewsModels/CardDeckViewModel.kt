package com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.controller.cardHandlers.callError
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCard
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.DeckContentRepository
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.SavedCard
import com.belmontCrest.cardCrafter.model.ui.states.CardState
import com.belmontCrest.cardCrafter.model.ui.CardUpdateError
import com.belmontCrest.cardCrafter.model.ui.states.DeckDetails
import com.belmontCrest.cardCrafter.model.ui.states.SealedAllCTs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class CardDeckViewModel(
    private val deckContentRepository: DeckContentRepository,
) : ViewModel() {

    private val _errorState = MutableStateFlow<CardUpdateError?>(null)
    val errorState: StateFlow<CardUpdateError?> = _errorState.asStateFlow()

    val cardListUiState = deckContentRepository.dueCardsState.stateIn(
        scope = viewModelScope, started = SharingStarted.Eagerly,
        initialValue = SealedAllCTs()
    )
    val savedCardUiState = deckContentRepository.savedCards.stateIn(
        scope = viewModelScope, started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )
    val deckNRState = deckContentRepository.deckNextReview
    val cardState = deckContentRepository.cardState

    /**
     * For when a user wants to redo a card.
     * First, we get the most recent saved card and get the card remains (e.g. deckId, etc)
     * Then we update the card and remove that saved card as a transaction.
     */
    suspend fun getRedoCardType(deck: Deck) = withContext(Dispatchers.IO) {
        try {
            val savedCard = savedCardUiState.value.first()
            val cardRemains = deckContentRepository.getCardRemains(savedCard.cardId)
            val card = savedCard.toCard(cardRemains)
            val cardsLeft = if (card.reviewsLeft <= 1) deck.cardsLeft + 1 else deck.cardsLeft
            val cardsDone = if (card.reviewsLeft <= 1) deck.cardsDone - 1 else deck.cardsDone
            val nextReview = deckNRState.value?.nextReview
            deckContentRepository.redoCard(
                card, savedCard, id = deck.id, cardsDone = cardsDone, cardsLeft = cardsLeft,
                nextReview = if (deck.nextReview > Date() && nextReview != null)
                    nextReview else deck.nextReview
            )
            clearErrorState()
            return@withContext true
        } catch (e: Exception) {
            _errorState.update { CardUpdateError.IllegalStateError(e) }
            _errorState.value?.let { cardUE -> callError(cardUE) }
            return@withContext false
        }
    }


    /** Update the card and add the savedCard as a transaction to the DB*/
    suspend fun addCardToUpdate(card: Card, savedCard: SavedCard, deck: Deck) =
        withContext(Dispatchers.IO) {
            if (card.nextReview > Date()) {
                val dd = DeckDetails(
                    id = deck.id,
                    cardsDone = (deck.cardsDone + 1),
                    cardsLeft = (deck.cardsLeft - 1),
                    nextReview = updateNextReview(deck)
                )
                deckContentRepository.updateCardWithDeck(card, savedCard, dd)
            } else {
                deckContentRepository.updateCard(card, savedCard)
            }
        }

    fun transitionTo(newState: CardState) = deckContentRepository.transitionTo(newState)

    private fun clearErrorState() = _errorState.update { null }

    val redoClicked = deckContentRepository.redoClicked.stateIn(
        scope = viewModelScope, initialValue = false, started = SharingStarted.Eagerly
    )

    fun updateRedoClicked(clicked: Boolean) = deckContentRepository.updateRedoClicked(clicked)
    /** Updating the nextReview for the deck which will only be
     * to the next day (++1 day). */
    private fun updateNextReview(deck: Deck) = with(Dispatchers.IO) {
        if (deck.cardsLeft <= 0 && deck.nextReview <= Date()) {
            val time = Calendar.getInstance()
            time.add(Calendar.DAY_OF_YEAR, 1)
            time.time
        } else {
            deck.nextReview
        }
    }

    fun updateTime() = deckContentRepository.updateInnerTime()

    init {
        updateTime()
        deckContentRepository.transitionTo(CardState.Finished)
    }
}
