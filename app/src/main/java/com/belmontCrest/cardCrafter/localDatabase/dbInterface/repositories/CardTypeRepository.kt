package com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories

import com.belmontCrest.cardCrafter.localDatabase.tables.BasicCard
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.HintCard
import com.belmontCrest.cardCrafter.localDatabase.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.localDatabase.tables.PartOfQorA
import com.belmontCrest.cardCrafter.localDatabase.tables.ThreeFieldCard
import com.belmontCrest.cardCrafter.model.ui.Fields
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

interface CardTypeRepository {

    val selectedCards: StateFlow<List<CT>>
    fun toggleCard(ct: CT)
    fun clearSelection()

    suspend fun toggleAllCards(deckId: Int)
    val searchQuery: StateFlow<String>
    fun updateQuery(query: String)
    fun resetQuery()

    suspend fun deleteBasicCard(basicCard: BasicCard)
    suspend fun deleteThreeCard(threeFieldCard: ThreeFieldCard)
    suspend fun deleteHintCard(hintCard: HintCard)
    suspend fun deleteMultiChoiceCard(multiChoiceCard: MultiChoiceCard)

    suspend fun updateBasicCard(id: Int, question: String, answer: String)

    suspend fun updateThreeCard(
        id: Int, question: String, middle: String, answer: String, isQOrA: PartOfQorA
    )

    suspend fun updateHintCard(id: Int, question: String, hint: String, answer: String)

    suspend fun updateMultiChoiceCard(
        id: Int, newQuestion: String, newChoiceA: String, newChoiceB: String,
        newChoiceC: String, newChoiceD: String, newCorrect: Char
    )

    suspend fun getAllCardTypes(deckId: Int): List<CT>

    fun getAllCardTypesStream(deckId: Int): Flow<List<CT>>

    fun getAllDueCards(deckId: Int, cardAmount: Int, currentTime: Long): Flow<List<CT>>

    fun getDueAllCardTypes(deckId: Int, cardAmount: Int, currentTime: Long = Date().time):
            List<CT>

    fun getACardType(id: Int): CT

    fun getACardTypeStream(id: Int): Flow<CT>


    suspend fun updateCT(cardId: Int, type: String, fields: Fields, deleteCT: CT)

    suspend fun deleteCTs()
}