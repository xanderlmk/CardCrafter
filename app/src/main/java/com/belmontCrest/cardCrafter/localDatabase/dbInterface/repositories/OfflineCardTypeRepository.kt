package com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories

import android.util.Log
import com.belmontCrest.cardCrafter.controller.cardHandlers.mapACardTypeToCT
import com.belmontCrest.cardCrafter.controller.cardHandlers.mapAllCardTypesToCTs
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.daoInterfaces.allCardTypesDao.BasicCardDao
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.daoInterfaces.allCardTypesDao.HintCardDao
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.daoInterfaces.allCardTypesDao.MultiChoiceCardDao
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.daoInterfaces.allCardTypesDao.ThreeCardDao
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.daoInterfaces.deckAndCardDao.CardTypesDao
import com.belmontCrest.cardCrafter.localDatabase.tables.BasicCard
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.HintCard
import com.belmontCrest.cardCrafter.localDatabase.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.localDatabase.tables.PartOfQorA
import com.belmontCrest.cardCrafter.localDatabase.tables.ThreeFieldCard
import com.belmontCrest.cardCrafter.model.ui.states.CDetails
import com.belmontCrest.cardCrafter.views.miscFunctions.details.toQuestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlin.text.isBlank

class OfflineCardTypeRepository(
    private val cardTypesDao: CardTypesDao,
    private val basicCardDao: BasicCardDao,
    private val hintCardDao: HintCardDao,
    private val threeCardDao: ThreeCardDao,
    private val multiChoiceCardDao: MultiChoiceCardDao,
) : CardTypeRepository {

    private val _selectedCards = MutableStateFlow(emptyList<CT>())
    override val selectedCards = _selectedCards.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    override val searchQuery = _searchQuery.asStateFlow()

    override fun toggleCard(ct: CT) {
        _selectedCards.update { cards ->
            val mutableCards = cards.toMutableList()
            if (!mutableCards.contains(ct)) {
                mutableCards.add(ct)
            } else {
                mutableCards.remove(ct)
            }
            mutableCards
        }
    }

    override suspend fun toggleAllCards(deckId: Int) {
        _selectedCards.update {
            val cts = getAllCardTypes(deckId)
            val filtered = cts.filter { ct ->
                if (_searchQuery.value.isBlank()) return@filter true

                ct.toQuestion().contains(_searchQuery.value, ignoreCase = true)
            }
            Log.d("OfflineCTRepo", "${filtered.size}")
            filtered
        }
    }

    override fun deselectAll() = _selectedCards.update { emptyList() }


    override fun updateQuery(query: String) = _searchQuery.update { query }


    override fun resetQuery() = _searchQuery.update { "" }

    override suspend fun deleteBasicCard(basicCard: BasicCard) =
        basicCardDao.deleteBasicCard(basicCard)

    override suspend fun deleteHintCard(hintCard: HintCard) =
        hintCardDao.deleteHintCard(hintCard)

    override suspend fun deleteThreeCard(threeFieldCard: ThreeFieldCard) =
        threeCardDao.deleteThreeCard(threeFieldCard)

    override suspend fun deleteMultiChoiceCard(multiChoiceCard: MultiChoiceCard) =
        multiChoiceCardDao.deleteMultiChoiceCard(multiChoiceCard)

    override suspend fun updateBasicCard(id: Int, question: String, answer: String) =
        basicCardDao.updateBasicCard(id, question, answer)

    override suspend fun updateThreeCard(
        id: Int, question: String, middle: String, answer: String, isQOrA: PartOfQorA
    ) = threeCardDao.updateThreeCard(id, question, middle, answer, isQOrA)

    override suspend fun updateHintCard(
        id: Int, question: String, hint: String, answer: String
    ) = hintCardDao.updateHintCard(id, question, hint, answer)

    override suspend fun updateMultiChoiceCard(
        id: Int, newQuestion: String, newChoiceA: String, newChoiceB: String,
        newChoiceC: String, newChoiceD: String, newCorrect: Char
    ) = multiChoiceCardDao.updateMultiChoiceCard(
        id, newQuestion, newChoiceA, newChoiceB, newChoiceC, newChoiceD, newCorrect
    )

    override fun getAllCardTypesStream(deckId: Int) =
        cardTypesDao.getAllCardTypesStream(deckId).map {
            try {
                mapAllCardTypesToCTs(it)
            } catch (e: IllegalStateException) {
                Log.d("CardTypeRepository", "$e")
                listOf<CT>()
            }
        }

    override suspend fun getAllCardTypes(deckId: Int) =
        try {
            mapAllCardTypesToCTs(cardTypesDao.getAllCardTypes(deckId))
        } catch (e: Exception) {
            Log.d("CardTypeRepository", "$e")
            listOf<CT>()
        }

    override fun getAllDueCardsStream(
        deckId: Int, cardAmount: Int, currentTime: Long
    ) = cardTypesDao.getDueAllCardTypesFlow(deckId, cardAmount, currentTime).map {
        try {
            mapAllCardTypesToCTs(it)
        } catch (e: IllegalStateException) {
            Log.d("CardTypeRepository", "$e")
            listOf<CT>()
        }
    }

    override fun getAllDueCards(
        deckId: Int, cardAmount: Int, currentTime: Long
    ) = try {
        mapAllCardTypesToCTs(cardTypesDao.getDueAllCardTypes(deckId, cardAmount, currentTime))
    } catch (e: IllegalStateException) {
        Log.d("CardTypeRepository", "$e")
        listOf<CT>()
    }


    override fun getACardType(id: Int) = try {
        mapACardTypeToCT(cardTypesDao.getACardType(id))
    } catch (e: IllegalStateException) {
        Log.d("CardTypeRepository", "$e")
        throw e
    }

    /** Get flow of a single CT */
    override fun getACardTypeStream(id: Int) = try {
        cardTypesDao.getACardTypeStream(id).map { mapACardTypeToCT(it) }
    } catch (e: IllegalStateException) {
        Log.d("CardTypeRepository", "$e")
        throw e
    }

    override suspend fun copyCardList(deck: Deck) =
        cardTypesDao.copyCardList(_selectedCards.value, deck)

    override suspend fun moveCardList(deck: Deck) =
        cardTypesDao.moveCardList(_selectedCards.value, deck)

    override suspend fun updateCT(
        cardId: Int, type: String, fields: CDetails, deleteCT: CT
    ) = cardTypesDao.updateCT(cardId, type, fields, deleteCT)

    override suspend fun deleteCTs() = cardTypesDao.deleteCardList(_selectedCards.value)
}