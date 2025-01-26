package com.example.flashcards.model.migrations

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_12_13 = object : Migration(12, 13) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            database.execSQL("PRAGMA foreign_keys=ON;")
            // Add 'cardAmount' column to the 'decks' table
            database.execSQL("""
                ALTER TABLE decks
                ADD COLUMN cardAmount 
                INTEGER NOT NULL DEFAULT 20
                """)
            database.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 12 to 13 failed", e)
            throw RuntimeException("Migration 12 to 13 failed: ${e.message}")
        } finally {
            database.endTransaction()
        }
    }
}

