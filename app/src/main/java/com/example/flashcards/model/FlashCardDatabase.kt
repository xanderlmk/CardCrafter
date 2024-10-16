package com.example.flashcards.model

import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Database
import androidx.room.Room

@Database(entities = [Decks::class], version = 1, exportSchema = false)
abstract class FlashCardDatabase : RoomDatabase() {

    abstract fun deckDao(): DeckDao

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