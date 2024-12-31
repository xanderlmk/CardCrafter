package com.example.flashcards.model

import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.flashcards.model.daoFiles.BasicCardDao
import com.example.flashcards.model.daoFiles.CardDao
import com.example.flashcards.model.daoFiles.CardTypesDao
import com.example.flashcards.model.daoFiles.DeckDao
import com.example.flashcards.model.daoFiles.HintCardDao
import com.example.flashcards.model.daoFiles.MultiChoiceCardDao
import com.example.flashcards.model.daoFiles.ThreeCardDao
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.Converters
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCard
import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(entities = [
    Deck::class,
    Card::class,
    BasicCard::class,
    ThreeFieldCard::class,
    HintCard::class,
    MultiChoiceCard::class], version = 9)
@TypeConverters(Converters::class)
abstract class FlashCardDatabase : RoomDatabase() {

    abstract fun deckDao(): DeckDao
    abstract fun cardDao(): CardDao
    abstract fun cardTypes() : CardTypesDao
    abstract fun basicCardDao() : BasicCardDao
    abstract fun hintCardDao() : HintCardDao
    abstract fun threeCardDao() : ThreeCardDao
    abstract fun multiChoiceCardDao() : MultiChoiceCardDao

    companion object {
        @Volatile
        private var Instance: FlashCardDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FlashCardDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(context, FlashCardDatabase::class.java, "deck_database")
                    .addMigrations(
                        MIGRATION_3_5,
                        MIGRATION_5_6,
                        MIGRATION_6_7,
                        MIGRATION_7_8,
                        MIGRATION_8_9
                    )
                    .fallbackToDestructiveMigration()
                    .addCallback(FlashCardDatabaseCallback(scope))
                    // Add callback for population
                    .build().also { Instance = it }
                Instance = instance
                // return instance
                instance
            }
        }


        private class FlashCardDatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                //db.execSQL("PRAGMA foreign_keys=ON;") // Enable foreign key support
            }
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                //db.execSQL("PRAGMA foreign_keys=ON;") // Enable foreign key support
                // Populate the database in a coroutine
                //runBlocking {
                Instance?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.deckDao(), database.cardDao(),
                            database.basicCardDao())
                    }
                //}
                }
            }
        }

        private suspend fun populateDatabase(deckDao: DeckDao, cardDao: CardDao,
                                             basicCardDao: BasicCardDao) {
            // Create and insert a new Deck
            val historyDeck = Deck(name = "History")
            deckDao.insertDeck(historyDeck)

            // Get the deck ID after insertion
            val deckId = historyDeck.id + 1// This will be auto-generated

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, -1) // Subtract 1 day
            val nextReviewDate = calendar.time

            val cards = listOf(
                Card(deckId = deckId, nextReview = nextReviewDate, passes = 0, prevSuccess = false, totalPasses = 0, type = "basic"),
                Card(deckId = deckId, nextReview = nextReviewDate, passes = 0, prevSuccess = false, totalPasses = 0, type = "basic"),
                Card(deckId = deckId, nextReview = nextReviewDate, passes = 0, prevSuccess = false, totalPasses = 0, type = "basic"),
                Card(deckId = deckId, nextReview = nextReviewDate, passes = 0, prevSuccess = false, totalPasses = 0, type = "basic"),
                Card(deckId = deckId, nextReview = nextReviewDate, passes = 0, prevSuccess = false, totalPasses = 0, type = "basic"),
                Card(deckId = deckId, nextReview = nextReviewDate, passes = 0, prevSuccess = false, totalPasses = 0, type = "basic"),
                Card(deckId = deckId, nextReview = nextReviewDate, passes = 0, prevSuccess = false, totalPasses = 0, type = "basic"),
                Card(deckId = deckId, nextReview = nextReviewDate, passes = 0, prevSuccess = false, totalPasses = 0, type = "basic"),
                Card(deckId = deckId, nextReview = nextReviewDate, passes = 0, prevSuccess = false, totalPasses = 0, type = "basic"),
                Card(deckId = deckId, nextReview = nextReviewDate, passes = 0, prevSuccess = false, totalPasses = 0, type = "basic"),
                Card(deckId = deckId, nextReview = nextReviewDate, passes = 0, prevSuccess = false, totalPasses = 0, type = "basic"),
                Card(deckId = deckId, nextReview = nextReviewDate, passes = 0, prevSuccess = false, totalPasses = 0, type = "basic"),
                Card(deckId = deckId, nextReview = nextReviewDate, passes = 0, prevSuccess = false, totalPasses = 0, type = "basic"),
                Card(deckId = deckId, nextReview = nextReviewDate, passes = 0, prevSuccess = false, totalPasses = 0, type = "basic"),
                Card(deckId = deckId, nextReview = nextReviewDate, passes = 0, prevSuccess = false, totalPasses = 0, type = "basic"),
                Card(deckId = deckId, nextReview = nextReviewDate, passes = 0, prevSuccess = false, totalPasses = 0, type = "basic"),
                Card(deckId = deckId, nextReview = nextReviewDate, passes = 0, prevSuccess = false, totalPasses = 0, type = "basic"),
                Card(deckId = deckId, nextReview = nextReviewDate, passes = 0, prevSuccess = false, totalPasses = 0, type = "basic"),
                Card(deckId = deckId, nextReview = nextReviewDate, passes = 0, prevSuccess = false, totalPasses = 0, type = "basic"),
                Card(deckId = deckId, nextReview = nextReviewDate, passes = 0, prevSuccess = false, totalPasses = 0, type = "basic"),
                Card(deckId = deckId, nextReview = nextReviewDate, passes = 0, prevSuccess = false, totalPasses = 0, type = "basic"),
            )

            val basicCards = listOf(
                BasicCard(
                    cardId = 1,
                    question = "What year did World War II begin?",
                    answer = "1939"
                ),
                BasicCard(
                    cardId = 2,
                    question = "Who was the first President of the United States?",
                    answer = "George Washington"
                ),
                BasicCard(
                    cardId = 3,
                    question = "What ancient civilization built the pyramids?",
                    answer = "Egyptians"
                ),
                BasicCard(
                    cardId = 4,
                    question = "What year did the Titanic sink?",
                    answer = "1912"
                ),
                BasicCard(
                    cardId = 5,
                    question = "Who was the first man on the moon?",
                    answer = "Neil Armstrong"
                ),
                BasicCard(
                    cardId = 6,
                    question = "What wall divided East and West Berlin?",
                    answer = "Berlin Wall"
                ),
                BasicCard(
                    cardId = 7,
                    question = "What year did the Berlin Wall fall?",
                    answer = "1989"
                ),
                BasicCard(
                    cardId = 8,
                    question = "Who wrote the Declaration of Independence?",
                    answer = "Thomas Jefferson"
                ),
                BasicCard(
                    cardId = 9,
                    question = "In which year did the U.S. Civil War begin?",
                    answer = "1861"
                ),
                BasicCard(
                    cardId = 10,
                    question = "What was the main cause of World War I?",
                    answer = "Assassination of Archduke Franz Ferdinand"
                ),
                BasicCard(
                    cardId = 11,
                    question = "Who discovered America?",
                    answer = "Christopher Columbus"
                ),
                BasicCard(
                    cardId = 12,
                    question = "What event started the Great Depression?",
                    answer = "Stock Market Crash of 1929"
                ),
                BasicCard(
                    cardId = 13,
                    question = "Which country gifted the Statue of Liberty to the United States?",
                    answer = "France"
                ),
                BasicCard(
                    cardId = 14,
                    question = "Who was the British Prime Minister during World War II?",
                    answer = "Winston Churchill"
                ),
                BasicCard(
                    cardId = 15,
                    question = "What was the name of the ship that brought the Pilgrims to America?",
                    answer = "Mayflower"
                ),
                BasicCard(
                    cardId = 16,
                    question = "What year did the U.S. enter World War I?",
                    answer = "1917"
                ),
                BasicCard(
                    cardId = 17,
                    question = "Who was the first woman to fly solo across the Atlantic?",
                    answer = "Amelia Earhart"
                ),
                BasicCard(
                    cardId = 18,
                    question = "What ancient city is known for its hanging gardens?",
                    answer = "Babylon"
                ),
                BasicCard(
                    cardId = 19,
                    question = "Who was the first Emperor of Rome?",
                    answer = "Augustus"
                ),
                BasicCard(
                    cardId = 20,
                    question = "What year did the French Revolution begin?",
                    answer = "1789"
                ),
                BasicCard(
                    cardId = 21,
                    question = "What is the longest river in the world?",
                    answer = "Nile River"
                )
            )

            // Insert all cards into the database
            for (card in cards) {
                cardDao.insertCard(card)
            }
            println("making basicCards...")
            for (basicCard in basicCards){
                basicCardDao.insertBasicCard(basicCard)
            }
            println("cards added...")
        }
    }
}

