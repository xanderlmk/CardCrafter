package com.belmontCrest.cardCrafter.localDatabase.dbInterface.daoInterfaces.allCardTypesDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.belmontCrest.cardCrafter.localDatabase.tables.NotationCard

@Dao
interface NotationCardDao {

    @Delete
    suspend fun deleteNotationCard(notationCard: NotationCard)

    @Query("""
        UPDATE notationCard
        set question = :question,
        steps = :steps,
        answer = :answer
        where cardId = :cardId
        """)
    fun updateNotationCard(
        question: String, steps: String,
        answer: String, cardId: Int
    )
}