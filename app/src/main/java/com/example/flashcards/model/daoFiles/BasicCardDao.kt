package com.example.flashcards.model.daoFiles


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.BasicCardType
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface BasicCardDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBasicCard(basicCard: BasicCard) : Long

    @Transaction
    @Query("SELECT * FROM cards where id = :cardId ")
    fun getBasicCard(cardId :Int) : Flow<BasicCardType>

    @Query("DELETE FROM basicCard WHERE cardId = :cardId")
    suspend fun deleteBasicCard(cardId : Int)

    @Query("""
        Update basicCard set question = :newQuestion, 
        answer = :newAnswer where cardId = :id""")
    suspend fun updateBasicCard(id: Int, newQuestion: String, newAnswer: String)

    @Transaction
    @Query("SELECT * FROM cards where deckId = :deckId AND type = 'basic'")
    fun getAllBasicCards(deckId: Int): Flow<List<BasicCardType>>

    @Transaction
    @Query("""
        SELECT * FROM cards WHERE deckId = :deckId AND nextReview <= :currentTime
        AND type = 'basic'""")
    fun getDueBasicCards(deckId : Int, currentTime : Long = Date().time) : Flow<List<BasicCardType>>
}