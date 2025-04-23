package com.belmontCrest.cardCrafter.model.migrations

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_20_21 = object : Migration(20, 21) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.beginTransaction()
            db.execSQL(
                """
    CREATE TABLE cards_temp (
        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
        deckId INTEGER NOT NULL,
        deckUUID TEXT NOT NULL,
        reviewsLeft INTEGER NOT NULL,
        nextReview INTEGER NOT NULL,
        passes INTEGER NOT NULL,
        prevSuccess INTEGER NOT NULL,
        totalPasses INTEGER NOT NULL,
        type TEXT NOT NULL,
        createdOn INTEGER NOT NULL,
        partOfList INTEGER NOT NULL,
        deckCardNumber INTEGER,
        cardIdentifier TEXT NOT NULL,
        FOREIGN KEY(deckId) REFERENCES decks(id)
    );"""
            )
            db.execSQL(
                """
                INSERT INTO cards_temp (
                id, deckId, deckUUID, reviewsLeft, nextReview, passes, prevSuccess,
                totalPasses, type, createdOn, partOfList, deckCardNumber, cardIdentifier
                ) SELECT 
                    id,
                    deckId,
                    deckUUID,
                    reviewsLeft,
                    nextReview,
                    passes,
                    prevSuccess,
                    totalPasses,
                    type,
                    createdOn,
                    partOfList,
                    row_number() OVER (PARTITION BY deckUUID ORDER BY createdOn, id) - 1 AS deckCardNumber,
                    deckUUID || '-' || (row_number() OVER (PARTITION BY deckUUID ORDER BY createdOn, id) - 1) AS cardIdentifier   
                    FROM cards;
            """.trimIndent()
            )
            db.execSQL(
                """
            DROP TABLE cards;
            """
            )
            db.execSQL(
                """
            ALTER TABLE cards_temp RENAME TO cards;
            """
            )
            db.execSQL(
                """
            CREATE UNIQUE INDEX IF NOT EXISTS index_cards_deckUUID_deckCardNumber
            ON cards (deckUUID, deckCardNumber);
            """.trimIndent()
            )

            db.execSQL("""
                CREATE INDEX IF NOT EXISTS index_cards_deckId 
                ON cards (deckId);
            """.trimIndent())

            db.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 20 to 21 failed", e)
            throw RuntimeException("Migration 20 to 21 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }

}

val MIGRATION_21_22 = object : Migration(21,22) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.beginTransaction()
            db.execSQL("""
                CREATE UNIQUE INDEX IF NOT EXISTS index_decks_uuid
                ON decks(uuid)
            """.trimIndent())
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 21 to 22 failed", e)
            throw RuntimeException("Migration 21 to 22 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }
}

val MIGRATION_22_23 = object : Migration(22,23) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.beginTransaction()
            db.execSQL("PRAGMA foreign_keys=OFF;")

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS importedDeckInfo(
                uuid TEXT PRIMARY KEY NOT NULL,
                lastUpdatedOn TEXT NOT NULL,
                FOREIGN KEY(uuid) REFERENCES decks(uuid) ON DELETE CASCADE
                )
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS syncedDeckInfo(
                uuid TEXT PRIMARY KEY NOT NULL,
                lastUpdatedOn TEXT NOT NULL,
                FOREIGN KEY(uuid) REFERENCES decks(uuid) ON DELETE CASCADE
                )
            """.trimIndent())

            db.execSQL("""
                ALTER TABLE decks
                ADD COLUMN cardsDone
                INTEGER NOT NULL DEFAULT 0
            """.trimIndent())
            db.execSQL("""
                CREATE INDEX IF NOT EXISTS index_importedDeckInfo_uuid on importedDeckInfo(uuid)
            """.trimIndent())
            db.execSQL("""
                CREATE INDEX IF NOT EXISTS index_syncedDeckInfo_uuid on syncedDeckInfo(uuid)
            """.trimIndent())
            db.execSQL("PRAGMA foreign_keys=ON;")

            Log.d("MIGRATION 22-23", "SUCCESS")
            db.setTransactionSuccessful()
        }  catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 22 to 23 failed", e)
            throw RuntimeException("Migration 22 to 23 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }
}

val MIGRATION_23_24 = object : Migration(23,24) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.beginTransaction()
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS pwd(
                    password TEXT PRIMARY KEY NOT NULL
                )
            """.trimIndent())
            db.setTransactionSuccessful()
        }  catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 23 to 24 failed", e)
            throw RuntimeException("Migration 23 to 24 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }
}

val MIGRATION_24_25 = object : Migration(24,25) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.beginTransaction()
            // 1. create a temp.
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS pwd_temp(
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    password TEXT NOT NULL
                )
            """.trimIndent())
            // 2. copy data
            db.execSQL("""
                INSERT INTO pwd_temp (password)
                SELECT password FROM pwd
            """.trimIndent())
            // 3. swap tables
            db.execSQL("DROP TABLE pwd")
            db.execSQL("ALTER TABLE pwd_temp RENAME TO pwd")

            db.setTransactionSuccessful()
        }  catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 24 to 25 failed", e)
            throw RuntimeException("Migration 24 to 25 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }
}

val MIGRATION_25_26 = object : Migration(25,26) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.beginTransaction()
            db.execSQL("""
            CREATE TABLE IF NOT EXISTS syncedDeckInfo_new (
                uuid TEXT NOT NULL,
                lastUpdatedOn TEXT NOT NULL,
                PRIMARY KEY(uuid)
            )
        """.trimIndent())

            db.execSQL("""
            INSERT INTO syncedDeckInfo_new(uuid, lastUpdatedOn)
            SELECT uuid, lastUpdatedOn FROM syncedDeckInfo
        """.trimIndent())

            db.execSQL("DROP TABLE syncedDeckInfo")

            db.execSQL("ALTER TABLE syncedDeckInfo_new RENAME TO syncedDeckInfo")

            db.setTransactionSuccessful()
        }  catch (e: Exception) {

            Log.e("Migration", "Migration 25 to 26 failed", e)
            throw RuntimeException("Migration 25 to 26 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }
}


val MIGRATION_26_27 = object : Migration(26,27) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.beginTransaction()
            db.execSQL("""
            ALTER TABLE syncedDeckInfo RENAME to syncedDeckInfo_T
        """.trimIndent())

            db.execSQL("""
            ALTER TABLE syncedDeckInfo_T RENAME to syncedDeckInfo
        """.trimIndent())



            db.setTransactionSuccessful()
        }  catch (e: Exception) {
            Log.e("Migration", "Migration 26 to 27 failed", e)
            throw RuntimeException("Migration 26 to 27 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }
}