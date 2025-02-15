package com.example.flashcards.model.repositories


import com.example.flashcards.model.tablesAndApplication.AllCardTypes
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCard
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface CardTypeRepository {
    suspend fun insertBasicCard(basicCard: BasicCard): Long
    suspend fun insertThreeCard(threeFieldCard: ThreeFieldCard): Long
    suspend fun insertHintCard(hintCard: HintCard): Long
    suspend fun insertMultiChoiceCard(multiChoiceCard: MultiChoiceCard): Long

    suspend fun deleteBasicCard(cardId: Int)
    suspend fun deleteThreeCard(cardId: Int)
    suspend fun deleteHintCard(cardId: Int)
    suspend fun deleteMultiChoiceCard(cardId: Int)

    suspend fun updateBasicCard(id: Int, question: String, answer: String)

    suspend fun updateThreeCard(
        id: Int, question: String, middle: String,
        answer: String
    )

    suspend fun updateHintCard(
        id: Int, question: String, hint: String,
        answer: String
    )

    suspend fun updateMultiChoiceCard(
        id: Int,
        newQuestion: String,
        newChoiceA: String,
        newChoiceB: String,
        newChoiceC: String,
        newChoiceD: String,
        newCorrect: Char
    )

    fun getAllCardTypes(deckId: Int): Flow<List<AllCardTypes>>

    fun getAllDueCards(
        deckId: Int,
        cardAmount: Int,
        currentTime: Long = Date().time
    ): Flow<List<AllCardTypes>>

    suspend fun getACardType(id: Int): AllCardTypes

}