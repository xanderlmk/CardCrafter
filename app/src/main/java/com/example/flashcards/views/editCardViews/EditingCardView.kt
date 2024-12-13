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
import com.example.flashcards.controller.BasicCardTypeHandler
import com.example.flashcards.controller.CardTypeHandler
import com.example.flashcards.controller.HintCardTypeHandler
import com.example.flashcards.controller.NullType
import com.example.flashcards.controller.ThreeCardTypeHandler
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
                val cardTypeHandler = when (selectedCard.value?.type) {
                    "basic" -> {
                        BasicCardTypeHandler()
                    }
                    "three" -> {
                        ThreeCardTypeHandler()
                    }
                    "hint" -> {
                        HintCardTypeHandler()
                    }
                    else -> {
                        NullType()
                    }
                }
                cardTypeHandler.HandleCardEdit(
                    cardListUiState = cardListUiState,
                cardId = card.id,
                onDismiss = onDismiss,
                cardTypes = cardTypes,
                fields = fields
                )
            }
        }
    }
}