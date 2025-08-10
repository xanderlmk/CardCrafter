package com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories

import android.util.Log
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCT
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCTList
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.daos.CardTypesDao
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.PartOfQorA
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.AnswerParam
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.MiddleParam
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.Param
import com.belmontCrest.cardCrafter.model.ui.states.CDetails
import com.belmontCrest.cardCrafter.views.misc.details.toQuestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlin.text.isBlank

class OfflineCardTypeRepository(
    private val cardTypesDao: CardTypesDao,
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

    override suspend fun updateBasicCard(id: Int, question: String, answer: String) =
        cardTypesDao.updateBasicCard(id, question, answer)

    override suspend fun updateThreeCard(
        id: Int, question: String, middle: String, answer: String, isQOrA: PartOfQorA
    ) = cardTypesDao.updateThreeCard(id, question, middle, answer, isQOrA)

    override suspend fun updateHintCard(
        id: Int, question: String, hint: String, answer: String
    ) = cardTypesDao.updateHintCard(id, question, hint, answer)

    override suspend fun updateMultiChoiceCard(
        id: Int, newQuestion: String, newChoiceA: String, newChoiceB: String,
        newChoiceC: String, newChoiceD: String, newCorrect: Char
    ) = cardTypesDao.updateMultiChoiceCard(
        id, newQuestion, newChoiceA, newChoiceB, newChoiceC, newChoiceD, newCorrect
    )

    override suspend fun updateNotationCard(
        question: String, steps: String,
        answer: String, cardId: Int
    ) = cardTypesDao.updateNotationCard(question, steps, answer, cardId)

    override suspend fun updateCustomCard(
        id: Int, newQuestion: Param, newMiddle: MiddleParam, newAnswer: AnswerParam
    ) = cardTypesDao.updateCustomCard(id, newQuestion, newMiddle, newAnswer)

    override fun getAllCardTypesStream(deckId: Int) =
        cardTypesDao.getAllCardTypesStream(deckId).map {
            try {
                it.toCTList()
            } catch (e: IllegalStateException) {
                Log.d("CardTypeRepository", "$e")
                listOf()
            }
        }

    override suspend fun getAllCardTypes(deckId: Int) =
        try {
            cardTypesDao.getAllCardTypes(deckId).toCTList()
        } catch (e: Exception) {
            Log.d("CardTypeRepository", "$e")
            listOf()
        }

    override fun getAllDueCardsStream(
        deckId: Int, cardAmount: Int, currentTime: Long
    ) = cardTypesDao.getDueAllCardTypesFlow(deckId, cardAmount, currentTime).map {
        try {
            it.toCTList()
        } catch (e: IllegalStateException) {
            Log.d("CardTypeRepository", "$e")
            listOf()
        }
    }

    override fun getACardType(id: Int) = try {
        cardTypesDao.getACardType(id).toCT()
    } catch (e: IllegalStateException) {
        Log.d("CardTypeRepository", "$e")
        throw e
    }

    /** Get flow of a single CT */
    override fun getACardTypeStream(id: Int) = try {
        cardTypesDao.getACardTypeStream(id).map { it.toCT() }
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