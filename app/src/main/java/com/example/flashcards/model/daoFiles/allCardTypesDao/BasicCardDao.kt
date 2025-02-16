package com.example.flashcards.model.daoFiles.allCardTypesDao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flashcards.model.tablesAndApplication.BasicCard

@Dao
interface BasicCardDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBasicCard(basicCard: BasicCard) : Long

    @Delete
    suspend fun deleteBasicCard(basicCard: BasicCard)

    @Query("""
        Update basicCard set question = :newQuestion, 
        answer = :newAnswer where cardId = :id""")
    suspend fun updateBasicCard(id: Int, newQuestion: String, newAnswer: String)
}