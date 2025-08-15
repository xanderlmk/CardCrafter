package com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCTList
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.daos.CardTypesDao
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.daos.DeckDao
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.daos.DueCardsDao
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.daos.SavedCardDao
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.CardRemains
import com.belmontCrest.cardCrafter.localDatabase.tables.SavedCard
import com.belmontCrest.cardCrafter.model.ui.states.CSS
import com.belmontCrest.cardCrafter.model.ui.states.CardState
import com.belmontCrest.cardCrafter.model.ui.states.DeckDetails
import com.belmontCrest.cardCrafter.model.ui.states.DeckNextReview
import com.belmontCrest.cardCrafter.model.ui.states.SealedAllCTs
import com.belmontCrest.cardCrafter.model.ui.states.StringVar
import com.belmontCrest.cardCrafter.model.ui.states.WhichDeck
import com.belmontCrest.cardCrafter.model.ui.states.toCSString
import com.belmontCrest.cardCrafter.model.ui.states.toCardState
import com.belmontCrest.cardCrafter.views.misc.CARD_CRAFTER
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.Date

interface DeckContentRepository {
    val sealedAllCTs: Flow<SealedAllCTs>
    val dueCardsState: Flow<SealedAllCTs>
    val savedCards: Flow<List<SavedCard>>
    val wd: Flow<WhichDeck>
    val deckName: Flow<StringVar>
    val deckNextReview: StateFlow<DeckNextReview?>
    val redoClicked: StateFlow<Boolean>
    val cardState: StateFlow<CardState>
    fun transitionTo(newState: CardState)
    fun updateDeckId(id: Int)
    suspend fun updateDeckNextReview(id: Int)
    fun updateSavedCards(
        cardId: Int, reviewsLeft: Int, nextReview: Long, passes: Int,
        prevSuccess: Boolean, totalPasses: Int, partOfList: Boolean
    )

    suspend fun updateCardWithDeck(card: Card, savedCard: SavedCard, dd: DeckDetails)

    suspend fun deleteSavedCards()
    fun updateRedoClicked(clicked: Boolean)
    suspend fun getCardRemains(cardId: Int): CardRemains
    suspend fun redoCard(
        card: Card, savedCard: SavedCard, id: Int, nextReview: Date, cardsLeft: Int, cardsDone: Int
    )

    suspend fun updateCard(card: Card, savedCard: SavedCard)
    fun updateInnerTime()
}

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineDeckContentRepo(
    private val cardTypesDao: CardTypesDao, private val deckDao: DeckDao,
    private val savedCardDao: SavedCardDao, private val dueCardsDao: DueCardsDao,
    val appPref: SharedPreferences
) : DeckContentRepository {
    private var savedId: Int
        get() = appPref.getInt("deck_id", 0)
        set(value) = appPref.edit { putInt("deck_id", value) }
    private var savedCardState: String
        get() = appPref.getString("card_state", CSS.IDLE) ?: CSS.IDLE
        set(value) = appPref.edit { putString("card_sate", value) }
    private var savedDate: Long
        get() = appPref.getLong("saved_date", 0L)
        set(value) = appPref.edit { putLong("saved_date", value) }
    private val deckId = MutableStateFlow(savedId)

    private val _cardState: MutableStateFlow<CardState> =
        MutableStateFlow(savedCardState.toCardState())
    override val cardState = _cardState.asStateFlow()

    override fun updateDeckId(id: Int) = deckId.update { savedId = id; id }

    override fun transitionTo(newState: CardState) = _cardState.update {
        savedCardState = newState.toCSString(); newState
    }

    override val savedCards = savedCardDao.getAllSavedCards()
    override fun updateSavedCards(
        cardId: Int, reviewsLeft: Int, nextReview: Long, passes: Int,
        prevSuccess: Boolean, totalPasses: Int, partOfList: Boolean
    ) = savedCardDao.updateCardsOnStart(
        cardId, reviewsLeft, nextReview, passes, prevSuccess, totalPasses, partOfList
    )

    override suspend fun deleteSavedCards() = savedCardDao.deleteSavedCards()

    private val _time = MutableStateFlow(System.currentTimeMillis())

    override fun updateInnerTime() = _time.update { Date().time }
    override val sealedAllCTs = deckId.flatMapLatest { id ->
        if (id == 0) {
            flowOf(SealedAllCTs())
        } else {
            cardTypesDao.getAllCardTypesStream(id).map {
                try {
                    SealedAllCTs(it.toCTList().toMutableList())
                } catch (e: IllegalStateException) {
                    Log.e("CardTypeRepository", "$e")
                    SealedAllCTs()
                }
            }
        }
    }

    override val wd = deckId.flatMapLatest { id ->
        if (id == 0) flowOf(WhichDeck())
        else deckDao.getDeckFlow(id).map { WhichDeck(it) }
    }

    override val dueCardsState = combine(wd, _time) { d, time ->
        d to time
    }.flatMapLatest { (d, time) ->
        val deck = d.deck
        if (deck == null) flowOf(SealedAllCTs())
        else  cardTypesDao.getDueAllCardTypesFlow(deck.id, deck.cardsLeft, time).toCTList().map {
            SealedAllCTs(it)
        }
    }

    private val _deckNextReview = MutableStateFlow(
        if (savedDate <= 0L) null else DeckNextReview(Date(savedDate))
    )
    override val deckNextReview = _deckNextReview.asStateFlow()
    override suspend fun updateDeckNextReview(id: Int) =
        if (id > 0) _deckNextReview.update { deckDao.getNextReview(id) }
        else { }

    override val deckName = deckId.flatMapLatest { id ->
        if (id == 0) flowOf(StringVar())
        else deckDao.getDeckName(id).map { StringVar(it ?: "") }
    }
    private val _redoClicked = MutableStateFlow(false)
    override val redoClicked = _redoClicked.asStateFlow()

    override fun updateRedoClicked(clicked: Boolean) = _redoClicked.update { clicked }

    override suspend fun getCardRemains(cardId: Int) = savedCardDao.getCardRemains(cardId)
    override suspend fun updateCard(card: Card, savedCard: SavedCard) =
        dueCardsDao.updateCardWithSavedCard(card, savedCard)


    override suspend fun redoCard(
        card: Card, savedCard: SavedCard, id: Int, nextReview: Date, cardsLeft: Int, cardsDone: Int
    ) = dueCardsDao.getCardAndRemoveSavedOne(
        card, savedCard, id, cardsLeft = cardsLeft, cardsDone = cardsDone, nextReview
    )

    override suspend fun updateCardWithDeck(card: Card, savedCard: SavedCard, dd: DeckDetails) =
        dueCardsDao.updateDeckAndCard(
            dd.id, dd.nextReview, dd.cardsLeft, dd.cardsDone, card, savedCard
        )
}