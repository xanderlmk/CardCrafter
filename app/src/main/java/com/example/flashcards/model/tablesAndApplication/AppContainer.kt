package com.example.flashcards.model.tablesAndApplication

import android.content.Context
import com.example.flashcards.model.FlashCardDatabase
import com.example.flashcards.model.repositories.CardTypeRepository
import com.example.flashcards.model.repositories.FlashCardRepository
import com.example.flashcards.model.repositories.OfflineCardTypeRepository
import com.example.flashcards.model.repositories.OfflineFlashCardRepository
import kotlinx.coroutines.CoroutineScope

interface AppContainer {
    val flashCardRepository: FlashCardRepository
    val cardTypeRepository : CardTypeRepository
}
class AppDataContainer(private val context: Context, scope: CoroutineScope) : AppContainer {
    override val flashCardRepository: FlashCardRepository by lazy {
        OfflineFlashCardRepository(
            FlashCardDatabase.Companion.getDatabase(context, scope).deckDao(),
            FlashCardDatabase.Companion.getDatabase(context, scope).cardDao()
        )
    }
    override val cardTypeRepository: CardTypeRepository by lazy {
        OfflineCardTypeRepository(
            FlashCardDatabase.Companion.getDatabase(context,scope).cardTypes(),
            FlashCardDatabase.Companion.getDatabase(context,scope).basicCardDao(),
            FlashCardDatabase.Companion.getDatabase(context,scope).hintCardDao(),
            FlashCardDatabase.Companion.getDatabase(context,scope).threeCardDao(),
            FlashCardDatabase.Companion.getDatabase(context,scope).multiChoiceCardDao()
        )
    }
}