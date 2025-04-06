package com.belmontCrest.cardCrafter.model.tablesAndApplication

import android.content.Context
import com.belmontCrest.cardCrafter.model.database.FlashCardDatabase
import com.belmontCrest.cardCrafter.model.databaseInterface.repositories.CardTypeRepository
import com.belmontCrest.cardCrafter.model.databaseInterface.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.model.databaseInterface.repositories.OfflineCardTypeRepository
import com.belmontCrest.cardCrafter.model.databaseInterface.repositories.OfflineFlashCardRepository
import com.belmontCrest.cardCrafter.model.databaseInterface.repositories.OfflineScienceRepository
import com.belmontCrest.cardCrafter.model.databaseInterface.repositories.ScienceSpecificRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repository.OfflineSupabaseRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repository.SupabaseRepository
import kotlinx.coroutines.CoroutineScope

/** Creating our App Container which will get the repositories,
 * who have all our Dao interfaces, which are tied by the database.
 */
interface AppContainer {
    val flashCardRepository: FlashCardRepository
    val cardTypeRepository : CardTypeRepository
    val scienceSpecificRepository : ScienceSpecificRepository
    val supabaseRepository : SupabaseRepository
}
class AppDataContainer(private val context: Context, scope: CoroutineScope) : AppContainer {
    override val flashCardRepository: FlashCardRepository by lazy {
        OfflineFlashCardRepository(
            FlashCardDatabase.Companion.getDatabase(context, scope).deckDao(),
            FlashCardDatabase.Companion.getDatabase(context, scope).cardDao(),
            FlashCardDatabase.Companion.getDatabase(context, scope).savedCardDao()
        )
    }
    override val cardTypeRepository: CardTypeRepository by lazy {
        OfflineCardTypeRepository(
            FlashCardDatabase.Companion.getDatabase(context, scope).cardTypes(),
            FlashCardDatabase.Companion.getDatabase(context, scope).basicCardDao(),
            FlashCardDatabase.Companion.getDatabase(context, scope).hintCardDao(),
            FlashCardDatabase.Companion.getDatabase(context, scope).threeCardDao(),
            FlashCardDatabase.Companion.getDatabase(context, scope).multiChoiceCardDao()
        )
    }
    override val scienceSpecificRepository: ScienceSpecificRepository by lazy {
        OfflineScienceRepository(
            FlashCardDatabase.Companion.getDatabase(context, scope).notationCardDao()
        )
    }
    override val supabaseRepository : SupabaseRepository by lazy {
        OfflineSupabaseRepository(
            FlashCardDatabase.Companion.getDatabase(context, scope).supabaseDao()
        )
    }
}