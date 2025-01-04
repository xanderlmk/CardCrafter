package com.example.flashcards.controller

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.flashcards.R
import com.example.flashcards.controller.navigation.AllTypesUiStates
import com.example.flashcards.controller.navigation.AllViewModels
import com.example.flashcards.controller.viewModels.CardViewModel
import com.example.flashcards.controller.viewModels.DeckViewModel
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.ui.theme.deleteTextColor
import com.example.flashcards.ui.theme.GetModifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun updateReviewAmount(
    viewModel: DeckViewModel, newReviewAmount : Int,
    errorMessage: MutableState<String>,
    isSubmitting: MutableState<Boolean>, deck: Deck,
    successful: MutableState<String>,
    errorMessages : List<String>, successMessage : String,
    coroutineScope: CoroutineScope
) {

    coroutineScope.launch {
        if (newReviewAmount <= 0) {
            errorMessage.value = errorMessages[0]
            return@launch
        }
        if (newReviewAmount >= 10) {
            errorMessage.value = errorMessages[1]
            return@launch
        }

        if (newReviewAmount == deck.reviewAmount) {
            errorMessage.value = errorMessages[2]
            return@launch
        }

        isSubmitting.value = true
        try {
            val result =
                viewModel.updateReviewAmount(newReviewAmount, deck.id)
            if (result > 0 ) {
                successful.value = successMessage
            } else {
                errorMessage.value =
                    errorMessages[3]
            }
        } catch (e: Exception) {
            errorMessage.value =
                e.message ?: R.string.error_occurred.toString()
        } finally {
            isSubmitting.value = false
        }
    }
}

fun updateMultipliers(
    viewModel: DeckViewModel, newGoodMultiplier: Double,
    newBadMultiplier: Double, errorMessage: MutableState<String>,
    isSubmitting: MutableState<Boolean>, deck: Deck,
    successful: MutableState<String>,
    errorMessages: List<String>, successMessage : String,
    coroutineScope: CoroutineScope
) {

    coroutineScope.launch {
        if (newGoodMultiplier <= 1.0) {
            errorMessage.value = errorMessages[0]
            return@launch
        }
        if (newBadMultiplier >= 1.0 || newBadMultiplier < 0.0) {
            errorMessage.value = errorMessages[1]
            return@launch
        }
        if (newGoodMultiplier == deck.goodMultiplier &&
            newBadMultiplier == deck.badMultiplier
        ) {
            errorMessage.value = errorMessages[2]
            return@launch
        }
        isSubmitting.value = true
        try {
            val result1 =
                viewModel.updateDeckGoodMultiplier(newGoodMultiplier, deck.id)
            val result2 =
                viewModel.updateDeckBadMultiplier(newBadMultiplier, deck.id)
            if (result1 > 0 && result2 > 0) {
                successful.value = successMessage
            } else {
                errorMessage.value = errorMessages[3]
            }
        } catch (e: Exception) {
            errorMessage.value =
                e.message ?: R.string.error_occurred.toString()
        } finally {
            isSubmitting.value = false
        }
    }
}

fun updateDeckName(
    viewModel: DeckViewModel, newDeckName: String,
    errorMessage: MutableState<String>,
    deckErrors: List<String>, currentName: String,
    isSubmitting: MutableState<Boolean>, deck: Deck,
    onNavigate: () -> Unit,
    coroutineScope: CoroutineScope
) {
    coroutineScope.launch {
        if (newDeckName.isBlank()) {
            errorMessage.value = deckErrors[0]
            return@launch
        }

        if (newDeckName == currentName) {
            onNavigate()
            return@launch
        }

        isSubmitting.value = true
        try {
            // First check if the deck name exists
            val exists = viewModel.checkIfDeckExists(newDeckName)
            if (exists > 0) {
                errorMessage.value = deckErrors[1]
                return@launch
            }

            val result =
                viewModel.updateDeckName(newDeckName, deck.id)
            if (result > 0) {
                onNavigate()
            } else {
                errorMessage.value = deckErrors[2]
            }
        } catch (e: Exception) {
            errorMessage.value =
                e.message ?: R.string.error_occurred.toString()
        } finally {
            isSubmitting.value = false
        }
    }
}

