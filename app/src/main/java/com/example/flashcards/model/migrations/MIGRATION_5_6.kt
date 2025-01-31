package com.example.flashcards.model.migrations

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Updated Multipliers
 * */

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            database.execSQL("PRAGMA foreign_keys=ON;")
            database.execSQL("ALTER TABLE decks ADD COLUMN badMultiplier DOUBLE NOT NULL DEFAULT 0.5")
            database.execSQL("ALTER TABLE decks RENAME COLUMN multiplier TO goodMultiplier")
            database.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 5 to 6 failed", e)
            throw RuntimeException("Migration 5 to 6 failed: ${e.message}")
        } finally {
            database.endTransaction()
        }
    }
}