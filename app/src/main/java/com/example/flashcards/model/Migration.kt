package com.example.flashcards.model

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.UUID

val MIGRATION_3_5 = object : Migration(3, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            database.execSQL("PRAGMA foreign_keys=ON;")
            database.execSQL("ALTER TABLE decks ADD COLUMN multiplier DOUBLE NOT NULL DEFAULT 1.5")
            database.execSQL(
                """
            CREATE TABLE cards_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                deckId INTEGER NOT NULL,
                nextReview INTEGER,
                passes INTEGER NOT NULL,
                prevSuccess INTEGER NOT NULL,
                totalPasses INTEGER NOT NULL,
                type TEXT NOT NULL,
                FOREIGN KEY(deckId) REFERENCES decks(id)
            )
            """
            )
            database.execSQL(
                "INSERT INTO cards_new (id, deckId, passes, prevSuccess, totalPasses, type, nextReview) " +
                        "SELECT id, deckId, passes, prevSuccess, totalPasses, type, nextReview FROM cards"
            )
            database.execSQL("DROP TABLE cards")
            database.execSQL("ALTER TABLE cards_new RENAME TO cards")
            database.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 4 to 5 failed", e)
            throw RuntimeException("Migration 4 to 5 failed: ${e.message}")
        } finally {
            database.endTransaction()
        }
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            database.execSQL("PRAGMA foreign_keys=ON;")
            database.execSQL("ALTER TABLE decks ADD COLUMN badMultiplier DOUBLE NOT NULL DEFAULT 0.5")
            database.execSQL("ALTER TABLE decks RENAME COLUMN multiplier TO goodMultiplier")
            database.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 5 to 6 failed", e)
            throw RuntimeException("Migration 5 to 6 failed: ${e.message}")
        } finally {
            database.endTransaction()
        }
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            database.execSQL("PRAGMA foreign_keys=ON;")

            // Add 'createdOn' column to the 'decks' table
            database.execSQL("ALTER TABLE decks ADD COLUMN createdOn INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}")

            // Add 'createdOn' column to the 'cards' table
            database.execSQL("ALTER TABLE cards ADD COLUMN createdOn INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}")
            database.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 6 to 7 failed", e)
            throw RuntimeException("Migration 6 to 7 failed: ${e.message}")
        } finally {
            database.endTransaction()
        }
    }
}

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            database.execSQL("PRAGMA foreign_keys=ON;")

            database.execSQL(
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
                    FOREIGN KEY(cardId) REFERENCES cards(id) ON DELETE CASCADE)
            """
            )
            database.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 7 to 8 failed", e)
            throw RuntimeException("Migration 7 to 8 failed: ${e.message}")
        } finally {
            database.endTransaction()
        }
    }
}

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            database.execSQL("PRAGMA foreign_keys=ON;")
            database.execSQL("CREATE INDEX index_cards_deckId ON cards (deckId)")
            database.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 8 to 9 failed", e)
            throw RuntimeException("Migration 8 to 9 failed: ${e.message}")
        } finally {
            database.endTransaction()
        }
    }
}

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            // Add the uuid column to the decks table
            database.execSQL("ALTER TABLE decks ADD COLUMN uuid TEXT NOT NULL DEFAULT ''")

            // Add the deckUUID column to the cards table
            database.execSQL("ALTER TABLE cards ADD COLUMN deckUUID TEXT NOT NULL DEFAULT ''")

            // Add the reviewAmount column to the decks table
            database.execSQL("ALTER TABLE decks ADD COLUMN reviewAmount INTEGER NOT NULL DEFAULT 1")

            // Add the reviewsLeft column to the cards table
            database.execSQL("ALTER TABLE cards ADD COLUMN reviewsLeft INTEGER NOT NULL DEFAULT 1")

            // Generate UUIDs for existing decks
            val cursorDecks = database.query("SELECT id FROM decks")
            while (cursorDecks.moveToNext()) {
                val id = cursorDecks.getInt(cursorDecks.getColumnIndexOrThrow("id"))
                val uuid = UUID.randomUUID().toString()
                database.execSQL("UPDATE decks SET uuid = '$uuid' WHERE id = $id")
            }
            cursorDecks.close()

            // Generate deckUUIDs for existing cards
            val cursorCards = database.query("SELECT id, deckId FROM cards")
            while (cursorCards.moveToNext()) {
                val id = cursorCards.getInt(cursorCards.getColumnIndexOrThrow("id"))
                val deckId = cursorCards.getInt(cursorCards.getColumnIndexOrThrow("deckId"))

                // Get the deck's UUID
                val cursorDeck = database.query("SELECT uuid FROM decks WHERE id = $deckId")
                if (cursorDeck.moveToFirst()) {
                    val deckUUID = cursorDeck.getString(cursorDeck.getColumnIndexOrThrow("uuid"))
                    database.execSQL("UPDATE cards SET deckUUID = '$deckUUID' WHERE id = $id")
                }
                cursorDeck.close()
            }
            cursorCards.close()

            database.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 9 to 10 failed", e)
            throw RuntimeException("Migration 9 to 10 failed: ${e.message}")
        } finally {
            database.endTransaction()
        }
    }
}



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


