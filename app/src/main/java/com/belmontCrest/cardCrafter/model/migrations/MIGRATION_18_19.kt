package com.belmontCrest.cardCrafter.model.migrations

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Fixing the addition of the MathCard to the
 *  Database, it worked (:
 */
val MIGRATION_18_19 = object : Migration(18, 19) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            database.execSQL("PRAGMA foreign_keys=OFF;")
            database.execSQL("""
                DROP TABLE IF EXISTS mathCard
            """)
            database.execSQL("""
               CREATE TABLE IF NOT EXISTS mathCard(
                    cardId INTEGER NOT NULL,
                    question TEXT NOT NULL, 
                    steps TEXT NOT NULL, 
                    answer TEXT NOT NULL,
                    PRIMARY KEY(cardId),
                    FOREIGN KEY(cardId) REFERENCES cards(id) ON DELETE CASCADE)
            """)
            // Create index
            database.execSQL("CREATE INDEX index_mathCard_cardId ON mathCard (cardId)")

            database.execSQL("PRAGMA foreign_keys=ON;")
            database.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 18 to 19 failed", e)
            throw RuntimeException("Migration 18 to 19 failed: ${e.message}")
        } finally {
            database.endTransaction()
        }
    }
}