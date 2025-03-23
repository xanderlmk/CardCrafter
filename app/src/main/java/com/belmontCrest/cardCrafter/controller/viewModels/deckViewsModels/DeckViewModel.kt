package com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.model.repositories.FlashCardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class DeckViewModel(
    private val flashCardRepository: FlashCardRepository
) : ViewModel() {
    fun updateDueDate(deckId : Int, cardAmount : Int){
        viewModelScope.launch(Dispatchers.IO){
            flashCardRepository.updateNextReview(Date(), deckId).also {
                flashCardRepository.updateCardsLeft(deckId, cardAmount)
            }
        }
    }
}