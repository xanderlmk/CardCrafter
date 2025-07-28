package com.belmontCrest.cardCrafter.model.application

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.belmontCrest.cardCrafter.localDatabase.database.FlashCardDatabase
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.CardTypeRepository
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.OfflineCardTypeRepository
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.OfflineFlashCardRepository
import com.belmontCrest.cardCrafter.navigation.FieldParamRepository
import com.belmontCrest.cardCrafter.navigation.FieldParamRepositoryImpl
import com.belmontCrest.cardCrafter.navigation.KeyboardSelectionRepoImpl
import com.belmontCrest.cardCrafter.navigation.KeyboardSelectionRepository
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
    val kbRepository: KeyboardSelectionRepository
    val fieldParamRepository : FieldParamRepository
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppDataContainer(
    private val context: Context,
    scope: CoroutineScope,
    sharedSupabase: SupabaseClient,
    syncedSupabase: SupabaseClient
) : AppContainer {

    override val flashCardRepository: FlashCardRepository by lazy {
        OfflineFlashCardRepository(
            FlashCardDatabase.getDatabase(context, scope).deckDao(),
            FlashCardDatabase.getDatabase(context, scope).cardDao(),
            FlashCardDatabase.getDatabase(context, scope).savedCardDao(),
            context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
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
        ImportRepositoryImpl(sharedSupabase)
    }
    override val sbTablesRepository: SBTablesRepository by lazy {
        SBTableRepositoryImpl(sharedSupabase, syncedSupabase = syncedSupabase)
    }
    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            sharedSupabase, syncedSupabase,
            FlashCardDatabase.getDatabase(context, scope).pwdDao()
        )
    }
    override val userExportedDecksRepository: UserExportedDecksRepository by lazy {
        UserExportDecksRepositoryImpl(sharedSupabase)
    }

    override val userSyncedInfoRepository: UserSyncedInfoRepository by lazy {
        OfflineUserSyncedInfoRepository(
            FlashCardDatabase.getDatabase(context, scope).syncedDeckInfoDao()
        )
    }
    override val personalDeckSyncRepository: PersonalDeckSyncRepository by lazy {
        PersonalDeckSyncRepositoryImpl(syncedSupabase)
    }
    override val coOwnerRequestsRepository: CoOwnerRequestsRepository by lazy {
        CoOwnerRequestsRepositoryImpl(sharedSupabase)
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
        IsOwnerOrCoOwnerRepoImpl(sharedSupabase)
    }
    override val deepLinkerRepo: DeepLinkerRepository by lazy {
        DeepLinkerRepositoryImpl(sharedSupabase)
    }

    override val fpRepository: ForgotPasswordRepository by lazy {
        ForgotPasswordRepoImpl(sharedSupabase)
    }

    override val kbRepository: KeyboardSelectionRepository by lazy {
        KeyboardSelectionRepoImpl(context, scope)
    }
    override val fieldParamRepository: FieldParamRepository by lazy {
        FieldParamRepositoryImpl()
    }
}