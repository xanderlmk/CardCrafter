package com.example.flashcards.model

import android.content.Context
import kotlinx.coroutines.CoroutineScope

interface AppContainer {
    val flashCardRepository: FlashCardRepository
}
class AppDataContainer(private val context: Context, scope: CoroutineScope) : AppContainer {
    override val flashCardRepository: FlashCardRepository by lazy {
        OfflineFlashCardRepository(FlashCardDatabase.getDatabase(context,scope).deckDao(),
            FlashCardDatabase.getDatabase(context,scope).cardDao())
    }
}