package com.example.flashcards.views.editCardViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.flashcards.controller.viewModels.BasicCardViewModel
import com.example.flashcards.controller.viewModels.CardListUiState
import com.example.flashcards.controller.viewModels.HintCardViewModel
import com.example.flashcards.controller.viewModels.ThreeCardViewModel
import com.example.flashcards.model.Fields
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.ui.theme.backgroundColor
import com.example.flashcards.views.miscFunctions.LoadingText

class EditingCardView(
    private var cardTypes : Triple<BasicCardViewModel,
            ThreeCardViewModel, HintCardViewModel>,
    ) {
    @Composable
    fun EditFlashCardView(
        card: Card,
        fields: Fields,
        cardListUiState: CardListUiState,
        selectedCard : MutableState<Card?>,
        onDismiss: () -> Unit) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(backgroundColor)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(50.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedCard.value?.type) {
                    "basic" -> {
                        val cardType =
                            cardListUiState.allCards.find {
                                it.basicCard?.cardId == card.id
                            }
                        cardType?.basicCard?.let { basicCard ->
                            EditBasicCard(basicCard, onDismiss, cardTypes.first, fields)
                        }
                    }

                    "three" -> {
                        val cardType =
                            cardListUiState.allCards.find {
                                it.threeFieldCard?.cardId == card.id
                            }
                        cardType?.threeFieldCard?.let { threeCard ->
                            EditThreeCard(threeCard, onDismiss, cardTypes.second, fields)
                        }
                    }

                    "hint" -> {
                        val cardType =
                            cardListUiState.allCards.find {
                                it.hintCard?.cardId == card.id
                            }
                        cardType?.hintCard?.let { hintCard ->
                            EditHintCard(hintCard, onDismiss, cardTypes.third, fields)
                        }
                    }
                    else -> {
                        Box(modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center) {
                            LoadingText()
                        }
                    }
                }
            }
        }
    }
}