package com.belmontCrest.cardCrafter.model.migrations

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** I tried doing the migration to add the MathCard
 *  But this migration was not successful, because I
 *  I forgot to add the MathCard to the database :/
 */
val MIGRATION_17_18 = object : Migration(17, 18) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            database.execSQL("PRAGMA foreign_keys=OFF;")
            database.execSQL("""
               CREATE TABLE IF NOT EXISTS mathCard(
                    cardId INTEGER NOT NULL,
                    question TEXT NOT NULL, 
                    steps TEXT, 
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
            Log.e("Migration", "Migration 17 to 18 failed", e)
            throw RuntimeException("Migration 17 to 18 failed: ${e.message}")
        } finally {
            database.endTransaction()
        }
    }
}