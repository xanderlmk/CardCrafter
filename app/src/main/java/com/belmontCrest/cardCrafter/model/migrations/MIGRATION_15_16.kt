package com.belmontCrest.cardCrafter.model.migrations

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Adding a cardsLeft column to deck
 * */

val MIGRATION_15_16 = object : Migration(15, 16) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            database.execSQL("PRAGMA foreign_keys=ON;")
            // Add 'cardLeft' column to the 'decks' table
            database.execSQL("""
                ALTER TABLE decks 
                ADD COLUMN cardsLeft 
                INTEGER NOT NULL DEFAULT 0
                """)
            database.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 14 to 15 failed", e)
            throw RuntimeException("Migration 14 to 15 failed: ${e.message}")
        } finally {
            database.endTransaction()
        }
    }
}