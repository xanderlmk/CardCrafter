package com.example.flashcards.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Update
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.Date

// Setting up some of the queries so we can use them
// on our MainController
@Dao
interface DeckDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertDeck(deck: Deck)

    @Update
    suspend fun updateDeck(deck: Deck)

    @Delete
    suspend fun deleteDeck(deck: Deck)

    @Query("SELECT * from decks WHERE id = :id")
    fun getDeck(id: Int): Flow<Deck>

    @Query("SELECT * from decks ORDER BY name ASC")
    fun getAllDecks(): Flow<List<Deck>>
}
@Dao
interface CardDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCard(card: Card)

    @Update
    suspend fun updateCard(card: Card)

    @Delete
    suspend fun deleteCard(card: Card)

    @Transaction
    @Query("SELECT * FROM decks WHERE id = :deckId")
    fun getDeckWithCards(deckId: Int): Flow<DeckWithCards>

    @Query("SELECT * FROM cards WHERE deckId = :deckId AND nextReview <= :currentTime")
    fun getDueCards(deckId: Int, currentTime: Long = Date().time): Flow<List<Card>>

    @Query("DELETE FROM cards WHERE deckId = :deckId")
    suspend fun deleteAllCards(deckId: Int)
}