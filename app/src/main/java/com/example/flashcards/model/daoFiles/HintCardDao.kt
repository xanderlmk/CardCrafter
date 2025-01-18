package com.example.flashcards.model.daoFiles

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.HintCardType
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface HintCardDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertHintCard(hintCard: HintCard) : Long

    @Query("DELETE FROM hintCard WHERE cardId = :cardId")
    suspend fun deleteHintCard(cardId: Int)

    @Transaction
    @Query("SELECT * FROM cards where id = :cardId")
    fun getHintCard(cardId :Int) : Flow<HintCardType>

    @Transaction
    @Query("SELECT * from cards where deckId = :deckId AND type = 'hint'")
    fun getAllHintCards(deckId: Int): Flow<List<HintCardType>>

    @Transaction
    @Query("""
        SELECT * FROM cards WHERE deckId = :deckId
    AND nextReview <= :currentTime AND type = 'hint'""")
    fun getDueHintCards(
        deckId : Int,
        currentTime : Long = Date().time
    ) : Flow<List<HintCardType>>

    @Query("""
        Update hintCard
        Set question = :newQuestion, 
        hint = :newHint,
        answer = :newAnswer
        where cardId = :id""")
    suspend fun updateHintCard(
        id: Int,
        newQuestion: String,
        newHint: String,
        newAnswer: String
    )
}