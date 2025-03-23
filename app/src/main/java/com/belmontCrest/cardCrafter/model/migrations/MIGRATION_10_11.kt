package com.belmontCrest.cardCrafter.model.migrations

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Creating a savedCards table
 * */
val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS savedCards (
                id INTEGER NOT NULL, 
                reviewsLeft INTEGER NOT NULL, 
                nextReview INTEGER NOT NULL, 
                passes INTEGER NOT NULL, 
                prevSuccess INTEGER NOT NULL, 
                totalPasses INTEGER NOT NULL, 
                PRIMARY KEY(id)
            )
            """
            )


            database.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 10 to 11 failed", e)
            throw RuntimeException("Migration 10 to 11 failed: ${e.message}")
        } finally {
            database.endTransaction()
        }
    }
}