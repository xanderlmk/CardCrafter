package com.belmontCrest.cardCrafter.model.migrations.m16_m20

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** I tried doing the migration to add the MathCard
 *  But this migration was not successful, because I
 *  I forgot to add the MathCard to the database :/
 */
val MIGRATION_17_18 = object : Migration(17, 18) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            db.execSQL("PRAGMA foreign_keys=OFF;")
            db.execSQL("""
               CREATE TABLE IF NOT EXISTS mathCard(
                    cardId INTEGER NOT NULL,
                    question TEXT NOT NULL, 
                    steps TEXT, 
                    answer TEXT NOT NULL,
                    PRIMARY KEY(cardId),
                    FOREIGN KEY(cardId) REFERENCES cards(id) ON DELETE CASCADE)
            """)
            // Create index
            db.execSQL("CREATE INDEX index_mathCard_cardId ON mathCard (cardId)")

            db.execSQL("PRAGMA foreign_keys=ON;")
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 17 to 18 failed", e)
            throw RuntimeException("Migration 17 to 18 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }
}