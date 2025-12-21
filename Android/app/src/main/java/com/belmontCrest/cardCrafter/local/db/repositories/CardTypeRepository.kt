package com.belmontCrest.cardCrafter.local.db.repositories

import com.belmontCrest.cardCrafter.local.db.tables.CT
import com.belmontCrest.cardCrafter.local.db.tables.Deck
import com.belmontCrest.cardCrafter.local.db.tables.PartOfQorA
import com.belmontCrest.cardCrafter.local.db.tables.customCardInit.AnswerParam
import com.belmontCrest.cardCrafter.local.db.tables.customCardInit.MiddleParam
import com.belmontCrest.cardCrafter.local.db.tables.customCardInit.Param
import com.belmontCrest.cardCrafter.model.ui.states.CDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface CardTypeRepository {

    val selectedCards: StateFlow<List<CT>>
    fun toggleCard(ct: CT)
    fun deselectAll()
    suspend fun toggleAllCards(deckId: Int)

    val searchQuery: StateFlow<String>
    fun updateQuery(query: String)
    fun resetQuery()

    suspend fun updateBasicCard(id: Int, question: String, answer: String)

    suspend fun updateThreeCard(
        id: Int, question: String, middle: String, answer: String, isQOrA: PartOfQorA
    )

    suspend fun updateHintCard(id: Int, question: String, hint: String, answer: String)

    suspend fun updateMultiChoiceCard(
        id: Int, newQuestion: String, newChoiceA: String, newChoiceB: String,
        newChoiceC: String, newChoiceD: String, newCorrect: Char
    )

    suspend fun updateNotationCard(
        question: String, steps: String,
        answer: String, cardId: Int
    )

    suspend fun updateCustomCard(
        id: Int, newQuestion: Param, newMiddle: MiddleParam, newAnswer: AnswerParam
    )

    suspend fun getAllCardTypes(deckId: Int): List<CT>

    fun getAllCardTypesStream(deckId: Int): Flow<List<CT>>

    fun getAllDueCardsStream(deckId: Int, cardAmount: Int, currentTime: Long): Flow<List<CT>>

    fun getACardType(id: Int): CT?

    fun getACardTypeStream(id: Int): Flow<CT>

    suspend fun copyCardList(deck: Deck)

    suspend fun moveCardList(deck: Deck)

    suspend fun updateCT(cardId: Int, type: String, fields: CDetails, deleteCT: CT)

    suspend fun deleteCTs()
}