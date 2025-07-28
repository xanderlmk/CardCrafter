package com.belmontCrest.cardCrafter.model.migrations

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


val MIGRATION_31_32 = object : Migration(31, 32) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("PRAGMA foreign_keys=OFF;")
            db.beginTransaction()

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS custom_card(
                    cardId INTEGER NOT NULL,
                    question TEXT NOT NULL,
                    middle TEXT,
                    answer TEXT NOT NULL,
                    PRIMARY KEY(cardId),
                    FOREIGN KEY(cardId) REFERENCES cards(id) ON DELETE CASCADE 
                    ON UPDATE CASCADE
                )
            """.trimIndent())

            db.execSQL("CREATE INDEX index_custom_card_cardId ON custom_card (cardId)")

            db.execSQL("PRAGMA foreign_keys=ON;")
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e("Migration", "Migration 31 to 32 failed", e)
            throw RuntimeException("Migration 31 to 32 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }
}