package com.belmontCrest.cardCrafter.model.migrations.m1_m10

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Adding a MultiChoiceCard
 * */
val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            db.execSQL("PRAGMA foreign_keys=ON;")

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS multiChoiceCard (
                    cardId INTEGER NOT NULL, 
                    question TEXT NOT NULL, 
                    choiceA TEXT NOT NULL,
                    choiceB TEXT NOT NULL,
                    choiceC TEXT NOT NULL,
                    choiceD TEXT NOT NULL,
                    correct INTEGER NOT NULL, 
                    PRIMARY KEY(cardId),
                    FOREIGN KEY(cardId) REFERENCES cards(id) ON DELETE CASCADE
                    )
            """
            )
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 7 to 8 failed", e)
            throw RuntimeException("Migration 7 to 8 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }
}