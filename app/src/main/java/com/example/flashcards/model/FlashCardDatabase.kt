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
            val deckId = historyDeck.id // This will be auto-generated

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, -1) // Subtract 1 day
            val nextReviewDate = calendar.time

            // Create and insert Cards related to the Deck
            val card1 = Card(deckId = deckId, question = "What year did World War II begin?", answer = "1939", nextReview = nextReviewDate, passes = 0)
            val card2 = Card(deckId = deckId, question = "Who was the first President of the United States?", answer = "George Washington", nextReview = nextReviewDate, passes = 0)
            val card3 = Card(deckId = deckId, question = "What ancient civilization built the pyramids?", answer = "Egyptians", nextReview = nextReviewDate, passes = 0)


            // Insert cards into the database
            cardDao.insertCard(card1)
            cardDao.insertCard(card2)
            cardDao.insertCard(card3)
        }
    }
}