@Composable
fun DeleteDeck(
    viewModel: DeckViewModel,
    deck: Deck,
    getModifier: GetModifier,
    coroutineScope: CoroutineScope,
    fields: Fields,
    onDelete: () -> Unit
) {
    var showConfirmationDialog by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = {
                showConfirmationDialog = true // Show confirmation dialog
            },
            modifier = Modifier
                .fillMaxWidth(fraction = 0.55f)
                .align(Alignment.Center),
            colors = ButtonDefaults.buttonColors(
                containerColor = getModifier.secondaryButtonColor(),
                contentColor = deleteTextColor
            )
        ) {
            Text(
                text = stringResource(R.string.delete_deck),
                textAlign = TextAlign.Center
            )
        }

        if (showConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                title = {
                    Text(
                        stringResource(R.string.delete_deck),
                        color = getModifier.titleColor()
                    )
                },
                text = { Text(
                    text = stringResource(R.string.sure_to_delete_deck),
                    color = getModifier.titleColor()) },
                confirmButton = {
                    Button(
                        onClick = {
                            showConfirmationDialog = false
                            fields.mainClicked.value = false
                            coroutineScope.launch {
                                viewModel.deleteDeck(deck)
                                onDelete()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getModifier.secondaryButtonColor(),
                            contentColor = deleteTextColor
                        )
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showConfirmationDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getModifier.secondaryButtonColor(),
                            contentColor = getModifier.buttonTextColor()
                        )
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

fun saveCard(
    selectedCard: MutableState<Card?>,
    fields: Fields,
    cardTypes: AllViewModels,
    typesUiStates: AllTypesUiStates
): Boolean {
    val cardId = selectedCard.value?.id
    when (selectedCard.value?.type) {
        "basic" -> {
            if (fields.question.value.isNotBlank() && fields.answer.value.isNotBlank()) {
                val basicCard =
                    typesUiStates.basicCardUiState.basicCards.find {
                        it.card.id == cardId
                    }?.basicCard
                basicCard.let { card ->
                    card?.cardId?.let { cardId ->
                        cardTypes.basicCardViewModel.updateBasicCard(
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
                val threeCard =
                    typesUiStates.threeCardUiState.threeFieldCards.find {
                        it.card.id == cardId
                    }?.threeFieldCard
                threeCard.let { card ->
                    card?.cardId?.let { cardId ->
                        cardTypes.threeCardViewModel.updateThreeCard(
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
                val hintCard =
                    typesUiStates.hintUiStates.hintCards.find {
                        it.card.id == cardId
                    }?.hintCard
                hintCard.let { card ->
                    card?.cardId?.let { cardId ->
                        cardTypes.hintCardViewModel.updateHintCard(
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

        "multi" -> {
            if (
                fields.question.value.isNotBlank() &&
                fields.choices[0].value.isNotBlank() &&
                fields.choices[1].value.isNotBlank() &&
                !(fields.choices[2].value.isBlank() &&
                        fields.choices[3].value.isNotBlank()) &&
                !((fields.choices[2].value.isBlank() &&
                        fields.correct.value == 'c') ||
                        (fields.choices[3].value.isBlank() &&
                                fields.correct.value == 'd')
                        )
            ) {
                val choiceCard =
                    typesUiStates.multiChoiceUiCardState.multiChoiceCard.find {
                        it.card.id == cardId
                    }?.multiChoiceCard
                choiceCard.let { card ->
                    card?.cardId?.let { cardId ->
                        cardTypes.multiChoiceCardViewModel.updateMultiChoiceCard(
                            cardId,
                            fields.question.value,
                            fields.choices[0].value,
                            fields.choices[1].value,
                            fields.choices[2].value,
                            fields.choices[3].value,
                            fields.correct.value
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

@Composable
fun DeleteCard(
    cardViewModel: CardViewModel,
    coroutineScope: CoroutineScope,
    card: Card,
    fields: Fields,
    pressed: MutableState<Boolean>,
    onDelete: () -> Unit,
    getModifier: GetModifier
) {
    if (pressed.value) {
        AlertDialog(
            onDismissRequest = { pressed.value = false },
            title = { Text(stringResource(R.string.delete_card)) },
            text = {
                Text(
                    text = stringResource(R.string.sure_to_delete_card),
                    color = getModifier.titleColor()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        pressed.value = false
                        fields.mainClicked.value = false
                        coroutineScope.launch {
                            cardViewModel.deleteCard(card)
                            onDelete()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getModifier.secondaryButtonColor(),
                        contentColor = deleteTextColor
                    )
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = { pressed.value = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getModifier.secondaryButtonColor(),
                        contentColor = getModifier.buttonTextColor()
                    )
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}