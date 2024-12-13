package com.example.flashcards.model

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            // 1. Add the 'type' column to the 'cards' table.
            database.execSQL("ALTER TABLE cards ADD COLUMN type TEXT NOT NULL DEFAULT 'basic'")
            database.execSQL("DROP TABLE IF EXISTS basicCard;")
            database.execSQL("DROP TABLE IF EXISTS threeFieldCard;")
            database.execSQL("DROP TABLE IF EXISTS hintCard;")
            // 2. Create the new tables.
            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS basicCard (
                cardId INTEGER PRIMARY KEY,
                question TEXT NOT NULL,
                answer TEXT NOT NULL,
                 FOREIGN KEY(cardId) REFERENCES cards(id) ON DELETE CASCADE
            )
        """
            )
            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS threeFieldCard (
                cardId INTEGER PRIMARY KEY,
                question TEXT NOT NULL,
                middle TEXT NOT NULL,
                answer TEXT NOT NULL,
                 FOREIGN KEY(cardId) REFERENCES cards(id) ON DELETE CASCADE
            )
        """
            )

            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS hintCard (
                cardId INTEGER PRIMARY KEY,
                question TEXT NOT NULL,
                hint TEXT NOT NULL,
                answer TEXT NOT NULL,
                 FOREIGN KEY(cardId) REFERENCES cards(id) ON DELETE CASCADE
            )
        """
            )

            // 3. Migrate the 'question' and 'answer' from 'cards' to 'basicCard'.
            // Insert the data from the 'cards' table into 'basicCard'.
            database.execSQL(
                """
            INSERT INTO basicCard (cardId, question, answer)
            SELECT id, question, answer FROM cards
        """
            )

            database.execSQL("DROP TABLE IF EXISTS cards_new;")

            database.execSQL(
                """
            CREATE TABLE cards_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                deckId INTEGER NOT NULL,
                nextReview INTEGER,
                passes INTEGER NOT NULL,
                prevSuccess INTEGER NOT NULL,
                totalPasses INTEGER NOT NULL,
                type TEXT NOT NULL
            );
        """
            )

            database.execSQL(
                """
            INSERT INTO cards_new (id, deckId, nextReview, passes, prevSuccess, totalPasses, type)
            SELECT id, deckId, nextReview, passes, prevSuccess, totalPasses, type
            FROM cards;
        """
            )
            database.execSQL("DROP TABLE cards;")
            database.execSQL("ALTER TABLE cards_new RENAME TO cards;")
        } catch (e: Exception) {
            // Log the error for debugging
            throw RuntimeException("Migration 3 to 4 failed: ${e.message}")
        } finally {
            database.endTransaction()
        }
    }
}
