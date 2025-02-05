package com.example.flashcards.controller.viewModels.cardViewsModels

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.model.repositories.CardTypeRepository
import com.example.flashcards.model.repositories.FlashCardRepository
import com.example.flashcards.model.tablesAndApplication.AllCardTypes
import com.example.flashcards.model.tablesAndApplication.CT
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.tablesAndApplication.SavedCard
import com.example.flashcards.model.uiModels.CardDeckCardLists
import com.example.flashcards.model.uiModels.CardState
import com.example.flashcards.model.uiModels.CardUpdateError
import com.example.flashcards.model.uiModels.SealedDueCTs
import com.example.flashcards.views.cardViews.cardDeckViews.returnCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.IOException
import java.util.Calendar
import java.util.Date
import kotlin.String

class CardDeckViewModel(
    private val flashCardRepository: FlashCardRepository,
    private val cardTypeRepository: CardTypeRepository,
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
        private const val EXTRA_TIME = 4_500L
    }

    private val errorMessage: MutableStateFlow<String> = MutableStateFlow("")
    private val _errorState = MutableStateFlow<CardUpdateError?>(null)
    val errorState: StateFlow<CardUpdateError?> = _errorState.asStateFlow()

    private val cardState: MutableState<CardState> = mutableStateOf(CardState.Idle)
    private val cardListToUpdateUiState = MutableStateFlow<List<Card>>(emptyList())
    var cardListToUpdate = cardListToUpdateUiState.asStateFlow()

    private var backupCardListState = MutableStateFlow<List<Card>>(emptyList())
    val backupCardList = backupCardListState.asStateFlow()

    private var thisSealedDueCTs = MutableStateFlow(SealedDueCTs())
    val sealedDueCTs = thisSealedDueCTs.asStateFlow()

    fun addCardToTheUpdateCardsList(card: Card) {
        cardListToUpdateUiState.update { cards ->
            val mutableCards = cards.toMutableList()
            if (!mutableCards.contains(card)) {
                mutableCards.add(card)
            }
            mutableCards
        }
    }

    suspend fun getRedoCard(cardId: Int): Card {
        return withContext(Dispatchers.IO) {
            flashCardRepository.getCardById(cardId)
        }.also {
            addCardToUpdate(it)
        }
    }

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

    suspend fun getRedoCardType(cardId: Int, index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                val ct = mapACardTypeToCT(cardTypeRepository.getACardType(cardId))
                viewModelScope.launch {
                    thisSealedDueCTs.update {
                        it.allCTs[index] = ct
                        it.savedCTs[index] = ct
                        it
                    }
                }
            } catch (e: IllegalStateException) {
                Log.e("GetRedoCardType", "Invalid AllCardType data: ${e.message}")
                thisSealedDueCTs.value.allCTs[index]
            }
        }
    }

    suspend fun getBackupCards(deck: Deck) {
        return withContext(Dispatchers.IO) {
            if (deck.nextReview <= Date()) {
                viewModelScope.launch {
                    backupCardListState.update {
                        flashCardRepository.getBackupDueCards(
                            deck.id, deck.cardAmount
                        )
                    }
                }
            }
        }
    }

    private suspend fun getDueTypesForDeck(deck: Deck) {
        return withContext(Dispatchers.IO)
        {
            var complete = false
            try {
                if (deck.nextReview <= Date()) {
                    if (!thisSealedDueCTs.value.collected) {
                        viewModelScope.launch {
                            withTimeout(EXTRA_TIME) {
                                cardTypeRepository.getDueAllCardTypes(
                                    deck.id, deck.cardsLeft
                                ).map { allCards ->
                                    updateUiState(allCards)
                                }.collect { state ->
                                    addToList(state.allCards.map {
                                        it.card
                                    })
                                    thisSealedDueCTs.update {
                                        updateSealedUiState(state.allCards)
                                    }
                                    complete = true
                                }
                            }
                        }
                    } else {
                        viewModelScope.launch {
                            withTimeout(TIMEOUT_MILLIS) {
                                cardTypeRepository.getDueAllCardTypes(
                                    deck.id, deck.cardsLeft
                                ).map { allCards ->
                                    updateUiState(allCards)
                                }.collect { state ->
                                    thisSealedDueCTs.update {
                                        updateSealedUiState(state.allCards)
                                    }
                                    complete = true
                                }
                            }
                        }
                    }
                } else {
                    viewModelScope.launch {
                        thisSealedDueCTs.update { thisState ->
                            thisState.copy(
                                allCTs = mutableListOf(),
                                savedCTs = mutableListOf(),
                                collected = false
                            )
                        }.also {
                            complete = true
                        }
                    }
                }
                while (!complete) {
                    delay(20)
                }
                return@withContext
            } catch (e: TimeoutCancellationException) {
                errorMessage.value = "Request timed out. Please try again."
                println(e)
                return@withContext
            }
        }
    }

    private fun updateUiState(
        allCards: List<AllCardTypes>, collected: Boolean = true
    ): CardDeckCardLists {
        return CardDeckCardLists(
            allCards = allCards,
            savedCardList = allCards,
            collected = collected,
        )
    }

    private fun updateSealedUiState(
        allCards: List<AllCardTypes>, collected: Boolean = true
    ): SealedDueCTs {
        var allCTs = try {
            mapAllCardTypesToCTs(allCards)
        } catch (e: IllegalStateException) {
            Log.e(
                "GetDueTypesForDeck",
                "Invalid AllCardType data: ${e.message}"
            )
            return SealedDueCTs()
        }
        return SealedDueCTs(
            allCTs = allCTs.toMutableList(),
            savedCTs = allCTs.toMutableList(),
            collected = collected
        )
    }

    private fun addToList(cards: List<Card>) {
        cards.map {
            viewModelScope.launch(Dispatchers.IO) {
                flashCardRepository.becomePartOfList(it.id)
            }
        }
    }

    fun transitionTo(newState: CardState) {
        cardState.value = newState
    }

    fun getState(): CardState = cardState.value

    fun setErrorMessage(message: String) {
        thisSealedDueCTs.value = thisSealedDueCTs.value.copy(errorMessage = message)
    }

    private fun clearErrorMessage() {
        thisSealedDueCTs.value = thisSealedDueCTs.value.copy(errorMessage = "")
    }

    private fun clearErrorState() {
        _errorState.value = null
    }


    fun updateNextReview(deck: Deck) {
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
                    if (deck.nextReview <= Date()) {
                        getDueCards(deck.id)
                    }
                    viewModelScope.launch(Dispatchers.IO) {
                        flashCardRepository.updateCardsLeft(deck.id, deck.cardsLeft)
                    }
                }
                clearErrorState()
                true
            } catch (e: Exception) {
                val error = when (e) {
                    is IOException -> CardUpdateError.NetworkError(e)
                    is SQLiteException -> CardUpdateError.DatabaseError(e)
                    else -> CardUpdateError.UnknownError(e)
                }
                _errorState.value = error
                false
            }
        }.also {
            viewModelScope.launch {
                cardListToUpdateUiState.update {
                    emptyList()
                }
            }
        }
    }


    suspend fun getDueCards(id : Int) {
        return withContext(Dispatchers.IO) {
            try {
                getDueTypesForDeck(flashCardRepository.getDeck(id))
                clearErrorMessage()
            } catch (e: Exception) {
                handleError(e, "Something went wrong")
            } catch (e: SQLiteConstraintException) {
                handleError(e, "SQLite Exception")
            } finally {
                transitionTo(CardState.Finished)
            }
        }
    }

    private fun handleError(e: Exception, prefix: String): Boolean {
        val message = "$prefix: $e"
        errorMessage.value = message
        setErrorMessage(message)
        return true
    }

}

