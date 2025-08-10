package com.belmontCrest.cardCrafter.model.migrations.m1_m10

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Adding a createdOn
 * */

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            db.execSQL("PRAGMA foreign_keys=ON;")

            // Add 'createdOn' column to the 'decks' table
            db.execSQL("""ALTER TABLE decks ADD COLUMN createdOn 
                INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}""")

            // Add 'createdOn' column to the 'cards' table
            db.execSQL("""ALTER TABLE cards ADD COLUMN createdOn 
                INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}""")
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 6 to 7 failed", e)
            throw RuntimeException("Migration 6 to 7 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }
}