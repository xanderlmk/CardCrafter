package com.example.flashcards.model.migrations

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.UUID

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            // Add the uuid column to the decks table
            database.execSQL("ALTER TABLE decks ADD COLUMN uuid TEXT NOT NULL DEFAULT ''")

            // Add the deckUUID column to the cards table
            database.execSQL("ALTER TABLE cards ADD COLUMN deckUUID TEXT NOT NULL DEFAULT ''")

            // Add the reviewAmount column to the decks table
            database.execSQL("ALTER TABLE decks ADD COLUMN reviewAmount INTEGER NOT NULL DEFAULT 1")

            // Add the reviewsLeft column to the cards table
            database.execSQL("ALTER TABLE cards ADD COLUMN reviewsLeft INTEGER NOT NULL DEFAULT 1")

            // Generate UUIDs for existing decks
            val cursorDecks = database.query("SELECT id FROM decks")
            while (cursorDecks.moveToNext()) {
                val id = cursorDecks.getInt(cursorDecks.getColumnIndexOrThrow("id"))
                val uuid = UUID.randomUUID().toString()
                database.execSQL("UPDATE decks SET uuid = '$uuid' WHERE id = $id")
            }
            cursorDecks.close()

            // Generate deckUUIDs for existing cards
            val cursorCards = database.query("SELECT id, deckId FROM cards")
            while (cursorCards.moveToNext()) {
                val id = cursorCards.getInt(cursorCards.getColumnIndexOrThrow("id"))
                val deckId = cursorCards.getInt(cursorCards.getColumnIndexOrThrow("deckId"))

                // Get the deck's UUID
                val cursorDeck = database.query("SELECT uuid FROM decks WHERE id = $deckId")
                if (cursorDeck.moveToFirst()) {
                    val deckUUID = cursorDeck.getString(cursorDeck.getColumnIndexOrThrow("uuid"))
                    database.execSQL("UPDATE cards SET deckUUID = '$deckUUID' WHERE id = $id")
                }
                cursorDeck.close()
            }
            cursorCards.close()

            database.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 9 to 10 failed", e)
            throw RuntimeException("Migration 9 to 10 failed: ${e.message}")
        } finally {
            database.endTransaction()
        }
    }
}