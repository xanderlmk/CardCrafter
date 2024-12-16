package com.example.flashcards.model

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_4_5 = object : Migration(3, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            database.execSQL("PRAGMA foreign_keys=ON;")
            database.execSQL("ALTER TABLE decks ADD COLUMN multiplier DOUBLE NOT NULL DEFAULT 1.5")
            database.execSQL(
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
            database.execSQL(
                "INSERT INTO cards_new (id, deckId, passes, prevSuccess, totalPasses, type, nextReview) " +
                        "SELECT id, deckId, passes, prevSuccess, totalPasses, type, nextReview FROM cards"
            )
            database.execSQL("DROP TABLE cards")
            database.execSQL("ALTER TABLE cards_new RENAME TO cards")
        } catch (e: Exception) {
            // Log the error for debugging
            throw RuntimeException("Migration 4 to 5 failed: ${e.message}")
        } finally {
            database.endTransaction()
        }
    }
}
