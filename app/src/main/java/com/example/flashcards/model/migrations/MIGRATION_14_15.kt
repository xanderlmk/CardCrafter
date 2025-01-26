package com.example.flashcards.model.migrations

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_14_15 = object : Migration(14, 15)  {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        try {
            database.execSQL("PRAGMA foreign_keys=ON;")
            // Create temporary table
            database.execSQL(
                """
                CREATE TABLE basicCard_temp (
                        cardId INTEGER NOT NULL,
                        question TEXT NOT NULL, 
                        answer TEXT NOT NULL,
                        PRIMARY KEY(cardId),
                        FOREIGN KEY(cardId) REFERENCES cards(id) ON DELETE CASCADE)
                        """
            )

            // Copy data
            database.execSQL(
                """
                INSERT INTO basicCard_temp (cardId, question, answer) 
                        SELECT cardId, question, answer FROM basicCard
                        """
            )

            // Drop old table
            database.execSQL("DROP TABLE basicCard")
            // Rename temporary table
            database.execSQL("ALTER TABLE basicCard_temp RENAME TO basicCard")

            // Create index
            database.execSQL("CREATE INDEX index_basicCard_cardId ON basicCard (cardId)")

            // Create temporary table
            database.execSQL("""
                CREATE TABLE threeFieldCard_temp (
                        cardId INTEGER NOT NULL,
                        question TEXT NOT NULL,
                        middle TEXT NOT NULL,
                        answer TEXT NOT NULL,
                        PRIMARY KEY(cardId),
                        FOREIGN KEY(cardId) REFERENCES cards(id) ON DELETE CASCADE )
                        """)

            // Copy data
            database.execSQL("""
                INSERT INTO threeFieldCard_temp (cardId, question, middle, answer) 
                        SELECT cardId, question, middle, answer FROM threeFieldCard
                        """
            )

            // Drop old table
            database.execSQL("DROP TABLE threeFieldCard")

            // Rename temporary table
            database.execSQL("ALTER TABLE threeFieldCard_temp RENAME TO threeFieldCard")
            // Create index
            database.execSQL("CREATE INDEX index_threeFieldCard_cardId ON threeFieldCard (cardId)")


            // Create temporary table
            database.execSQL("""
                CREATE TABLE hintCard_temp (
                        cardId INTEGER NOT NULL, 
                        question TEXT NOT NULL, 
                        hint TEXT NOT NULL, 
                        answer TEXT NOT NULL,
                        PRIMARY KEY(cardId),
                        FOREIGN KEY(cardId) REFERENCES cards(id) ON DELETE CASCADE)
                        """)

            // Copy data
            database.execSQL("""
                INSERT INTO hintCard_temp (cardId, question, hint, answer) 
                        SELECT cardId, question, hint, answer FROM hintCard
                        """
            )

            // Drop old table
            database.execSQL("DROP TABLE hintCard")

            // Rename temporary table
            database.execSQL("ALTER TABLE hintCard_temp RENAME TO hintCard")
            // Create index
            database.execSQL("CREATE INDEX index_hintCard_cardId ON hintCard (cardId)")

            // Create temporary table
            database.execSQL("""
                CREATE TABLE multiChoiceCard_temp (
                        cardId INTEGER NOT NULL,
                        question TEXT NOT NULL, 
                        choiceA TEXT NOT NULL,
                        choiceB TEXT NOT NULL, 
                        choiceC TEXT NOT NULL, 
                        choiceD TEXT NOT NULL, 
                        correct INTEGER  NOT NULL,
                        PRIMARY KEY(cardId),
                        FOREIGN KEY(cardId) REFERENCES cards(id) ON DELETE CASCADE)
                        """)

            // Copy data
            database.execSQL(
                "INSERT INTO multiChoiceCard_temp (cardId, question, choiceA, choiceB, choiceC, choiceD, correct) " +
                        "SELECT cardId, question, choiceA, choiceB, choiceC, choiceD, correct FROM multiChoiceCard"
            )

            // Drop old table
            database.execSQL("DROP TABLE multiChoiceCard")

            // Rename temporary table
            database.execSQL("ALTER TABLE multiChoiceCard_temp RENAME TO multiChoiceCard")

            database.execSQL("CREATE INDEX index_multiChoiceCard_cardId ON multiChoiceCard (cardId)")


            database.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 14 to 15 failed", e)
            throw RuntimeException("Migration 14 to 15 failed: ${e.message}")
        } finally {
            database.endTransaction()
        }
    }
}