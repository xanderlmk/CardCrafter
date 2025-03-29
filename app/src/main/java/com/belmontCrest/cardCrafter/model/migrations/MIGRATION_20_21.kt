package com.belmontCrest.cardCrafter.model.migrations

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_20_21 = object : Migration(20, 21) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            database.execSQL(
                """
    CREATE TABLE cards_temp (
        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
        deckId INTEGER NOT NULL,
        deckUUID TEXT NOT NULL,
        reviewsLeft INTEGER NOT NULL,
        nextReview INTEGER NOT NULL,
        passes INTEGER NOT NULL,
        prevSuccess INTEGER NOT NULL,
        totalPasses INTEGER NOT NULL,
        type TEXT NOT NULL,
        createdOn INTEGER NOT NULL,
        partOfList INTEGER NOT NULL,
        deckCardNumber INTEGER,
        cardIdentifier TEXT NOT NULL,
        FOREIGN KEY(deckId) REFERENCES decks(id)
    );"""
            )
            database.execSQL(
                """
                INSERT INTO cards_temp (
                id, deckId, deckUUID, reviewsLeft, nextReview, passes, prevSuccess,
                totalPasses, type, createdOn, partOfList, deckCardNumber, cardIdentifier
                ) SELECT 
                    id,
                    deckId,
                    deckUUID,
                    reviewsLeft,
                    nextReview,
                    passes,
                    prevSuccess,
                    totalPasses,
                    type,
                    createdOn,
                    partOfList,
                    row_number() OVER (PARTITION BY deckUUID ORDER BY createdOn, id) - 1 AS deckCardNumber,
                    deckUUID || '-' || (row_number() OVER (PARTITION BY deckUUID ORDER BY createdOn, id) - 1) AS cardIdentifier   
                    FROM cards;
            """.trimIndent()
            )
            database.execSQL(
                """
            DROP TABLE cards;
            """
            )
            database.execSQL(
                """
            ALTER TABLE cards_temp RENAME TO cards;
"""
            )
            database.execSQL(
                """
            CREATE UNIQUE INDEX IF NOT EXISTS index_cards_deckUUID_deckCardNumber
            ON cards (deckUUID, deckCardNumber);
            """.trimIndent()
            )

            database.execSQL("""
                CREATE INDEX IF NOT EXISTS index_cards_deckId 
                ON cards (deckId);
            """.trimIndent())

            database.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 19 to 20 failed", e)
            throw RuntimeException("Migration 19 to 20 failed: ${e.message}")
        } finally {
            database.endTransaction()
        }
    }

}
/**
 * Expected:
 * TableInfo{name='cards', columns={
 * indices=[Index{name='index_cards_deckId', unique=false, columns=[deckId], orders=[ASC]'},
 *      Index{name='index_cards_deckUUID_deckCardNumber', unique=true, columns=[deckUUID, deckCardNumber], orders=[ASC, ASC]'}]}
 * Found:
 * TableInfo{name='cards', columns={
 * indices=[Index{name='index_cards_deckUUID_deckCardNumber', unique=true, columns=[deckUUID, deckCardNumber], orders=[ASC, ASC]'}]} (Ask Gemini)
 *
 */