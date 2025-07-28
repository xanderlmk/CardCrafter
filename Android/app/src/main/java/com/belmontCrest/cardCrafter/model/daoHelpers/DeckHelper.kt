package com.belmontCrest.cardCrafter.model.daoHelpers

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckHelperDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertDeck(deck: Deck): Long

    @Query(
        """
    WITH RankedCards AS (
    SELECT
        c.deckId,
        COUNT(*) AS cardCount,
        d.cardsLeft
    FROM cards c
    INNER JOIN decks d ON c.deckId = d.id
    WHERE c.nextReview <= :currentTime
    AND d.nextReview <= :currentTime
    GROUP BY c.deckId, d.cardsLeft
    ORDER BY c.nextReview ASC 
    ) -- Collecting due cards pertaining to their deck
    SELECT 
    COALESCE(
        CASE 
            WHEN rc.cardCount >= d.cardsLeft THEN d.cardsLeft
            ELSE rc.cardCount
        END, 0
    ) -- if there is no cards, return 0
    FROM decks d
    LEFT JOIN RankedCards rc ON d.id = rc.deckId
    ORDER BY d.name ASC"""
    )
    fun getCCOrderedByNameAsc(currentTime: Long): Flow<List<Int>>

    @Query("SELECT * from decks ORDER BY name ASC")
    fun getDecksStreamOrderedByNameAsc(): Flow<List<Deck>>

    @Query(
        """
    WITH RankedCards AS (
    SELECT
        c.deckId,
        COUNT(*) AS cardCount,
        d.cardsLeft
    FROM cards c
    INNER JOIN decks d ON c.deckId = d.id
    WHERE c.nextReview <= :currentTime
    AND d.nextReview <= :currentTime
    GROUP BY c.deckId, d.cardsLeft
    ORDER BY c.nextReview ASC 
    ) -- Collecting due cards pertaining to their deck
    SELECT 
    COALESCE(
        CASE 
            WHEN rc.cardCount >= d.cardsLeft THEN d.cardsLeft
            ELSE rc.cardCount
        END, 0
    ) -- if there is no cards, return 0
    FROM decks d
    LEFT JOIN RankedCards rc ON d.id = rc.deckId
    ORDER BY d.name DESC"""
    )
    fun getCCOrderedByNameDesc(currentTime: Long): Flow<List<Int>>

    @Query("SELECT * from decks ORDER BY name DESC")
    fun getDecksStreamOrderedByNameDesc(): Flow<List<Deck>>

    @Query(
        """
    WITH RankedCards AS (
    SELECT
        c.deckId,
        COUNT(*) AS cardCount,
        d.cardsLeft
    FROM cards c
    INNER JOIN decks d ON c.deckId = d.id
    WHERE c.nextReview <= :currentTime
    AND d.nextReview <= :currentTime
    GROUP BY c.deckId, d.cardsLeft
    ORDER BY c.nextReview ASC 
    ) -- Collecting due cards pertaining to their deck
    SELECT 
    COALESCE(
        CASE 
            WHEN rc.cardCount >= d.cardsLeft THEN d.cardsLeft
            ELSE rc.cardCount
        END, 0
    ) -- if there is no cards, return 0
    FROM decks d
    LEFT JOIN RankedCards rc ON d.id = rc.deckId
    ORDER BY d.createdOn ASC"""
    )
    fun getCCOrderedByCreatedOnAsc(currentTime: Long): Flow<List<Int>>

    @Query("SELECT * from decks ORDER BY createdOn ASC")
    fun getDecksStreamOrderedByCreatedOnAsc(): Flow<List<Deck>>

    @Query(
        """
    WITH RankedCards AS (
    SELECT
        c.deckId,
        COUNT(*) AS cardCount,
        d.cardsLeft
    FROM cards c
    INNER JOIN decks d ON c.deckId = d.id
    WHERE c.nextReview <= :currentTime
    AND d.nextReview <= :currentTime
    GROUP BY c.deckId, d.cardsLeft
    ORDER BY c.nextReview ASC 
    ) -- Collecting due cards pertaining to their deck
    SELECT 
    COALESCE(
        CASE 
            WHEN rc.cardCount >= d.cardsLeft THEN d.cardsLeft
            ELSE rc.cardCount
        END, 0
    ) -- if there is no cards, return 0
    FROM decks d
    LEFT JOIN RankedCards rc ON d.id = rc.deckId
    ORDER BY d.createdOn DESC"""
    )
    fun getCCOrderedByCreatedOnDesc(currentTime: Long): Flow<List<Int>>

    @Query("SELECT * from decks ORDER BY createdOn DESC")
    fun getDecksStreamOrderedByCreatedOnDesc(): Flow<List<Deck>>

    @Query("""
    WITH RankedCards AS (
    SELECT
        c.deckId,
        COUNT(*) AS cardCount,
        d.cardsLeft
    FROM cards c
    INNER JOIN decks d ON c.deckId = d.id
    WHERE c.nextReview <= :currentTime
    AND d.nextReview <= :currentTime
    GROUP BY c.deckId, d.cardsLeft
    ORDER BY c.nextReview ASC 
    ) -- Collecting due cards pertaining to their deck
    SELECT 
    COALESCE(
        CASE 
            WHEN rc.cardCount >= d.cardsLeft THEN d.cardsLeft
            ELSE rc.cardCount
        END, 0
    ) -- if there is no cards, return 0
    FROM decks d
    LEFT JOIN RankedCards rc ON d.id = rc.deckId
    ORDER BY d.cardsLeft ASC"""
    )
    fun getCCOrderedByCardsLeftAsc(currentTime: Long): Flow<List<Int>>

    @Query("SELECT * from decks ORDER BY cardsLeft ASC")
    fun getDecksStreamOrderedByCardsLeftAsc(): Flow<List<Deck>>

    @Query("""
    WITH RankedCards AS (
    SELECT
        c.deckId,
        COUNT(*) AS cardCount,
        d.cardsLeft
    FROM cards c
    INNER JOIN decks d ON c.deckId = d.id
    WHERE c.nextReview <= :currentTime
    AND d.nextReview <= :currentTime
    GROUP BY c.deckId, d.cardsLeft
    ORDER BY c.nextReview ASC 
    ) -- Collecting due cards pertaining to their deck
    SELECT 
    COALESCE(
        CASE 
            WHEN rc.cardCount >= d.cardsLeft THEN d.cardsLeft
            ELSE rc.cardCount
        END, 0
    ) -- if there is no cards, return 0
    FROM decks d
    LEFT JOIN RankedCards rc ON d.id = rc.deckId
    ORDER BY d.cardsLeft DESC"""
    )
    fun getCCOrderedByCardsLefDesc(currentTime: Long): Flow<List<Int>>

    @Query("SELECT * from decks ORDER BY cardsLeft DESC")
    fun getDecksStreamOrderedByCardsLeftDesc(): Flow<List<Deck>>
}

