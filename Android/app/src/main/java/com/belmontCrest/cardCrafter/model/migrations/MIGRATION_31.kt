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

val MIGRATION_32_33 = object : Migration(32, 33) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("PRAGMA foreign_keys=OFF;")
            db.beginTransaction()
            db.execSQL("""
                DROP TABLE IF EXISTS savedCards;
            """.trimIndent())
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS saved_card(
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                cardId INTEGER NOT NULL,
                reviewsLeft INTEGER NOT NULL, 
                nextReview INTEGER NOT NULL, 
                passes INTEGER NOT NULL, 
                prevSuccess INTEGER NOT NULL, 
                totalPasses INTEGER NOT NULL, 
                partOfList INTEGER NOT NULL,
                FOREIGN KEY(cardId) REFERENCES cards(id) ON DELETE CASCADE 
                ON UPDATE CASCADE
                )
            """.trimIndent())
            db.execSQL("CREATE INDEX index_saved_card_cardId ON saved_card (cardId)")
            db.execSQL("PRAGMA foreign_keys=ON;")
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e("Migration", "Migration 32 to 34 failed", e)
            throw RuntimeException("Migration 32 to 34 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }
}

val MIGRATION_33_34 = object : Migration(33, 34) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("PRAGMA foreign_keys=OFF;")
            db.execSQL("""
                ALTER TABLE saved_card ADD COLUMN createdOn INTEGER NOT NULL
            """.trimIndent())
            db.execSQL("PRAGMA foreign_keys=ON;")
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e("Migration", "Migration 33 to 34 failed", e)
            throw RuntimeException("Migration 33 to 34 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }
}