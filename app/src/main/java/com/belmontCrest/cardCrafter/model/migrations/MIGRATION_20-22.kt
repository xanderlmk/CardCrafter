package com.belmontCrest.cardCrafter.model.migrations

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_20_21 = object : Migration(20, 21) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            db.execSQL(
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
            db.execSQL(
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
            db.execSQL(
                """
            DROP TABLE cards;
            """
            )
            db.execSQL(
                """
            ALTER TABLE cards_temp RENAME TO cards;
"""
            )
            db.execSQL(
                """
            CREATE UNIQUE INDEX IF NOT EXISTS index_cards_deckUUID_deckCardNumber
            ON cards (deckUUID, deckCardNumber);
            """.trimIndent()
            )

            db.execSQL("""
                CREATE INDEX IF NOT EXISTS index_cards_deckId 
                ON cards (deckId);
            """.trimIndent())

            db.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 20 to 21 failed", e)
            throw RuntimeException("Migration 20 to 21 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }

}

val MIGRATION_21_22 = object : Migration(21,22) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            db.execSQL("""
                CREATE UNIQUE INDEX IF NOT EXISTS index_decks_uuid
                ON decks(uuid)
            """.trimIndent())
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 21 to 22 failed", e)
            throw RuntimeException("Migration 21 to 22 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }
}