package com.example.flashcards.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Update
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

// Setting up some of the queries so we can use them
// on our MainController
@Dao
interface DeckDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(decks: Decks)

    @Update
    suspend fun update(decks: Decks)

    @Delete
    suspend fun delete(decks: Decks)

    @Query("SELECT * from decks WHERE id = :id")
    fun getDeck(id: Int): Flow<Decks>

    @Query("SELECT * from decks ORDER BY name ASC")
    fun getAllDecks(): Flow<List<Decks>>
}