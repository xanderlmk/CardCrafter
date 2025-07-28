package com.belmontCrest.cardCrafter.localDatabase.dbInterface.daoInterfaces.deckAndCardDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.model.daoHelpers.DeckHelperDao
import com.belmontCrest.cardCrafter.model.ui.states.DueDeckDetails
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.Date


data class DeckId(
    val id: Int
)

@Dao
interface DeckDao : DeckHelperDao {

    @Update
    suspend fun updateDeck(deck: Deck)

    @Delete
    suspend fun deleteDeck(deck: Deck)

    @Delete(entity = Deck::class)
    suspend fun deleteDeckById(vararg id: DeckId)

    @Query("SELECT * from decks WHERE id = :id")
    fun getDeckFlow(id: Int): Flow<Deck>

    @Query("SELECT * from decks where id = :id")
    fun getDeck(id: Int): Deck

    @Query("SELECT * from decks ORDER BY name ASC")
    suspend fun getAllDecks(): List<Deck>

    @Query("SELECT name from decks where id = :id")
    fun getDeckName(id: Int) : Flow<String?>

    @Query(
        """
    WITH RankedCards AS (
    SELECT
        c.deckId,
        COUNT(*) AS cardCount,
        d.cardAmount,
        d.cardsLeft
    FROM cards c
    INNER JOIN decks d ON c.deckId = d.id
    WHERE c.nextReview <= :currentTime 
      AND d.nextReview <= :currentTime
      AND d.lastUpdated != :startOfDay
    GROUP BY c.deckId, d.cardAmount, d.cardsLeft
    ORDER BY c.nextReview ASC 
    )
    UPDATE decks
    SET cardsLeft = (
        SELECT 
            CASE 
                WHEN rc.cardCount IS NULL THEN 0
                WHEN rc.cardCount > decks.cardAmount THEN decks.cardAmount
                ELSE rc.cardCount
        END
    FROM RankedCards rc
    WHERE rc.deckId = decks.id
    ),
    cardsDone = 0,
    lastUpdated = :startOfDay
    WHERE EXISTS (
    SELECT 1
    FROM RankedCards rc
    WHERE rc.deckId = decks.id
    )
    AND NOT EXISTS (
        SELECT 1 
        FROM cards c
        WHERE c.deckId = decks.id
        AND c.partOfList = 1                    -- Making sure there's no cards in a list.
        AND decks.lastUpdated == :startOfDay    -- Making sure it's only updated once a day.
        )
    """
    )
    fun resetCardLefts(
        currentTime: Long = Date().time,
        startOfDay: Long = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    )

    @Query("SELECT COUNT(*) FROM decks WHERE LOWER(name) = LOWER(:deckName)")
    fun checkIfDeckExists(deckName: String): Int

    @Query(
        """
        SELECT COUNT(*) 
        FROM decks WHERE LOWER(name) = LOWER(:deckName) 
        OR uuid = :deckUUID
        """
    )
    fun checkIfDeckExists(deckName: String, deckUUID: String): Int

    @Query(
        """
        SELECT COUNT(*) 
        FROM decks WHERE uuid = :deckUUID
    """
    )
    fun checkIfDeckUUIDExists(deckUUID: String): Int

    @Query(
        """
        UPDATE decks 
        SET name = :newName 
        WHERE id = :deckId
        AND NOT EXISTS (
            SELECT 1 
            FROM decks 
            WHERE LOWER(name) = LOWER(:newName) 
            AND id != :deckId
        )
    """
    )
    fun updateDeckName(newName: String, deckId: Int): Int

    @Query(
        """
        update decks
        set goodMultiplier = :newMultiplier
        where id = :deckId
        and :newMultiplier > 1.0
    """
    )
    fun updateDeckGoodMultiplier(newMultiplier: Double, deckId: Int): Int

    @Query(
        """
        update decks
        set badMultiplier = :newMultiplier
        where id = :deckId
        and :newMultiplier < 1.0
        and :newMultiplier > 0.0
    """
    )
    fun updateDeckBadMultiplier(newMultiplier: Double, deckId: Int): Int

    @Query(
        """
        update decks
        set reviewAmount = :newReviewAmount
        where id = :deckId
    """
    )
    fun updateReviewAmount(newReviewAmount: Int, deckId: Int): Int

    @Query(
        """
        update decks
        set nextReview = :nextReview
        where id = :deckId
    """
    )
    fun updateNextReview(nextReview: Date, deckId: Int)

    @Query(
        """ 
        UPDATE decks 
        SET cardsLeft = :cardsLeft,
        cardsDone = :cardsDone
        WHERE id = :deckId
    """
    )
    fun updateCardsLeft(deckId: Int, cardsLeft: Int, cardsDone: Int)

    @Query(
        """
        WITH CurrentCards AS (
            SELECT COUNT(*) as cardCount, deckId FROM cards 
            WHERE deckId = :deckId 
            AND nextReview <= :currentTime 
            ORDER BY partOfList DESC, nextReview ASC
            LIMIT :cardAmount
        )
        UPDATE decks
        SET cardAmount = :cardAmount,
        cardsLeft =  (
            SELECT
                CASE
                    WHEN nextReview > :currentTime THEN 0
                    WHEN CC.cardCount IS NULL THEN 0
                    WHEN CC.cardCount >= :cardAmount then :cardAmount
                    ELSE CC.cardCount
                END 
            FROM CurrentCards CC
            WHERE CC.deckId = :deckId
        )
        WHERE id = :deckId 
        AND cardAmount > 4
        AND cardAmount < 1001
    """
    )
    fun updateCardAmount(cardAmount: Int, deckId: Int, currentTime: Long = Date().time): Int

    @Query(
        """
        SELECT id, cardsLeft, cardAmount, reviewAmount, nextReview
        FROM decks WHERE id = :id
        """
    )
    fun getDueDeckDetails(id: Int): Flow<DueDeckDetails?>
}