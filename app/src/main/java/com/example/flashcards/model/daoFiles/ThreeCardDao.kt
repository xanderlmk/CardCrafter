package com.example.flashcards.model.daoFiles

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.flashcards.model.tablesAndApplication.ThreeCardType
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ThreeCardDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertThreeCard(threeFieldCard: ThreeFieldCard) : Long

    @Transaction
    @Query("SELECT * FROM cards where id = :cardId ")
    fun getThreeCard(cardId :Int) : Flow<ThreeCardType>

    @Query("DELETE FROM threeFieldCard WHERE cardId = :cardId")
    suspend fun deleteThreeCard(cardId: Int)

    @Transaction
    @Query("SELECT * FROM cards where deckId = :deckId AND type = 'three'")
    fun getAllThreeCards(deckId: Int): Flow<List<ThreeCardType>>

    @Transaction
    @Query("""SELECT * FROM cards WHERE deckId = :deckId 
        AND nextReview <= :currentTime AND type = 'three'""")
    fun getDueThreeCards(
        deckId : Int,
        currentTime : Long = Date().time
    ) : Flow<List<ThreeCardType>>
    @Query("""
        Update threeFieldCard
        Set question = :newQuestion, 
        middle = :newMiddle,
        answer = :newAnswer
        where cardId = :id""")
    suspend fun updateThreeCard(
        id: Int,
        newQuestion: String,
        newMiddle: String,
        newAnswer: String
    )

}