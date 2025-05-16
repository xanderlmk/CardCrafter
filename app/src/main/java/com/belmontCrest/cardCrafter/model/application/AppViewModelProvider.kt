package com.belmontCrest.cardCrafter.model.application

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.EditingCardListViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.MainViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.CardDeckViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.AddCardViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.EditCardViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.AddDeckViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.DeckViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.EditDeckViewModel
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.CoOwnerViewModel
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.DeepLinksViewModel
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.ForgotPasswordViewModel
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.ImportDeckViewModel
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.PersonalDeckSyncViewModel
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.UserExportedDecksViewModel
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.UserProfileViewModel


/**
 * Provides Factory to create instance of ViewModel for the entire  app
 */
@RequiresApi(Build.VERSION_CODES.Q)
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            NavViewModel(
                flashCardApplication().container.flashCardRepository,
                flashCardApplication().container.cardTypeRepository,
                this.createSavedStateHandle()
            )
        }
        initializer {
            MainViewModel(
                flashCardApplication().container.flashCardRepository,
                this.createSavedStateHandle()
            )
        }
        initializer {
            AddDeckViewModel(
                flashCardApplication().container.flashCardRepository,
            )
        }
        initializer {
            DeckViewModel(
                flashCardApplication().container.flashCardRepository
            )
        }
        initializer {
            EditDeckViewModel(
                flashCardApplication().container.flashCardRepository,
            )
        }
        initializer {
            AddCardViewModel(
                flashCardApplication().container.flashCardRepository,
                flashCardApplication().container.isOwnerOrCoOwnerRepo,
                this.createSavedStateHandle().get<String>("deckUUID")!!
            )
        }
        initializer {
            CardDeckViewModel(
                flashCardApplication().container.flashCardRepository,
                flashCardApplication().container.cardTypeRepository,
                this.createSavedStateHandle()
            )
        }
        initializer {
            EditingCardListViewModel(
                flashCardApplication().container.cardTypeRepository,
                this.createSavedStateHandle()
            )
        }
        initializer {
            EditCardViewModel(
                flashCardApplication().container.cardTypeRepository,
                flashCardApplication().container.scienceSpecificRepository
            )
        }
        initializer {
            SupabaseViewModel(
                flashCardApplication().container.exportRepository,
                flashCardApplication().container.sbTablesRepository,
                flashCardApplication().container.authRepository,
                flashCardApplication()
            )
        }
        initializer {
            ImportDeckViewModel(
                flashCardApplication().container.flashCardRepository,
                flashCardApplication().container.supabaseToRoomRepository,
                flashCardApplication().container.importRepository,
                this.createSavedStateHandle().get<String>("uuid")!!
            )
        }
        initializer {
            UserProfileViewModel(
                flashCardApplication().container.authRepository
            )
        }
        initializer {
            UserExportedDecksViewModel(
                flashCardApplication().container.sbTablesRepository,
                flashCardApplication().container.userExportedDecksRepository,
                flashCardApplication().container.mergeDecksRepository,
                this.createSavedStateHandle()
            )
        }
        initializer {
            PersonalDeckSyncViewModel(
                flashCardApplication().container.personalDeckSyncRepository,
                flashCardApplication().container.userSyncedInfoRepository
            )
        }
        initializer {
            CoOwnerViewModel(
                flashCardApplication().container.coOwnerRequestsRepository
            )
        }
        initializer {
            DeepLinksViewModel(
                flashCardApplication().container.deepLinkerRepo
            )
        }
        initializer {
            ForgotPasswordViewModel(
                flashCardApplication().container.fpRepository
            )
        }
    }
}

fun CreationExtras.flashCardApplication(): FlashCardApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FlashCardApplication)