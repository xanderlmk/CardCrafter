package com.example.flashcards.controller

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flashcards.model.FlashCardApplication



/**
 * Provides Factory to create instance of ViewModel for the entire  app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            DeckViewModel(flashCardApplication().container.flashCardRepository)
        }
        initializer {
            CardViewModel(flashCardApplication().container.flashCardRepository)
        }

    }
}


fun CreationExtras.flashCardApplication(): FlashCardApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FlashCardApplication)