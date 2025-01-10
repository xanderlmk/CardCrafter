package com.example.flashcards.controller.onClickActions

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import com.example.flashcards.R
import com.example.flashcards.controller.navigation.AllTypesUiStates
import com.example.flashcards.controller.viewModels.cardViewsModels.EditCardViewModel
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.ui.theme.GetModifier
import com.example.flashcards.ui.theme.deleteTextColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun saveCard(
    selectedCard: MutableState<Card?>,
    fields: Fields,
    editCardVM: EditCardViewModel,
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
                        editCardVM.updateBasicCard(
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
                        editCardVM.updateThreeCard(
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
                        editCardVM.updateHintCard(
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
                        editCardVM.updateMultiChoiceCard(
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
    editCardVM: EditCardViewModel,
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
                            editCardVM.deleteCard(card)
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