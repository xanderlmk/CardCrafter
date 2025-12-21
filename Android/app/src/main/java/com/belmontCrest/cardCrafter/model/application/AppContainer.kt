package com.belmontCrest.cardCrafter.model.application

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.belmontCrest.cardCrafter.controller.view.models.deckViewsModels.TimeClass
import com.belmontCrest.cardCrafter.local.db.FlashCardDatabase
import com.belmontCrest.cardCrafter.local.db.repositories.CardTypeRepository
import com.belmontCrest.cardCrafter.local.db.repositories.DeckContentRepository
import com.belmontCrest.cardCrafter.local.db.repositories.OfflineDeckContentRepo
import com.belmontCrest.cardCrafter.local.db.repositories.DeckListRepository
import com.belmontCrest.cardCrafter.local.db.repositories.OfflineDeckListRepository
import com.belmontCrest.cardCrafter.local.db.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.local.db.repositories.OfflineCardTypeRepository
import com.belmontCrest.cardCrafter.local.db.repositories.OfflineFlashCardRepository
import com.belmontCrest.cardCrafter.navigation.FieldParamRepo
import com.belmontCrest.cardCrafter.navigation.FieldParamRepoImpl
import com.belmontCrest.cardCrafter.navigation.KeyboardSelectionRepoImpl
import com.belmontCrest.cardCrafter.navigation.KeyboardSelectionRepo
import com.belmontCrest.cardCrafter.navigation.NavHostRepo
import com.belmontCrest.cardCrafter.navigation.NavHostRepoImpl
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.CoOwnerRequestsRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.CoOwnerRequestsRepositoryImpl
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.ownerRepos.ExportRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo.AuthRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo.AuthRepositoryImpl
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.ImportRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.ImportRepositoryImpl
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.ownerRepos.OfflineExportRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.SBTableRepositoryImpl
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.SBTablesRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.OfflineSupabaseToRoomRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.personalSyncedRepos.OfflineUserSyncedInfoRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.SupabaseToRoomRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo.DeepLinkerRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo.DeepLinkerRepositoryImpl
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo.ForgotPasswordRepoImpl
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo.ForgotPasswordRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo.IsOwnerOrCoOwnerRepo
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo.IsOwnerOrCoOwnerRepoImpl
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.ownerRepos.MergeDecksRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.ownerRepos.OfflineMergeDecksRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.ownerRepos.UserExportDecksRepositoryImpl
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.ownerRepos.UserExportedDecksRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.personalSyncedRepos.UserSyncedInfoRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.personalSyncedRepos.PersonalDeckSyncRepository
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.personalSyncedRepos.PersonalDeckSyncRepositoryImpl
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.CoroutineScope

/** Creating our App Container which will get the repositories,
 * who have all our Dao interfaces, which are tied by the database.
 * And the supabase functions to get our online tables
 */
interface AppContainer {
    val flashCardRepository: FlashCardRepository
    val cardTypeRepository: CardTypeRepository
    val supabaseToRoomRepository: SupabaseToRoomRepository
    val sbTablesRepository: SBTablesRepository
    val importRepository: ImportRepository
    val authRepository: AuthRepository
    val userExportedDecksRepository: UserExportedDecksRepository
    val userSyncedInfoRepository: UserSyncedInfoRepository
    val personalDeckSyncRepository: PersonalDeckSyncRepository
    val coOwnerRequestsRepository: CoOwnerRequestsRepository
    val exportRepository: ExportRepository
    val mergeDecksRepository: MergeDecksRepository
    val isOwnerOrCoOwnerRepo: IsOwnerOrCoOwnerRepo
    val deepLinkerRepo: DeepLinkerRepository
    val fpRepository: ForgotPasswordRepository
    val kbRepository: KeyboardSelectionRepo
    val fieldParamRepo: FieldParamRepo
    val deckListRepository: DeckListRepository
    val deckContentRepository: DeckContentRepository
    val navHostRepo : NavHostRepo
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppDataContainer(
    private val context: Context,
    scope: CoroutineScope,
    supabase: SupabaseClient,
) : AppContainer {

    private val timeClass = TimeClass()
    private val sharedPrefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    override val flashCardRepository: FlashCardRepository by lazy {
        OfflineFlashCardRepository(
            FlashCardDatabase.getDatabase(context, scope).deckDao(),
            FlashCardDatabase.getDatabase(context, scope).cardDao()
        )
    }
    override val cardTypeRepository: CardTypeRepository by lazy {
        OfflineCardTypeRepository(
            FlashCardDatabase.getDatabase(context, scope).cardTypes(),
        )
    }
    override val supabaseToRoomRepository: SupabaseToRoomRepository by lazy {
        OfflineSupabaseToRoomRepository(
            FlashCardDatabase.getDatabase(context, scope).importFromSBDao()
        )
    }
    override val importRepository: ImportRepository by lazy {
        ImportRepositoryImpl(supabase)
    }
    override val sbTablesRepository: SBTablesRepository by lazy {
        SBTableRepositoryImpl(supabase)
    }
    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(supabase)
    }
    override val userExportedDecksRepository: UserExportedDecksRepository by lazy {
        UserExportDecksRepositoryImpl(supabase)
    }

    override val userSyncedInfoRepository: UserSyncedInfoRepository by lazy {
        OfflineUserSyncedInfoRepository(
            FlashCardDatabase.getDatabase(context, scope).syncedDeckInfoDao()
        )
    }
    override val personalDeckSyncRepository: PersonalDeckSyncRepository by lazy {
        PersonalDeckSyncRepositoryImpl(supabase)
    }
    override val coOwnerRequestsRepository: CoOwnerRequestsRepository by lazy {
        CoOwnerRequestsRepositoryImpl(supabase)
    }
    override val exportRepository: ExportRepository by lazy {
        OfflineExportRepository(
            FlashCardDatabase.getDatabase(context, scope).exportToSBDao()
        )
    }

    override val mergeDecksRepository: MergeDecksRepository by lazy {
        OfflineMergeDecksRepository(
            FlashCardDatabase.getDatabase(context, scope).mergeDecksDao()
        )
    }

    override val isOwnerOrCoOwnerRepo: IsOwnerOrCoOwnerRepo by lazy {
        IsOwnerOrCoOwnerRepoImpl(supabase)
    }
    override val deepLinkerRepo: DeepLinkerRepository by lazy {
        DeepLinkerRepositoryImpl(supabase)
    }

    override val fpRepository: ForgotPasswordRepository by lazy {
        ForgotPasswordRepoImpl(supabase)
    }

    override val kbRepository: KeyboardSelectionRepo by lazy {
        KeyboardSelectionRepoImpl(context, scope)
    }
    override val fieldParamRepo: FieldParamRepo by lazy {
        FieldParamRepoImpl()
    }
    override val deckListRepository: DeckListRepository by lazy {
        OfflineDeckListRepository(
            timeClass, sharedPrefs,
            FlashCardDatabase.getDatabase(context, scope).deckDao()
        )
    }
    override val deckContentRepository: DeckContentRepository by lazy {
        OfflineDeckContentRepo(
            FlashCardDatabase.getDatabase(context, scope).cardTypes(),
            FlashCardDatabase.getDatabase(context, scope).deckDao(),
            FlashCardDatabase.getDatabase(context, scope).savedCardDao(),
            FlashCardDatabase.getDatabase(context, scope).dueCardsDao(),
            sharedPrefs
        )
    }
    override val navHostRepo: NavHostRepo by lazy { NavHostRepoImpl() }
}