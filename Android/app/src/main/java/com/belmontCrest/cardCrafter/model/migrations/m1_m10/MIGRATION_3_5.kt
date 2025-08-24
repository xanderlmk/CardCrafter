package com.belmontCrest.cardCrafter.model.migrations.m1_m10

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Adding a Multiplier
 * */
val MIGRATION_3_5 = object : Migration(3, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            db.execSQL("PRAGMA foreign_keys=ON;")
            db.execSQL("ALTER TABLE decks ADD COLUMN multiplier DOUBLE NOT NULL DEFAULT 1.5")
            db.execSQL(
                """
            CREATE TABLE cards_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                deckId INTEGER NOT NULL,
                nextReview INTEGER,
                passes INTEGER NOT NULL,
                prevSuccess INTEGER NOT NULL,
                totalPasses INTEGER NOT NULL,
                type TEXT NOT NULL,
                FOREIGN KEY(deckId) REFERENCES decks(id)
            )
            """
            )
            db.execSQL(
                "INSERT INTO cards_new (id, deckId, passes, prevSuccess, totalPasses, type, nextReview) " +
                        "SELECT id, deckId, passes, prevSuccess, totalPasses, type, nextReview FROM cards"
            )
            db.execSQL("DROP TABLE cards")
            db.execSQL("ALTER TABLE cards_new RENAME TO cards")
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 4 to 5 failed", e)
            throw RuntimeException("Migration 4 to 5 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }
}