// The mapping function
fun mapAllCardTypesToCTs(allCardTypesList: List<AllCardTypes>): List<CT> {
    return allCardTypesList.map { allCardTypes ->
        when {
            allCardTypes.basicCard != null -> CT.Basic(
                allCardTypes.card,
                allCardTypes.basicCard
            )

            allCardTypes.hintCard != null -> CT.Hint(
                allCardTypes.card,
                allCardTypes.hintCard
            )

            allCardTypes.threeFieldCard != null -> CT.ThreeField(
                allCardTypes.card,
                allCardTypes.threeFieldCard
            )

            allCardTypes.multiChoiceCard != null -> CT.MultiChoice(
                allCardTypes.card,
                allCardTypes.multiChoiceCard
            )

            else -> throw IllegalStateException("Invalid AllCardTypes: all card types are null")
        }
    }
}

fun mapACardTypeToCT(cardTypes: AllCardTypes): CT {
    return cardTypes.let {
        return when {
            it.basicCard != null -> CT.Basic(it.card, it.basicCard)
            it.hintCard != null -> CT.Hint(it.card, it.hintCard)
            it.threeFieldCard != null -> CT.ThreeField(it.card, it.threeFieldCard)
            it.multiChoiceCard != null -> CT.MultiChoice(it.card, it.multiChoiceCard)
            else -> throw IllegalStateException("Invalid AllCardTypes: all card types are null")
        }
    }
}