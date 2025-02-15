package com.example.flashcards.model.daoFiles.allCardTypesDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCard

@Dao
interface MultiChoiceCardDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMultiChoiceCard(multiChoiceCard: MultiChoiceCard) : Long

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
}