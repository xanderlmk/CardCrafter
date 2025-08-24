package com.belmontCrest.cardCrafter.controller.view.models.deckViewsModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.FlashCardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class DeckViewModel(
    private val flashCardRepository: FlashCardRepository
) : ViewModel() {
    fun updateDueDate(deckId: Int, cardAmount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            flashCardRepository.updateDeckDetails(
                deckId, Date(), cardsDone = 0, cardsLeft = cardAmount
            )
        }
    }
}