package com.example.flashcards.model.repositories


import com.example.flashcards.model.tablesAndApplication.AllCardTypes
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.BasicCardType
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.HintCardType
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCard
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCardType
import com.example.flashcards.model.tablesAndApplication.ThreeCardType
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface CardTypeRepository {
    suspend fun insertBasicCard(basicCard: BasicCard): Long
    suspend fun insertThreeCard(threeFieldCard: ThreeFieldCard): Long
    suspend fun insertHintCard(hintCard: HintCard): Long
    suspend fun insertMultiChoiceCard(multiChoiceCard: MultiChoiceCard): Long

    fun getBasicCard(cardId: Int): Flow<BasicCardType>
    fun getThreeCard(cardId: Int): Flow<ThreeCardType>
    fun getHintCard(cardId: Int): Flow<HintCardType>
    fun getMultiChoiceCard(cardId: Int): Flow<MultiChoiceCardType>

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

    fun getAllBasicCards(deckId: Int): Flow<List<BasicCardType>>
    fun getAllThreeCards(deckId: Int): Flow<List<ThreeCardType>>
    fun getAllHintCards(deckId: Int): Flow<List<HintCardType>>
    fun getAllMultiChoiceCards(deckId: Int): Flow<List<MultiChoiceCardType>>
    fun getAllCardTypes(deckId: Int): Flow<List<AllCardTypes>>

    fun getDueBasicCards(
        deckId: Int,
        currentTime: Long = Date().time
    ): Flow<List<BasicCardType>>

    fun getDueThreeCards(
        deckId: Int,
        currentTime: Long = Date().time
    ): Flow<List<ThreeCardType>>

    fun getDueHintCards(
        deckId: Int,
        currentTime: Long = Date().time
    ): Flow<List<HintCardType>>

    fun getDueMultiChoiceCards(
        deckId: Int,
        currentTime: Long = Date().time
    ): Flow<List<MultiChoiceCardType>>

    suspend fun getDueAllCardTypes(
        deckId: Int,
        cardAmount: Int,
        currentTime: Long = Date().time
    ): Flow<List<AllCardTypes>>

    suspend fun getACardType(id: Int): AllCardTypes

    suspend fun getCurrentDueAllCardTypes(
        deckId: Int,
        currentTime: Long = Date().time
    ): Flow<List<AllCardTypes>>

}