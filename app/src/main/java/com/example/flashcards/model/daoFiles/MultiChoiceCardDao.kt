package com.example.flashcards.model.daoFiles

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCard
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCardType
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface MultiChoiceCardDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMultiChoiceCard(multiChoiceCard: MultiChoiceCard) : Long

    @Transaction
    @Query("SELECT * FROM cards where id = :cardId ")
    fun getMultiChoiceCard(cardId :Int) : Flow<MultiChoiceCardType>

    @Query("DELETE FROM multiChoiceCard WHERE cardId = :cardId")
    suspend fun deleteMultiChoiceCard(cardId : Int)

    @Query("""
        Update multiChoiceCard set question = :newQuestion, 
        choiceA = :newChoiceA, 
        choiceB = :newChoiceB,
        choiceC = :newChoiceC,
        choiceD = :newChoiceD,
        correct = :newCorrect
        where cardId = :id""")
    suspend fun updateMultiChoiceCard(
        id: Int,
        newQuestion: String,
        newChoiceA: String,
        newChoiceB: String,
        newChoiceC: String,
        newChoiceD: String,
        newCorrect: Char
    )

    @Transaction
    @Query("SELECT * FROM cards where deckId = :deckId")
    fun getAllMultiChoiceCards(deckId: Int): Flow<List<MultiChoiceCardType>>

    @Transaction
    @Query("SELECT * FROM cards WHERE deckId = :deckId AND nextReview <= :currentTime")
    fun getDueMultiChoiceCards(
        deckId : Int,
        currentTime : Long = Date().time
    ) : Flow<List<MultiChoiceCardType>>
}