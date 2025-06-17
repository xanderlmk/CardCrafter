package com.belmontCrest.cardCrafter.controller.onClickActions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.EditCardViewModel
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.model.ui.Fields
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import kotlinx.coroutines.CoroutineScope
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCard
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.model.TAProp
import com.belmontCrest.cardCrafter.model.TCProp
import com.belmontCrest.cardCrafter.model.TextProps
import com.belmontCrest.cardCrafter.model.Type
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.ui.theme.mainViewModifier
import com.belmontCrest.cardCrafter.uiFunctions.CustomText
import com.belmontCrest.cardCrafter.uiFunctions.buttons.CancelButton
import com.belmontCrest.cardCrafter.uiFunctions.buttons.SubmitButton
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
                    fields.answer.value,
                    fields.isQOrA.value
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
                fields.correct.value in 'a'..'d'
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
                (fields.stringList.isEmpty() ||
                        fields.stringList.all { it.value.isNotBlank() }
                        )
            ) {
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
    newType: String
): Boolean {
    val card = ct.toCard()
    when (newType) {
        Type.BASIC -> {
            if (fields.question.value.isNotBlank() && fields.answer.value.isNotBlank()) {
                editCardVM.updateCardType(card.id, newType, fields, ct)
                return true
            } else {
                return false
            }
        }

        Type.THREE -> {
            if (fields.question.value.isNotBlank() && fields.answer.value.isNotBlank()
                && fields.middleField.value.isNotBlank()
            ) {
                editCardVM.updateCardType(card.id, newType, fields, ct)
                return true
            } else {
                return false
            }

        }

        Type.HINT -> {
            if (fields.question.value.isNotBlank() && fields.answer.value.isNotBlank()
                && fields.middleField.value.isNotBlank()
            ) {
                editCardVM.updateCardType(card.id, newType, fields, ct)
                return true
            } else {
                return false
            }
        }

        Type.MULTI -> {
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
                fields.correct.value in 'a'..'d'
            ) {
                editCardVM.updateCardType(card.id, newType, fields, ct)
                return true
            } else {
                return false
            }
        }

        Type.NOTATION -> {
            if (fields.question.value.isNotBlank() &&
                fields.answer.value.isNotBlank() &&
                (fields.stringList.isEmpty() ||
                        fields.stringList.all { it.value.isNotBlank() }
                        )
            ) {
                editCardVM.updateCardType(card.id, newType, fields, ct)
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
                SubmitButton(
                    onClick = {
                        pressed.value = false
                        fields.mainClicked.value = false
                        coroutineScope.launch {
                            navViewModel.deleteCard(card)
                            onDelete()
                        }
                    }, enabled = true, getUIStyle = getUIStyle,
                    string = stringResource(R.string.okay)
                )
            },
            dismissButton = {
                CancelButton(onClick = { pressed.value = false }, enabled = true, getUIStyle)
            }
        )
    }
}

@Composable
fun DeleteCards(
    showDialog: Boolean, onDismiss: (Boolean) -> Unit,
    onDelete: () -> Unit, getUIStyle: GetUIStyle, enabled: Boolean
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { if (enabled) onDismiss(false) },
            title = { Text(stringResource(R.string.delete_card_list)) },
            text = {
                Text(
                    text = stringResource(R.string.sure_to_delete_card_list),
                    color = getUIStyle.titleColor()
                )
            },
            confirmButton = {
                SubmitButton(
                    onClick = { onDelete() }, enabled = enabled,
                    getUIStyle, stringResource(R.string.delete)
                )
            },
            dismissButton = {
                CancelButton(onClick = { onDismiss(false) }, enabled, getUIStyle)
            }
        )
    }
}

@Composable
fun DuplicateCards(
    showDialog: Boolean, onDismiss: (Boolean) -> Unit,
    onDuplicate: () -> Unit, getUIStyle: GetUIStyle, enabled: Boolean
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { if (enabled) onDismiss(false) },
            title = { Text(stringResource(R.string.duplicate_card_list)) },
            text = {
                Text(
                    text = stringResource(R.string.sure_to_duplicate_card_list),
                    color = getUIStyle.titleColor()
                )
            },
            confirmButton = {
                SubmitButton(
                    onClick = { onDuplicate() }, enabled = enabled,
                    getUIStyle, stringResource(R.string.okay)
                )
            },
            dismissButton = {
                CancelButton(onClick = { onDismiss(false) }, enabled, getUIStyle)
            }
        )
    }
}

@Composable
fun CopyMoveCardList(
    showDialog: Boolean, onDismiss: (Boolean) -> Unit, getUIStyle: GetUIStyle,
    deckList: List<Deck>, onCopyOrMove: (Int) -> Unit, enabled: Boolean, selectedDeck: Deck?
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = { onDismiss(false) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(0.9f)
                    .background(
                        color = getUIStyle.altBackground(), shape = RoundedCornerShape(16.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(
                        vertical = 24.dp,
                        horizontal = 6.dp
                    )
                ) {
                    items(deckList) { deck ->
                        CustomText(
                            text = deck.name,
                            getUIStyle = getUIStyle,
                            modifier = Modifier
                                .padding(4.dp)
                                .mainViewModifier(getUIStyle.getColorScheme())
                                .clickable(enabled = deck.id != selectedDeck?.id) {
                                    onCopyOrMove(deck.id)
                                },
                            props = TextProps(
                                ta = TAProp.Center,
                                tc =
                                    if (deck.id != selectedDeck?.id) TCProp.Default
                                    else TCProp.Disabled
                            )
                        )
                    }
                }
                CancelButton(
                    onClick = { onDismiss(false) }, enabled = enabled, getUIStyle = getUIStyle
                )
            }
        }
    }
}