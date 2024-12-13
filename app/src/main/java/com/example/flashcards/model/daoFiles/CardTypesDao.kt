package com.example.flashcards.model.daoFiles

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.flashcards.model.tablesAndApplication.AllCardTypes
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.BasicCardType
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.HintCardType
import com.example.flashcards.model.tablesAndApplication.ThreeCardType
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface CardTypesDao{
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBasicCard(basicCard: BasicCard)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertThreeCard(threeFieldCard: ThreeFieldCard)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertHintCard(hintCard: HintCard)

    @Transaction
    @Query("SELECT * FROM cards where id = :cardId ")
    fun getBasicCard(cardId :Int) : Flow<BasicCardType>
    @Transaction
    @Query("SELECT * FROM cards where id = :cardId ")
    fun getThreeCard(cardId :Int) : Flow<ThreeCardType>
    @Transaction
    @Query("SELECT * FROM cards where id = :cardId ")
    fun getHintCard(cardId :Int) : Flow<HintCardType>

    @Query("DELETE FROM basicCard WHERE cardId = :cardId")
    suspend fun deleteBasicCard(cardId : Int)

    @Query("DELETE FROM threeFieldCard WHERE cardId = :cardId")
    suspend fun deleteThreeCard(cardId: Int)

    @Query("DELETE FROM hintCard WHERE cardId = :cardId")
    suspend fun deleteHintCard(cardId: Int)

    @Query("""
        Update basicCard set question = :newQuestion, 
        answer = :newAnswer where cardId = :id""")
    suspend fun updateBasicCard(id: Int, newQuestion: String, newAnswer: String)

    @Transaction
    @Query("SELECT * FROM cards")
    fun getAllBasicCards(): List<BasicCardType>

    @Transaction
    @Query("SELECT * FROM cards")
    fun getAllThreeCards(): List<ThreeCardType>

    @Transaction
    @Query("SELECT * from cards")
    fun getAllHintCards(): List<HintCardType>

    @Transaction
    @Query("SELECT * FROM cards WHERE deckId = :deckId AND nextReview <= :currentTime")
    fun getDueBasicCards(deckId : Int, currentTime : Long = Date().time) : Flow<List<BasicCardType>>

    @Transaction
    @Query("""SELECT * FROM cards WHERE deckId = :deckId 
        AND nextReview <= :currentTime ORDER BY cards.id""")
    fun getDueAllCardTypes(deckId : Int, currentTime : Long = Date().time) :
            Flow<List<AllCardTypes>>

    @Transaction
    @Query("""SELECT * FROM cards WHERE deckId = :deckId
        ORDER BY cards.id""")
    fun getAllCardTypes(deckId : Int) :
            Flow<List<AllCardTypes>>

    @Transaction
    @Query("SELECT * FROM cards WHERE deckId = :deckId AND nextReview <= :currentTime")
    fun getDueThreeCards(deckId : Int, currentTime : Long = Date().time) : Flow<List<ThreeCardType>>

    @Transaction
    @Query("SELECT * FROM cards WHERE deckId = :deckId AND nextReview <= :currentTime")
    fun getDueHintCards(deckId : Int, currentTime : Long = Date().time) : Flow<List<HintCardType>>

    @Query("""
        Update threeFieldCard
        Set question = :newQuestion, 
        middle = :newMiddle,
        answer = :newAnswer
        where cardId = :id""")
    suspend fun updateThreeCard(id: Int, newQuestion: String, newMiddle: String, newAnswer: String)

    @Query("""
        Update hintCard
        Set question = :newQuestion, 
        hint = :newHint,
        answer = :newAnswer
        where cardId = :id""")
    suspend fun updateHintCard(id: Int, newQuestion: String, newHint: String, newAnswer: String)
}