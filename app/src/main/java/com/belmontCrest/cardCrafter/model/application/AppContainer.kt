package com.belmontCrest.cardCrafter.model.application

import android.content.Context
import com.belmontCrest.cardCrafter.localDatabase.database.FlashCardDatabase
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.CardTypeRepository
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.OfflineCardTypeRepository
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.OfflineFlashCardRepository
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.OfflineScienceRepository
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.ScienceSpecificRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repository.AuthRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repository.AuthRepositoryImpl
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repository.SBTableRepositoryImpl
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repository.SBTablesRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repository.OfflineSupabaseToRoomRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repository.SupabaseToRoomRepository
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.CoroutineScope

/** Creating our App Container which will get the repositories,
 * who have all our Dao interfaces, which are tied by the database.
 * And the supabase functions to get our online tables
 */
interface AppContainer {
    val flashCardRepository: FlashCardRepository
    val cardTypeRepository: CardTypeRepository
    val scienceSpecificRepository: ScienceSpecificRepository
    val supabaseToRoomRepository: SupabaseToRoomRepository
    val sbTablesRepository: SBTablesRepository
    val authRepository: AuthRepository
    val supabase: SupabaseClient
}

class AppDataContainer(
    private val context: Context,
    scope: CoroutineScope,
    supabaseClient: SupabaseClient
) : AppContainer {
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
    override val supabaseToRoomRepository: SupabaseToRoomRepository by lazy {
        OfflineSupabaseToRoomRepository(
            FlashCardDatabase.Companion.getDatabase(context, scope).supabaseDao()
        )
    }
    override val sbTablesRepository: SBTablesRepository by lazy {
        SBTableRepositoryImpl(supabaseClient)
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(supabaseClient)
    }
    override val supabase: SupabaseClient by lazy { supabaseClient }
}