package com.example.flashcards.model.migrations

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_13_14 = object : Migration(13, 14) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            database.execSQL("PRAGMA foreign_keys=ON;")
            // Add 'cardAmount' column to the 'decks' table
            database.execSQL("""
                ALTER TABLE decks 
                ADD COLUMN nextReview 
                INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}
                """)
            database.execSQL("""ALTER TABLE cards ADD COLUMN partOfList 
                INTEGER NOT NULL DEFAULT 0""")
            database.execSQL("""ALTER TABLE savedCards 
                ADD COLUMN partOfList INTERGER NOT NULL""")
            database.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 13 to 14 failed", e)
            throw RuntimeException("Migration 13 to 14 failed: ${e.message}")
        } finally {
            database.endTransaction()
        }
    }
}