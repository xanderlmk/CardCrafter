package com.belmontCrest.cardCrafter.model.migrations.m11_m16

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Adding a nextReview to decks
 * Adding a partOfList to cards and savedCards
 * */

val MIGRATION_13_14 = object : Migration(13, 14) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            db.execSQL("PRAGMA foreign_keys=ON;")
            // Add 'cardAmount' column to the 'decks' table
            db.execSQL("""
                ALTER TABLE decks 
                ADD COLUMN nextReview 
                INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}
                """)
            db.execSQL("""ALTER TABLE cards ADD COLUMN partOfList 
                INTEGER NOT NULL DEFAULT 0""")
            db.execSQL("""ALTER TABLE savedCards 
                ADD COLUMN partOfList INTEGER NOT NULL""")
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 13 to 14 failed", e)
            throw RuntimeException("Migration 13 to 14 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }
}