sealed class OrderBy {
    data object NameASC : OrderBy()

    data object NameDESC : OrderBy()

    data object CreatedOnASC : OrderBy()

    data object CreatedOnDESC : OrderBy()

    data object CardsLeftASC : OrderBy()

    data object CardsLeftDESC : OrderBy()
}

sealed class Order {
    data object Name : Order()
    data object CreatedOn : Order()
    data object CardsLeft : Order()
}

fun Order.toOrderedByClass(asc: Boolean): OrderBy = when (this) {
    is Order.Name -> if (asc) OrderBy.NameASC else OrderBy.NameDESC
    is Order.CreatedOn -> if (asc) OrderBy.CreatedOnASC else OrderBy.CreatedOnDESC
    is Order.CardsLeft -> if (asc) OrderBy.CardsLeftASC else OrderBy.CardsLeftDESC
}

fun OrderBy.reverseSort(): OrderBy = when (this) {
    OrderBy.CreatedOnASC -> OrderBy.CreatedOnDESC
    OrderBy.CreatedOnDESC -> OrderBy.CreatedOnASC
    OrderBy.NameASC -> OrderBy.NameDESC
    OrderBy.NameDESC -> OrderBy.NameASC
    OrderBy.CardsLeftASC -> OrderBy.CardsLeftDESC
    OrderBy.CardsLeftDESC -> OrderBy.CardsLeftASC
}

fun OrderBy.toOrderedString(): String = when (this) {
    OrderBy.CreatedOnASC -> OBS.CREATED_ON_ASC
    OrderBy.CreatedOnDESC -> OBS.CREATED_ON_DESC
    OrderBy.NameASC -> OBS.NAME_ASC
    OrderBy.NameDESC -> OBS.NAME_DESC
    OrderBy.CardsLeftASC -> OBS.CARDS_LEFT_ASC
    OrderBy.CardsLeftDESC -> OBS.CARDS_LEFT_DESC
}

fun String.toOrderedByClass(): OrderBy = when (this) {
    OBS.NAME_DESC -> OrderBy.NameDESC
    OBS.CREATED_ON_ASC -> OrderBy.CreatedOnASC
    OBS.CREATED_ON_DESC -> OrderBy.CreatedOnDESC
    OBS.CARDS_LEFT_ASC -> OrderBy.CardsLeftASC
    OBS.CARDS_LEFT_DESC -> OrderBy.CardsLeftDESC
    else -> OrderBy.NameASC
}

object OBS {
    const val CREATED_ON_ASC = "created_on_asc"
    const val CREATED_ON_DESC = "created_on_desc"
    const val NAME_ASC = "name_asc"
    const val NAME_DESC = "name_desc"
    const val CARDS_LEFT_ASC = "cards_left_asc"
    const val CARDS_LEFT_DESC = "cards_left_desc"
}