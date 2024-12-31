package com.example.flashcards.views.miscFunctions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.flashcards.model.uiModels.View
import com.example.flashcards.model.tablesAndApplication.Deck

class ChoosingView() {
    @Composable
    fun WhichScreen(
        deck: Deck,
        view: View,
        goToAddCard : (Int) -> Unit,
        goToViewCard: (Int) -> Unit,
        goToEditDeck: (Int,String) -> Unit,
        goToViewCards: (Int) -> Unit
    ) {
        LaunchedEffect(Unit) {
        if (!view.onView.value) {
                when (view.whichView.intValue) {
                    1 -> {
                        // Navigate to AddCard screen
                            goToAddCard(deck.id)
                            view.whichView.intValue = 0
                            view.onView.value = true
                    }
                    2 -> {
                        // Navigate to ViewCard screen
                            delayNavigate()
                            goToViewCard(deck.id)
                            view.whichView.intValue = 0
                            view.onView.value = true
                    }
                    3 -> {
                        // Navigate to EditDeckScreen
                        delayNavigate()
                        goToEditDeck(deck.id, deck.name)
                        view.whichView.intValue = 0
                        view.onView.value = true
                    }
                    4 -> {
                        // Navigate to ViewFlashCardList
                        delayNavigate()
                        goToViewCards(deck.id)
                        view.whichView.intValue = 0
                        view.onView.value = true

                    }
                }
            }
        }
    }
}

