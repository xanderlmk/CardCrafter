package com.belmontCrest.cardCrafter.model.migrations.m1_m10

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Updated Multipliers
 * */

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            db.execSQL("PRAGMA foreign_keys=ON;")
            db.execSQL("ALTER TABLE decks ADD COLUMN badMultiplier DOUBLE NOT NULL DEFAULT 0.5")
            db.execSQL("ALTER TABLE decks RENAME COLUMN multiplier TO goodMultiplier")
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 5 to 6 failed", e)
            throw RuntimeException("Migration 5 to 6 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }
}