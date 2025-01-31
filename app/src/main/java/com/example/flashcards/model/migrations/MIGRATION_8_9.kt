package com.example.flashcards.model.migrations

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Creating an Index for cards on deckId
 * */
val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            database.execSQL("PRAGMA foreign_keys=ON;")
            database.execSQL("CREATE INDEX index_cards_deckId ON cards (deckId)")
            database.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 8 to 9 failed", e)
            throw RuntimeException("Migration 8 to 9 failed: ${e.message}")
        } finally {
            database.endTransaction()
        }
    }
}