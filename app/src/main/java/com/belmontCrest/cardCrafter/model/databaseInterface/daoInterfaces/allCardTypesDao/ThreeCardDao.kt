package com.belmontCrest.cardCrafter.model.databaseInterface.daoInterfaces.allCardTypesDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.belmontCrest.cardCrafter.model.tablesAndApplication.ThreeFieldCard

@Dao
interface ThreeCardDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertThreeCard(threeFieldCard: ThreeFieldCard) : Long

    @Delete
    suspend fun deleteThreeCard(threeFieldCard: ThreeFieldCard)

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