package com.belmontCrest.cardCrafter.localDatabase.dbInterface.daoInterfaces.allCardTypesDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.belmontCrest.cardCrafter.localDatabase.tables.PartOfQorA
import com.belmontCrest.cardCrafter.localDatabase.tables.ThreeFieldCard

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
        answer = :newAnswer,
        field = :isQOrA
        where cardId = :id""")
    suspend fun updateThreeCard(
        id: Int,
        newQuestion: String,
        newMiddle: String,
        newAnswer: String,
        isQOrA : PartOfQorA
    )

}