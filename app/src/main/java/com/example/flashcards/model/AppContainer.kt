package com.example.flashcards.model

import android.content.Context

interface AppContainer {
    val flashCardRepository: FlashCardRepository
}
class AppDataContainer(private val context: Context) : AppContainer {
    override val flashCardRepository: FlashCardRepository by lazy {
        OfflineFlashCardRepository(FlashCardDatabase.getDatabase(context).deckDao(),
            FlashCardDatabase.getDatabase(context).cardDao())
    }
}