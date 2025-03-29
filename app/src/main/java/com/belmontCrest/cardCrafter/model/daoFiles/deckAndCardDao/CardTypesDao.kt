package com.belmontCrest.cardCrafter.model.daoFiles.deckAndCardDao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.belmontCrest.cardCrafter.model.tablesAndApplication.AllCardTypes
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface CardTypesDao {
    @Transaction
    @Query(
        """SELECT * FROM cards WHERE deckId = :deckId 
        AND nextReview <= :currentTime 
        ORDER BY nextReview ASC, partOfList DESC, reviewsLeft DESC
        LIMIT :cardAmount"""
    )
    fun getDueAllCardTypesFlow(deckId: Int, cardAmount: Int, currentTime: Long):
            Flow<List<AllCardTypes>>

    @Transaction
    @Query(
        """SELECT * FROM cards WHERE deckId = :deckId 
        AND nextReview <= :currentTime AND reviewsLeft >= 1
        ORDER BY nextReview ASC, partOfList DESC, reviewsLeft DESC
        LIMIT :cardAmount"""
    )
    fun getDueAllCardTypes(deckId: Int, cardAmount: Int, currentTime: Long):
            List<AllCardTypes>

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