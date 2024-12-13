package com.example.flashcards.model.repositories

import com.example.flashcards.model.tablesAndApplication.AllCardTypes
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.BasicCardType
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.HintCardType
import com.example.flashcards.model.tablesAndApplication.ThreeCardType
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface CardTypeRepository {
    suspend fun insertBasicCard(basicCard: BasicCard)
    suspend fun insertThreeCard(threeFieldCard: ThreeFieldCard)
    suspend fun insertHintCard(hintCard: HintCard)

    fun getBasicCard(cardId :Int) : Flow<BasicCardType>
    fun getThreeCard(cardId :Int) : Flow<ThreeCardType>
    fun getHintCard(cardId :Int) : Flow<HintCardType>

    suspend fun deleteBasicCard(cardId: Int)
    suspend fun deleteThreeCard(cardId: Int)
    suspend fun deleteHintCard(cardId: Int)

    suspend fun updateBasicCard(id: Int, question: String, answer: String)

    suspend fun updateThreeCard(id: Int, question: String, middle: String,
                                answer: String)
    suspend fun updateHintCard(id: Int, question: String, hint : String,
                               answer: String)

    fun getAllBasicCards() : List<BasicCardType>
    fun getAllThreeCards() : List<ThreeCardType>
    fun getAllHintCards() : List<HintCardType>
    fun getAllCardTypes(deckId : Int) : Flow<List<AllCardTypes>>

    fun getDueBasicCards(deckId : Int, currentTime : Long = Date().time) : Flow<List<BasicCardType>>
    fun getDueThreeCards(deckId : Int, currentTime : Long = Date().time) : Flow<List<ThreeCardType>>
    fun getDueHintCards(deckId : Int, currentTime : Long = Date().time) : Flow<List<HintCardType>>
    fun getDueAllCardTypes(deckId : Int, currentTime : Long = Date().time) :
            Flow<List<AllCardTypes>>


}