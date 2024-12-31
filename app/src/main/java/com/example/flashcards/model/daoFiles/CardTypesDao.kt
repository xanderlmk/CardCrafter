package com.example.flashcards.model.daoFiles

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.flashcards.model.tablesAndApplication.AllCardTypes
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface CardTypesDao{
    @Transaction
    @Query("""SELECT * FROM cards WHERE deckId = :deckId 
        AND nextReview <= :currentTime ORDER BY cards.id""")
    fun getDueAllCardTypes(deckId : Int, currentTime : Long = Date().time) :
            Flow<List<AllCardTypes>>

    @Transaction
    @Query("""SELECT * FROM cards WHERE deckId = :deckId
        ORDER BY cards.id""")
    fun getAllCardTypes(deckId : Int) :
            Flow<List<AllCardTypes>>
}