package com.example.flashcards.model

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

// Setting up some of the queries so we can use them
// on our MainController
interface FlashCardDao {
    @Query("SELECT * FROM decks_table ORDER BY id ASC")
    fun getAllDecks(): Flow<List<Decks>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(decks: Decks)

    @Query("DELETE FROM decks_table")
    suspend fun deleteAll()

    @Transaction
    @Query("SELECT * FROM decks_table")
    fun getDeckWithCards(): List<DeckWithCards>
}