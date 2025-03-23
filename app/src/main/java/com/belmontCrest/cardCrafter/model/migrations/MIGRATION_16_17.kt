package com.belmontCrest.cardCrafter.model.migrations

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Adding a lastUpdated Column to decks
 * */

val MIGRATION_16_17 = object : Migration(16, 17)  {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            database.execSQL("PRAGMA foreign_keys=OFF;")
            // Add 'createdOn' column to the 'decks' table
            database.execSQL("""ALTER TABLE decks ADD COLUMN lastUpdated 
                INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}""")

            database.execSQL("PRAGMA foreign_keys=ON;")
            database.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 16 to 17 failed", e)
            throw RuntimeException("Migration 16 to 17 failed: ${e.message}")
        } finally {
            database.endTransaction()
        }
    }
}