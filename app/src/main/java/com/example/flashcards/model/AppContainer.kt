package com.example.flashcards.model

import android.content.Context

interface AppContainer {
    val decksRepository: FlashCardRepository
}
class AppDataContainer(private val context: Context) : AppContainer {
    override val decksRepository: FlashCardRepository by lazy {
        OfflineFlashCardRepository(FlashCardDatabase.getDatabase(context).deckDao())
    }
}