package com.example.flashcards.model.daoFiles

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.flashcards.model.tablesAndApplication.AllCardTypes
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface CardTypesDao {
    @Transaction
    @Query(
        """SELECT * FROM cards WHERE deckId = :deckId 
        AND nextReview <= :currentTime AND NOT EXISTS (
        SELECT 1 
        FROM cards c
        WHERE c.partOfList = 1 AND nextReview <= :currentTime AND c.deckId = :deckId
    ) ORDER BY cards.nextReview ASC LIMIT :cardAmount"""
    )
    fun getDueAllCardTypes(deckId: Int, cardAmount: Int, currentTime: Long = Date().time):
            Flow<List<AllCardTypes>>

    @Transaction
    @Query(
        """SELECT * FROM cards WHERE deckId = :deckId 
        AND nextReview <= :currentTime 
        AND partOfList = 1 ORDER BY cards.nextReview ASC """
    )
    fun getCurrentDueAllCardTypes(
        deckId: Int,
        currentTime: Long = Date().time
    ): Flow<List<AllCardTypes>>

    @Transaction
    @Query(
        """SELECT * FROM cards WHERE deckId = :deckId
        ORDER BY cards.nextReview"""
    )
    fun getAllCardTypes(deckId: Int):
            Flow<List<AllCardTypes>>

    @Transaction
    @Query("""SELECT * FROM cards where id = :id""")
    fun getACardType(id: Int):
            AllCardTypes
}