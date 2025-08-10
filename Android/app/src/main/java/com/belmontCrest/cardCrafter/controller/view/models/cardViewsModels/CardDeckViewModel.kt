package com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.controller.cardHandlers.callError
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCard
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.DeckContentRepository
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.SavedCard
import com.belmontCrest.cardCrafter.model.ui.states.CardState
import com.belmontCrest.cardCrafter.model.ui.CardUpdateError
import com.belmontCrest.cardCrafter.model.ui.states.SealedAllCTs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class CardDeckViewModel(
    private val flashCardRepository: FlashCardRepository,
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
            if (card.reviewsLeft <= 1) {
                deck.cardsLeft += 1
                deck.cardsDone -= 1
            }
            if (deck.nextReview > Date())
                deckNRState.value?.let {
                    flashCardRepository.updateNextReview(it.nextReview, deck.id)
                }
            flashCardRepository.updateCardsLeft(deck.id, deck.cardsLeft, deck.cardsDone)
            deckContentRepository.redoCard(card, savedCard)
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
                deck.cardsLeft -= 1
                deck.cardsDone += 1
                flashCardRepository.updateCardsLeft(deck.id, deck.cardsLeft, deck.cardsDone)
            }
            if (deck.cardsLeft <= 0 && deck.nextReview <= Date())
                updateNextReview(deck)
            deckContentRepository.updateCard(card, savedCard)
        }

    fun transitionTo(newState: CardState) = deckContentRepository.transitionTo(newState)

    private fun clearErrorState() = _errorState.update { null }

    val redoClicked = deckContentRepository.redoClicked.stateIn(
        scope = viewModelScope, initialValue = false, started = SharingStarted.Eagerly
    )

    fun updateRedoClicked(clicked: Boolean) = deckContentRepository.updateRedoClicked(clicked)

    fun updateIndex(index: Int) = deckContentRepository.updateIndex(index)

    /** Updating the nextReview for the deck which will only be
     * to the next day (++1 day). */
    private suspend fun updateNextReview(deck: Deck) = withContext(Dispatchers.IO) {
        val time = Calendar.getInstance()
        time.add(Calendar.DAY_OF_YEAR, 1)
        deck.nextReview = time.time
        flashCardRepository.updateNextReview(
            deck.nextReview, deck.id
        )
    }

    fun updateWhichDeck(i: Int) = deckContentRepository.updateDeckId(i)
}
