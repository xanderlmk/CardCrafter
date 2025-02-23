package com.example.flashcards.model.daoFiles.deckAndCardDao

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
        AND nextReview <= :currentTime 
        ORDER BY partOfList DESC, nextReview ASC
        LIMIT :cardAmount"""
    )
    fun getDueAllCardTypes(deckId: Int, cardAmount: Int, currentTime: Long = Date().time):
            Flow<List<AllCardTypes>>

    @Transaction
    @Query(
        """SELECT * FROM cards WHERE deckId = :deckId
        ORDER BY cards.id"""
    )
    fun getAllCardTypes(deckId: Int): Flow<List<AllCardTypes>>

    @Transaction
    @Query("""SELECT * FROM cards where id = :id""")
    fun getACardType(id: Int): AllCardTypes
}