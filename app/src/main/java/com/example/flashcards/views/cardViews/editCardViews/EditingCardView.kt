package com.example.flashcards.views.cardViews.editCardViews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.R
import com.example.flashcards.controller.BasicCardTypeHandler
import com.example.flashcards.controller.HintCardTypeHandler
import com.example.flashcards.controller.ThreeCardTypeHandler
import com.example.flashcards.controller.viewModels.BasicCardViewModel
import com.example.flashcards.controller.viewModels.CardTypeViewModel
import com.example.flashcards.controller.viewModels.HintCardViewModel
import com.example.flashcards.controller.viewModels.ThreeCardViewModel
import com.example.flashcards.model.Fields
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import com.example.flashcards.views.miscFunctions.GetModifier
import com.example.flashcards.views.miscFunctions.delayNavigate
import kotlinx.coroutines.launch

class EditingCardView(
    private var cardTypes: Triple<BasicCardViewModel,
            ThreeCardViewModel, HintCardViewModel>,
    private var cardTypeViewModel: CardTypeViewModel,
    private var getModifier: GetModifier
) {
    @Composable
    fun EditFlashCardView(
        card: Card,
        fields: Fields,
        selectedCard: MutableState<Card?>,
        onNavigateBack: () -> Unit
    ) {
        val fillOutfields = stringResource(R.string.fill_out_all_fields).toString()
        val coroutineScope = rememberCoroutineScope()
        val cardListUiState by cardTypeViewModel.cardListUiState.collectAsState()
        Box(
            modifier = getModifier.boxViewsModifier()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (selectedCard.value != null) {
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
                            null
                        }
                    }
                    cardTypeHandler?.HandleCardEdit(
                        cardListUiState = cardListUiState,
                        cardId = card.id,
                        fields = fields,
                        getModifier = getModifier
                    )
                    if (cardListUiState.errorMessage.isNotEmpty()) {
                        Text(
                            text = cardListUiState.errorMessage,
                            color = Color.Red,
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Spacer(modifier = Modifier.padding(12.dp))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    delayNavigate()
                                    onNavigateBack()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getModifier.secondaryButtonColor(),
                                contentColor = getModifier.buttonTextColor()
                            )
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val success = saveCard(selectedCard, fields, cardTypes)
                                    if (success) {
                                        delayNavigate()
                                        onNavigateBack()
                                    } else {
                                        cardTypeViewModel.setErrorMessage(fillOutfields)
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getModifier.secondaryButtonColor(),
                                contentColor = getModifier.buttonTextColor()
                            )
                        ) {
                            Text(stringResource(R.string.save))
                        }
                    }
                }
            }
        }
    }

    private fun saveCard(
        selectedCard: MutableState<Card?>,
        fields: Fields,
        cardTypes: Triple<
                BasicCardViewModel,
                ThreeCardViewModel,
                HintCardViewModel>
    ): Boolean {
        val cardId = selectedCard.value?.id
        val whatCard = mutableStateOf<Any?>(null)
        when (selectedCard.value?.type) {
            "basic" -> {
                if (fields.question.value.isNotBlank() && fields.answer.value.isNotBlank()) {
                    whatCard.value = cardTypes.first.getBasicCard(cardId ?: 0)
                    (whatCard.value as? BasicCard).let { card ->
                        card?.cardId?.let { cardId ->
                            cardTypes.first.updateBasicCard(
                                cardId,
                                fields.question.value,
                                fields.answer.value
                            )
                        }
                    }
                    return true
                } else {
                    return false
                }
            }

            "three" -> {
                if (fields.question.value.isNotBlank() && fields.answer.value.isNotBlank()
                    && fields.middleField.value.isNotBlank()
                ) {
                    whatCard.value = cardTypes.second.getThreeCard(cardId ?: 0)
                    (whatCard.value as? ThreeFieldCard).let { card ->
                        card?.cardId?.let { cardId ->
                            cardTypes.second.updateThreeCard(
                                cardId,
                                fields.question.value,
                                fields.middleField.value,
                                fields.answer.value
                            )
                        }
                    }
                    return true
                } else {
                    return false
                }

            }

            "hint" -> {
                if (fields.question.value.isNotBlank() && fields.answer.value.isNotBlank()
                    && fields.middleField.value.isNotBlank()
                ) {

                    whatCard.value = cardTypes.third.getHintCard(cardId ?: 0)
                    (whatCard.value as? HintCard).let { card ->
                        card?.cardId?.let { cardId ->
                            cardTypes.third.updateHintCard(
                                cardId,
                                fields.question.value,
                                fields.middleField.value,
                                fields.answer.value
                            )
                        }
                    }
                    return true
                } else {
                    return false
                }
            }
        }
        return false
    }

}
