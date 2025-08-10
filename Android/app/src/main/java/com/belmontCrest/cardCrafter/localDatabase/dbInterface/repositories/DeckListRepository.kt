package com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories

import android.content.SharedPreferences
import androidx.core.content.edit
import com.belmontCrest.cardCrafter.controller.view.models.deckViewsModels.TimeClass
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.daos.DeckDao
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.model.daoHelpers.OBS
import com.belmontCrest.cardCrafter.model.daoHelpers.OrderBy
import com.belmontCrest.cardCrafter.model.daoHelpers.toOrderedByClass
import com.belmontCrest.cardCrafter.model.daoHelpers.toOrderedString
import com.belmontCrest.cardCrafter.model.ui.states.DeckUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update

interface DeckListRepository {
    val deckUiState: Flow<DeckUiState>
    val orderedBy: StateFlow<OrderBy>
    fun updateOrder(orderBy: OrderBy)
    fun updateTime()
}

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineDeckListRepository(
    private val timeClass: TimeClass, private val appPref: SharedPreferences,
    private val deckDao: DeckDao
): DeckListRepository {
    private var isOrderedBy: String
        get() = appPref.getString("ordered_by", OBS.NAME_ASC) ?: OBS.NAME_ASC
        set(value) = appPref.edit { putString("ordered_by", value) }

    private val _orderedBy: MutableStateFlow<OrderBy> =
        MutableStateFlow(isOrderedBy.toOrderedByClass())
    override val orderedBy = _orderedBy.asStateFlow()

    override fun updateOrder(orderBy: OrderBy) {
        _orderedBy.update { orderBy }
        isOrderedBy = orderBy.toOrderedString()
    }
    private val currentTime = timeClass.time
    override val deckUiState = combine(currentTime, _orderedBy) { time, order ->
        getDecksAndCC(time, order)
    }.flatMapLatest { (decksFlow, cardCountsFlow) ->
        combine(decksFlow, cardCountsFlow) { decks, cardCounts ->
            DeckUiState(decks, cardCounts)
        }
    }

    private fun getDecksAndCC(
        currentTime: Long,
        orderBy: OrderBy
    ): Pair<Flow<List<Deck>>, Flow<List<Int>>> {
        return when (orderBy) {
            OrderBy.CreatedOnASC -> {
                val decks = deckDao.getDecksStreamOrderedByCreatedOnAsc()
                val cardAmount = deckDao.getCCOrderedByCreatedOnAsc(currentTime)
                Pair(decks, cardAmount)
            }

            OrderBy.CreatedOnDESC -> {
                val decks = deckDao.getDecksStreamOrderedByCreatedOnDesc()
                val cardAmount = deckDao.getCCOrderedByCreatedOnDesc(currentTime)
                Pair(decks, cardAmount)
            }

            OrderBy.NameASC -> {
                val decks = deckDao.getDecksStreamOrderedByNameAsc()
                val cardAmount = deckDao.getCCOrderedByNameAsc(currentTime)
                Pair(decks, cardAmount)
            }

            OrderBy.NameDESC -> {
                val decks = deckDao.getDecksStreamOrderedByNameDesc()
                val cardAmount = deckDao.getCCOrderedByNameDesc(currentTime)
                Pair(decks, cardAmount)
            }

            OrderBy.CardsLeftASC -> {
                val decks = deckDao.getDecksStreamOrderedByCardsLeftAsc()
                val cardAmount = deckDao.getCCOrderedByCardsLeftAsc(currentTime)
                Pair(decks, cardAmount)
            }

            OrderBy.CardsLeftDESC -> {
                val decks = deckDao.getDecksStreamOrderedByCardsLeftDesc()
                val cardAmount = deckDao.getCCOrderedByCardsLefDesc(currentTime)
                Pair(decks, cardAmount)
            }
        }
    }

    override fun updateTime() = timeClass.updateCurrentTime()
}