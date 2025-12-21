package com.belmontCrest.cardCrafter.model.migrations.m16_m20

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Adding a lastUpdated Column to decks
 * */

val MIGRATION_16_17 = object : Migration(16, 17)  {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            db.execSQL("PRAGMA foreign_keys=OFF;")
            // Add 'createdOn' column to the 'decks' table
            db.execSQL("""ALTER TABLE decks ADD COLUMN lastUpdated 
                INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}""")

            db.execSQL("PRAGMA foreign_keys=ON;")
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 16 to 17 failed", e)
            throw RuntimeException("Migration 16 to 17 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }
}