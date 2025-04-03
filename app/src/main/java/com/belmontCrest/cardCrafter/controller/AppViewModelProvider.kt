package com.belmontCrest.cardCrafter.controller

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.belmontCrest.cardCrafter.controller.navigation.NavViewModel
import com.belmontCrest.cardCrafter.supabase.controller.SupabaseViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.EditingCardListViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.MainViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.CardDeckViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.AddCardViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.EditCardViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.AddDeckViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.DeckViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.EditDeckViewModel
import com.belmontCrest.cardCrafter.model.tablesAndApplication.FlashCardApplication


/**
 * Provides Factory to create instance of ViewModel for the entire  app
 */
@RequiresApi(Build.VERSION_CODES.Q)
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            NavViewModel(
                flashCardApplication().container.flashCardRepository,
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
                flashCardApplication().container.cardTypeRepository,
                flashCardApplication().container.scienceSpecificRepository
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
                flashCardApplication().container.flashCardRepository,
                flashCardApplication().container.cardTypeRepository,
                flashCardApplication().container.scienceSpecificRepository
            )
        }
        initializer {
            SupabaseViewModel(
                flashCardApplication().container.flashCardRepository,
                flashCardApplication().container.cardTypeRepository,
                flashCardApplication().container.scienceSpecificRepository,
                flashCardApplication()
            )
        }
    }
}

fun CreationExtras.flashCardApplication(): FlashCardApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FlashCardApplication)