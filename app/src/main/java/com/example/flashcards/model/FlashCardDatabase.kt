package com.example.flashcards.model

import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.TypeConverters

@Database(entities = [Deck::class, Card::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class FlashCardDatabase : RoomDatabase() {

    abstract fun deckDao(): DeckDao
    abstract fun cardDao(): CardDao

    companion object {
        @Volatile
        private var Instance: FlashCardDatabase? = null

        fun getDatabase(context: Context): FlashCardDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, FlashCardDatabase::class.java, "deck_database")
                    .build().also { Instance = it }
            }
        }
    }
}