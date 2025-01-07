package com.example.flashcards.controller

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flashcards.controller.viewModels.BasicCardViewModel
import com.example.flashcards.controller.viewModels.EditingCardListViewModel
import com.example.flashcards.controller.viewModels.CardViewModel
import com.example.flashcards.controller.viewModels.deckViewsModels.DeckViewModel
import com.example.flashcards.controller.viewModels.CardDeckViewModel
import com.example.flashcards.controller.viewModels.HintCardViewModel
import com.example.flashcards.controller.viewModels.MultiChoiceCardViewModel
import com.example.flashcards.controller.viewModels.ThreeCardViewModel
import com.example.flashcards.model.tablesAndApplication.FlashCardApplication


/**
 * Provides Factory to create instance of ViewModel for the entire  app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            DeckViewModel(
                flashCardApplication().container.flashCardRepository
            )
        }
        initializer {
            CardViewModel(
                flashCardApplication().container.flashCardRepository
            )
        }
        initializer {
            CardDeckViewModel(
                flashCardApplication().container.flashCardRepository,
                flashCardApplication().container.cardTypeRepository
            )
        }
        initializer {
            EditingCardListViewModel(
                flashCardApplication().container.cardTypeRepository
            )
        }
        initializer {
            BasicCardViewModel(
                flashCardApplication().container.flashCardRepository,
                flashCardApplication().container.cardTypeRepository
            )
        }
        initializer {
            ThreeCardViewModel(
                flashCardApplication().container.flashCardRepository,
                flashCardApplication().container.cardTypeRepository
            )
        }
        initializer {
            HintCardViewModel(
                flashCardApplication().container.flashCardRepository,
                flashCardApplication().container.cardTypeRepository
            )
        }
        initializer {
            MultiChoiceCardViewModel(
                flashCardApplication().container.flashCardRepository,
                flashCardApplication().container.cardTypeRepository
            )
        }
    }
}


fun CreationExtras.flashCardApplication(): FlashCardApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FlashCardApplication)