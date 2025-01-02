package com.example.flashcards.views.cardViews.editCardViews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.R
import com.example.flashcards.controller.BasicCardTypeHandler
import com.example.flashcards.controller.ChoiceCardTypeHandler
import com.example.flashcards.controller.HintCardTypeHandler
import com.example.flashcards.controller.ThreeCardTypeHandler
import com.example.flashcards.controller.navigation.AllTypesUiStates
import com.example.flashcards.controller.navigation.AllViewModels
import com.example.flashcards.controller.saveCard
import com.example.flashcards.controller.viewModels.CardTypeViewModel
import com.example.flashcards.controller.viewModels.CardViewModel
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.ui.theme.GetModifier
import com.example.flashcards.views.miscFunctions.DeleteCardButton
import com.example.flashcards.views.miscFunctions.delayNavigate
import kotlinx.coroutines.launch

class EditingCardView(
    private var cardViewModel: CardViewModel,
    private var cardTypes: AllViewModels,
    private var cardTypeViewModel: CardTypeViewModel,
    private var allTypesUiStates: AllTypesUiStates,
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
                    .padding(10.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(R.string.edit_flashcard),
                        fontSize = 35.sp,
                        lineHeight = 40.sp,
                        textAlign = TextAlign.Center,
                        color = getModifier.titleColor(),
                        modifier = getModifier.editCardModifier()
                    )
                    DeleteCardButton(
                        cardViewModel, getModifier, card, fields, onNavigateBack,
                        Modifier.align(Alignment.TopEnd)
                    )
                }
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

                        "multi" -> {
                            ChoiceCardTypeHandler()
                        }

                        else -> {
                            null
                        }
                    }
                    cardTypeHandler?.HandleCardEdit(
                        state = allTypesUiStates,
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
                                    val success = saveCard(
                                        selectedCard, fields, cardTypes,
                                        allTypesUiStates
                                    )
                                    if (success) {
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
}
