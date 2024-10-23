package com.example.flashcards.model

import androidx.room.RoomDatabase
import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.room.Database
import androidx.room.Room
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@Database(entities = [Deck::class, Card::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class FlashCardDatabase : RoomDatabase() {

    abstract fun deckDao(): DeckDao
    abstract fun cardDao(): CardDao

    companion object {
        @Volatile
        private var Instance: FlashCardDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FlashCardDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(context, FlashCardDatabase::class.java, "deck_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(FlashCardDatabaseCallback(scope)) // Add callback for population
                    .build()
                //.build().also { Instance = it }
                Instance = instance
                // return instance
                instance
            }
        }


        private class FlashCardDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Populate the database in a coroutine
                Instance?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.deckDao(), database.cardDao())
                    }
                }
            }
        }

        private suspend fun populateDatabase(deckDao: DeckDao, cardDao: CardDao) {
            // Create and insert a new Deck
            val historyDeck = Deck(name = "History")
            deckDao.insertDeck(historyDeck)

            // Get the deck ID after insertion
            val deckId = historyDeck.id + 1// This will be auto-generated

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, -1) // Subtract 1 day
            val nextReviewDate = calendar.time

            val cards = listOf(
                Card(deckId = deckId, question = "What year did World War II begin?", answer = "1939", nextReview = nextReviewDate, passes = 0),
                Card(deckId = deckId, question = "Who was the first President of the United States?", answer = "George Washington", nextReview = nextReviewDate, passes = 0),
                Card(deckId = deckId, question = "What ancient civilization built the pyramids?", answer = "Egyptians", nextReview = nextReviewDate, passes = 0),
                Card(deckId = deckId, question = "What year did the Titanic sink?", answer = "1912", nextReview = nextReviewDate, passes = 0),
                Card(deckId = deckId, question = "Who was the first man on the moon?", answer = "Neil Armstrong", nextReview = nextReviewDate, passes = 0),
                Card(deckId = deckId, question = "What wall divided East and West Berlin?", answer = "Berlin Wall", nextReview = nextReviewDate, passes = 0),
                Card(deckId = deckId, question = "What year did the Berlin Wall fall?", answer = "1989", nextReview = nextReviewDate, passes = 0),
                Card(deckId = deckId, question = "Who wrote the Declaration of Independence?", answer = "Thomas Jefferson", nextReview = nextReviewDate, passes = 0),
                Card(deckId = deckId, question = "In which year did the U.S. Civil War begin?", answer = "1861", nextReview = nextReviewDate, passes = 0),
                Card(deckId = deckId, question = "What was the main cause of World War I?", answer = "Assassination of Archduke Franz Ferdinand", nextReview = nextReviewDate, passes = 0),
                Card(deckId = deckId, question = "Who discovered America?", answer = "Christopher Columbus", nextReview = nextReviewDate, passes = 0),
                Card(deckId = deckId, question = "What event started the Great Depression?", answer = "Stock Market Crash of 1929", nextReview = nextReviewDate, passes = 0),
                Card(deckId = deckId, question = "Which country gifted the Statue of Liberty to the United States?", answer = "France", nextReview = nextReviewDate, passes = 0),
                Card(deckId = deckId, question = "Who was the British Prime Minister during World War II?", answer = "Winston Churchill", nextReview = nextReviewDate, passes = 0),
                Card(deckId = deckId, question = "What was the name of the ship that brought the Pilgrims to America?", answer = "Mayflower", nextReview = nextReviewDate, passes = 0),
                Card(deckId = deckId, question = "What year did the U.S. enter World War I?", answer = "1917", nextReview = nextReviewDate, passes = 0),
                Card(deckId = deckId, question = "Who was the first woman to fly solo across the Atlantic?", answer = "Amelia Earhart", nextReview = nextReviewDate, passes = 0),
                Card(deckId = deckId, question = "What ancient city is known for its hanging gardens?", answer = "Babylon", nextReview = nextReviewDate, passes = 0),
                Card(deckId = deckId, question = "Who was the first Emperor of Rome?", answer = "Augustus", nextReview = nextReviewDate, passes = 0),
                Card(deckId = deckId, question = "What year did the French Revolution begin?", answer = "1789", nextReview = nextReviewDate, passes = 0),
                Card(deckId = deckId, question = "What is the longest river in the world?", answer = "Nile River", nextReview = nextReviewDate, passes = 0)
            )

// Insert all cards into the database
            for (card in cards) {
                cardDao.insertCard(card)
            }
        }
    }
}