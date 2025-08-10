package com.belmontCrest.cardCrafter.model.migrations.m1_m10

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Creating an Index for cards on deckId
 * */
val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            db.execSQL("PRAGMA foreign_keys=ON;")
            db.execSQL("CREATE INDEX index_cards_deckId ON cards (deckId)")
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 8 to 9 failed", e)
            throw RuntimeException("Migration 8 to 9 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }
}