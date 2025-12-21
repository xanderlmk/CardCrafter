package com.belmontCrest.cardCrafter.model.application

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.supabase.controller.view.models.SupabaseViewModel
import com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels.EditingCardListViewModel
import com.belmontCrest.cardCrafter.controller.view.models.deckViewsModels.MainViewModel
import com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels.CardDeckViewModel
import com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels.AddCardViewModel
import com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels.EditCardViewModel
import com.belmontCrest.cardCrafter.controller.view.models.deckViewsModels.AddDeckViewModel
import com.belmontCrest.cardCrafter.controller.view.models.deckViewsModels.DeckViewModel
import com.belmontCrest.cardCrafter.controller.view.models.deckViewsModels.EditDeckViewModel
import com.belmontCrest.cardCrafter.supabase.controller.view.models.CoOwnerViewModel
import com.belmontCrest.cardCrafter.supabase.controller.view.models.DeepLinksViewModel
import com.belmontCrest.cardCrafter.supabase.controller.view.models.ForgotPasswordViewModel
import com.belmontCrest.cardCrafter.supabase.controller.view.models.ImportDeckViewModel
import com.belmontCrest.cardCrafter.supabase.controller.view.models.PersonalDeckSyncViewModel
import com.belmontCrest.cardCrafter.supabase.controller.view.models.UserExportedDecksViewModel
import com.belmontCrest.cardCrafter.supabase.controller.view.models.UserProfileViewModel


/**
 * Provides Factory to create instance of ViewModel for the entire  app
 */
@RequiresApi(Build.VERSION_CODES.Q)
object AppVMProvider {
    val Factory = viewModelFactory {
        initializer {
            NavViewModel(
                flashCardApplication().container.flashCardRepository,
                flashCardApplication().container.cardTypeRepository,
                flashCardApplication().container.kbRepository,
                flashCardApplication().container.fieldParamRepo,
                flashCardApplication().container.deckContentRepository,
                flashCardApplication().container.deckListRepository,
                flashCardApplication().container.navHostRepo,
                flashCardApplication().container.authRepository,
                flashCardApplication().container.sbTablesRepository,
                flashCardApplication().container.exportRepository,
                this.createSavedStateHandle(),
                flashCardApplication()
            )
        }
        initializer { MainViewModel(flashCardApplication().container.deckListRepository) }
        initializer {
            AddDeckViewModel(flashCardApplication().container.flashCardRepository)
        }
        initializer {
            DeckViewModel(flashCardApplication().container.flashCardRepository)
        }
        initializer {
            EditDeckViewModel(flashCardApplication().container.flashCardRepository)
        }
        initializer {
            AddCardViewModel(
                flashCardApplication().container.flashCardRepository,
                flashCardApplication().container.isOwnerOrCoOwnerRepo,
                flashCardApplication().container.kbRepository,
                flashCardApplication().container.fieldParamRepo,
                this.createSavedStateHandle(),
                this.createSavedStateHandle().get<String>("deckUUID")!!
            )
        }
        initializer {
            CardDeckViewModel(flashCardApplication().container.deckContentRepository)
        }
        initializer {
            EditingCardListViewModel(
                flashCardApplication().container.cardTypeRepository,
                flashCardApplication().container.deckContentRepository
            )
        }
        initializer {
            EditCardViewModel(
                flashCardApplication().container.cardTypeRepository,
                flashCardApplication().container.kbRepository,
                flashCardApplication().container.fieldParamRepo,
                this.createSavedStateHandle(),
            )
        }
        initializer {
            SupabaseViewModel(
                flashCardApplication().container.exportRepository,
                flashCardApplication().container.sbTablesRepository,
                flashCardApplication().container.authRepository
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
                flashCardApplication().container.deckListRepository,
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
            CoOwnerViewModel(flashCardApplication().container.coOwnerRequestsRepository)
        }
        initializer {
            DeepLinksViewModel(flashCardApplication().container.deepLinkerRepo)
        }
        initializer {
            ForgotPasswordViewModel(flashCardApplication().container.fpRepository)
        }
    }
}

fun CreationExtras.flashCardApplication(): FlashCardApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FlashCardApplication)