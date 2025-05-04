package com.belmontCrest.cardCrafter.model.migrations

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


val MIGRATION_27_28 = object : Migration(27, 28) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.beginTransaction()
            db.execSQL("PRAGMA foreign_keys=OFF;")
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
                    FOREIGN KEY(deckId) REFERENCES decks(id) ON DELETE CASCADE
                );""".trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO cards_temp (
                id, deckId, deckUUID, reviewsLeft, nextReview, passes, prevSuccess,
                totalPasses, type, createdOn, partOfList, deckCardNumber, cardIdentifier
                ) SELECT 
                    id, deckId, deckUUID, reviewsLeft,
                    nextReview, passes, prevSuccess,
                    totalPasses, type, createdOn, partOfList,
                    deckCardNumber, cardIdentifier
                    FROM cards;
            """.trimIndent()
            )
            db.execSQL("""DROP TABLE cards;""")
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

            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_cards_deckId 
                ON cards (deckId);
            """.trimIndent()
            )
            db.execSQL("PRAGMA foreign_keys=ON;")
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 27 to 28 failed", e)
            throw RuntimeException("Migration 27 to 28 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }
}

val MIGRATION_28_29 = object : Migration(28, 29) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("PRAGMA foreign_keys=OFF;")
            db.beginTransaction()
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS card_info(
                card_identifier TEXT PRIMARY KEY NOT NULL,
                is_local INTEGER NOT NULL,
                FOREIGN KEY (card_identifier) REFERENCES cards(cardIdentifier) ON DELETE CASCADE 
                ON UPDATE CASCADE
                );
            """.trimIndent()
            )

            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_card_info_card_identifier 
                ON card_info(card_identifier);
            """.trimIndent()
            )
            db.execSQL(
                """
                CREATE UNIQUE INDEX IF NOT EXISTS index_cards_cardIdentifier 
                ON cards(cardIdentifier);
            """.trimIndent()
            )


            db.execSQL("PRAGMA foreign_keys=ON;")
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e("Migration", "Migration 28 to 29 failed", e)
            throw RuntimeException("Migration 28 to 29 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }
}

val MIGRATION_29_30 = object : Migration(29, 30) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("PRAGMA foreign_keys=OFF;")
            db.beginTransaction()
            // Create temporary table
            db.execSQL(
                """
                CREATE TABLE basicCard_temp (
                        cardId INTEGER NOT NULL,
                        question TEXT NOT NULL, 
                        answer TEXT NOT NULL,
                        PRIMARY KEY(cardId),
                        FOREIGN KEY(cardId) REFERENCES cards(id) ON DELETE CASCADE 
                        ON UPDATE CASCADE
                        )
                        """
            )

            // Copy data
            db.execSQL(
                """
                INSERT INTO basicCard_temp (cardId, question, answer) 
                        SELECT cardId, question, answer FROM basicCard
                        """
            )

            // Drop old table
            db.execSQL("DROP TABLE basicCard")
            // Rename temporary table
            db.execSQL("ALTER TABLE basicCard_temp RENAME TO basicCard")

            // Create index
            db.execSQL("CREATE INDEX index_basicCard_cardId ON basicCard (cardId)")

            // Create temporary table
            db.execSQL(
                """
                CREATE TABLE threeFieldCard_temp (
                        cardId INTEGER NOT NULL,
                        question TEXT NOT NULL,
                        middle TEXT NOT NULL,
                        answer TEXT NOT NULL,
                        PRIMARY KEY(cardId),
                        FOREIGN KEY(cardId) REFERENCES cards(id) ON DELETE CASCADE 
                        ON UPDATE CASCADE
                        )
                        """
            )

            // Copy data
            db.execSQL(
                """
                INSERT INTO threeFieldCard_temp (cardId, question, middle, answer) 
                        SELECT cardId, question, middle, answer FROM threeFieldCard
                        """
            )

            // Drop old table
            db.execSQL("DROP TABLE threeFieldCard")

            // Rename temporary table
            db.execSQL("ALTER TABLE threeFieldCard_temp RENAME TO threeFieldCard")
            // Create index
            db.execSQL("CREATE INDEX index_threeFieldCard_cardId ON threeFieldCard (cardId)")


            // Create temporary table
            db.execSQL(
                """
                CREATE TABLE hintCard_temp (
                        cardId INTEGER NOT NULL, 
                        question TEXT NOT NULL, 
                        hint TEXT NOT NULL, 
                        answer TEXT NOT NULL,
                        PRIMARY KEY(cardId),
                        FOREIGN KEY(cardId) REFERENCES cards(id) ON DELETE CASCADE 
                        ON UPDATE CASCADE
                        )
                        """
            )

            // Copy data
            db.execSQL(
                """
                INSERT INTO hintCard_temp (cardId, question, hint, answer) 
                        SELECT cardId, question, hint, answer FROM hintCard
                        """
            )

            // Drop old table
            db.execSQL("DROP TABLE hintCard")

            // Rename temporary table
            db.execSQL("ALTER TABLE hintCard_temp RENAME TO hintCard")
            // Create index
            db.execSQL("CREATE INDEX index_hintCard_cardId ON hintCard (cardId)")

            // Create temporary table
            db.execSQL(
                """
                CREATE TABLE multiChoiceCard_temp (
                        cardId INTEGER NOT NULL,
                        question TEXT NOT NULL, 
                        choiceA TEXT NOT NULL,
                        choiceB TEXT NOT NULL, 
                        choiceC TEXT NOT NULL, 
                        choiceD TEXT NOT NULL, 
                        correct INTEGER  NOT NULL,
                        PRIMARY KEY(cardId),
                        FOREIGN KEY(cardId) REFERENCES cards(id) ON DELETE CASCADE 
                        ON UPDATE CASCADE
                        )
                        """
            )

            // Copy data
            db.execSQL(
                "INSERT INTO multiChoiceCard_temp (cardId, question, choiceA, choiceB, choiceC, choiceD, correct) " +
                        "SELECT cardId, question, choiceA, choiceB, choiceC, choiceD, correct FROM multiChoiceCard"
            )

            // Drop old table
            db.execSQL("DROP TABLE multiChoiceCard")

            // Rename temporary table
            db.execSQL("ALTER TABLE multiChoiceCard_temp RENAME TO multiChoiceCard")

            db.execSQL("CREATE INDEX index_multiChoiceCard_cardId ON multiChoiceCard (cardId)")

            db.execSQL(
                """
                 CREATE TABLE notationCard_temp(
                    cardId INTEGER NOT NULL,
                    question TEXT NOT NULL, 
                    steps TEXT NOT NULL, 
                    answer TEXT NOT NULL,
                    PRIMARY KEY(cardId),
                    FOREIGN KEY(cardId) REFERENCES cards(id) ON DELETE CASCADE 
                    ON UPDATE CASCADE
                )
            """.trimIndent()
            )

            db.execSQL(
                """
                INSERT INTO notationCard_temp(cardId, question, steps, answer)
                SELECT cardId, question, steps, answer FROM notationCard
            """.trimIndent()
            )

            db.execSQL(
                """
                DROP TABLE notationCard
            """.trimIndent()
            )
            db.execSQL(
                """
                ALTER TABLE notationCard_temp RENAME TO notationCard
            """.trimIndent()
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_notationCard_cardId ON notationCard (cardId)
                """.trimIndent()
            )
            db.execSQL("PRAGMA foreign_keys=ON;")
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("Migration", "Migration 29 to 30 failed", e)
            throw RuntimeException("Migration 29 to 30 failed: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }
}