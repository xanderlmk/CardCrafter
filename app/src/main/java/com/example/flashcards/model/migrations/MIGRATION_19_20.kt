package com.example.flashcards.model.migrations

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_19_20 = object : Migration(19, 20) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            database.execSQL("PRAGMA foreign_keys=OFF;")
            // Drop index first.
            database.execSQL("DROP INDEX IF EXISTS index_mathCard_cardId;")
            // rename
            database.execSQL("ALTER TABLE mathCard RENAME TO notationCard;")
            // create new index.
            database.execSQL("CREATE INDEX index_notationCard_cardId ON notationCard (cardId)")
            // rename all the card.type to notation.
            database.execSQL(
                """
                UPDATE cards 
                SET type = 'notation' 
                WHERE type = 'math';
                """
            )
            database.execSQL("PRAGMA foreign_keys=ON;")
            database.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 19 to 20 failed", e)
            throw RuntimeException("Migration 19 to 20 failed: ${e.message}")
        } finally {
            database.endTransaction()
        }
    }
}