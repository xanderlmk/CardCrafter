package com.belmontCrest.cardCrafter.controller.onClickActions

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.EditCardViewModel
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.deleteTextColor
import kotlinx.coroutines.CoroutineScope
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCard
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import kotlinx.coroutines.launch

fun saveCard(
    fields: Fields,
    editCardVM: EditCardViewModel,
    ct: CT
): Boolean {
    when (ct) {
        is CT.Basic -> {
            if (fields.question.value.isNotBlank() && fields.answer.value.isNotBlank()) {
                editCardVM.updateBasicCard(
                    ct.card.id,
                    fields.question.value,
                    fields.answer.value
                )
                return true
            } else {
                return false
            }
        }

        is CT.ThreeField -> {
            if (fields.question.value.isNotBlank() && fields.answer.value.isNotBlank()
                && fields.middleField.value.isNotBlank()
            ) {
                editCardVM.updateThreeCard(
                    ct.card.id,
                    fields.question.value,
                    fields.middleField.value,
                    fields.answer.value
                )
                return true
            } else {
                return false
            }

        }

        is CT.Hint -> {
            if (fields.question.value.isNotBlank() && fields.answer.value.isNotBlank()
                && fields.middleField.value.isNotBlank()
            ) {
                editCardVM.updateHintCard(
                    ct.card.id,
                    fields.question.value,
                    fields.middleField.value,
                    fields.answer.value
                )
                return true
            } else {
                return false
            }
        }
        is CT.MultiChoice -> {
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
                        ) &&
                fields.correct.value in 'a' .. 'd'
            ) {
                editCardVM.updateMultiChoiceCard(
                    ct.card.id,
                    fields.question.value,
                    fields.choices[0].value,
                    fields.choices[1].value,
                    fields.choices[2].value,
                    fields.choices[3].value,
                    fields.correct.value
                )
                return true
            } else {
                return false
            }
        }
        is CT.Notation -> {
            if (fields.question.value.isNotBlank() &&
                fields.answer.value.isNotBlank() &&
                ( fields.stringList.isEmpty() ||
                        fields.stringList.all { it.value.isNotBlank() }
                        )) {
                editCardVM.updateNotationCard(
                    ct.card.id,
                    fields.question.value,
                    fields.stringList.map {
                        it.value
                    },
                    fields.answer.value
                )
                return true
            } else {
                return false
            }
        }
    }
    return false
}


suspend fun updateCardType(
    fields: Fields,
    editCardVM: EditCardViewModel,
    ct: CT,
    newType : String
): Boolean {
    val card = ct.toCard()
    when (newType) {
        "basic" -> {
            if (fields.question.value.isNotBlank() && fields.answer.value.isNotBlank()) {
                editCardVM.updateCardType(card.id,newType,fields,ct)
                return true
            } else {
                return false
            }
        }
        "three" -> {
            if (fields.question.value.isNotBlank() && fields.answer.value.isNotBlank()
                && fields.middleField.value.isNotBlank()
            ) {
                editCardVM.updateCardType(card.id,newType,fields,ct)
                return true
            } else {
                return false
            }

        }
        "hint" -> {
            if (fields.question.value.isNotBlank() && fields.answer.value.isNotBlank()
                && fields.middleField.value.isNotBlank()
            ) {
                editCardVM.updateCardType(card.id,newType,fields,ct)
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
                        ) &&
                fields.correct.value in 'a' .. 'd'
            ) {
                editCardVM.updateCardType(card.id,newType,fields,ct)
                return true
            } else {
                return false
            }
        }
        "notation" -> {
            if (fields.question.value.isNotBlank() &&
                fields.answer.value.isNotBlank() &&
                ( fields.stringList.isEmpty() ||
                        fields.stringList.all { it.value.isNotBlank() }
                        )) {
                editCardVM.updateCardType(card.id,newType,fields,ct)
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
    navViewModel: NavViewModel,
    coroutineScope: CoroutineScope,
    card: Card,
    fields: Fields,
    pressed: MutableState<Boolean>,
    onDelete: () -> Unit,
    getUIStyle: GetUIStyle
) {
    if (pressed.value) {
        AlertDialog(
            onDismissRequest = { pressed.value = false },
            title = { Text(stringResource(R.string.delete_card)) },
            text = {
                Text(
                    text = stringResource(R.string.sure_to_delete_card),
                    color = getUIStyle.titleColor()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        pressed.value = false
                        fields.mainClicked.value = false
                        coroutineScope.launch {
                            navViewModel.deleteCard(card)
                            onDelete()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getUIStyle.secondaryButtonColor(),
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
                        containerColor = getUIStyle.secondaryButtonColor(),
                        contentColor = getUIStyle.buttonTextColor()
                    )
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}