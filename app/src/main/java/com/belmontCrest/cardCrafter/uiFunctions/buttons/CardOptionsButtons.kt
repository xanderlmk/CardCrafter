package com.belmontCrest.cardCrafter.uiFunctions.buttons

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.controller.onClickActions.CopyMoveCardList
import com.belmontCrest.cardCrafter.controller.onClickActions.DeleteCard
import com.belmontCrest.cardCrafter.controller.onClickActions.DeleteCards
import com.belmontCrest.cardCrafter.controller.onClickActions.DuplicateCards
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.model.Type
import com.belmontCrest.cardCrafter.model.ui.Decision
import com.belmontCrest.cardCrafter.model.ui.Dialogs
import com.belmontCrest.cardCrafter.model.ui.Fields
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import kotlinx.coroutines.delay

@Composable
fun CardOptionsButton(
    navVM: NavViewModel,
    getUIStyle: GetUIStyle, card: Card,
    fields: Fields,
    expanded: MutableState<Boolean>,
    modifier: Modifier,
    onDelete: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val showDialog = remember { mutableStateOf(false) }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ToggleKeyBoard(navVM, getUIStyle, fields.newType.value)
        Box(
            modifier = modifier
                .wrapContentSize(Alignment.TopEnd)
        ) {
            IconButton(
                onClick = { expanded.value = true },
                modifier = Modifier
                    .size(54.dp)
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Card Type",
                    tint = getUIStyle.titleColor()
                )
            }
            DropdownMenu(
                expanded = expanded.value, onDismissRequest = { expanded.value = false }
            ) {
                CardItems(
                    toBasic = {
                        resetKeyboardStuff(navVM, fields.newType.value)
                        fields.newType.value = Type.BASIC
                    },
                    toThree = {
                        resetKeyboardStuff(navVM, fields.newType.value)
                        fields.newType.value = Type.THREE
                    },
                    toHint = {
                        resetKeyboardStuff(navVM, fields.newType.value)
                        fields.newType.value = Type.HINT
                    },
                    toMulti = {
                        resetKeyboardStuff(navVM, fields.newType.value)
                        fields.newType.value = Type.MULTI
                    },
                    toNotation = { fields.newType.value = Type.NOTATION }
                )
                HorizontalDivider()
                DropdownMenuItem(
                    onClick = {
                        showDialog.value = true
                    },
                    text = { Text(stringResource(R.string.delete_card)) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            modifier = Modifier
                                .size(28.dp),
                            contentDescription = "Delete Card",
                            tint = getUIStyle.iconColor()
                        )
                    }
                )
            }
            DeleteCard(
                navVM, coroutineScope,
                card, fields, showDialog,
                onDelete, getUIStyle
            )
        }
    }
}

private fun resetKeyboardStuff(navVM: NavViewModel, type: String) {
    if (type == Type.NOTATION) {
        navVM.resetKeyboardStuff()
    }
}

@Composable
fun CardTypesButton(getUIStyle: GetUIStyle, navVM: NavViewModel) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val type by navVM.type.collectAsState()
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ToggleKeyBoard(navVM, getUIStyle, type)
        Box(
            Modifier
                .wrapContentSize(Alignment.TopEnd)
        ) {
            IconButton(
                onClick = { expanded = true },
                modifier = Modifier
                    .padding(4.dp)
                    .size(54.dp)
            ) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Card Type",
                    tint = getUIStyle.defaultIconColor()
                )
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                CardItems(
                    toBasic = { navVM.updateType(Type.BASIC); navVM.resetKeyboardStuff() },
                    toThree = { navVM.updateType(Type.THREE); navVM.resetKeyboardStuff() },
                    toHint = { navVM.updateType(Type.HINT); navVM.resetKeyboardStuff() },
                    toMulti = { navVM.updateType(Type.MULTI); navVM.resetKeyboardStuff() },
                    toNotation = { navVM.updateType(Type.NOTATION) }
                )
            }
        }
    }
}

@Composable
fun CardItems(
    toBasic: () -> Unit, toThree: () -> Unit, toHint: () -> Unit,
    toMulti: () -> Unit, toNotation: () -> Unit
) {
    DropdownMenuItem(
        onClick = { toBasic() },
        text = { Text(stringResource(R.string.basic_card)) }
    )
    DropdownMenuItem(
        onClick = { toThree() },
        text = { Text(stringResource(R.string.three_field_card)) }
    )
    DropdownMenuItem(
        onClick = { toHint() },
        text = { Text(stringResource(R.string.hint_card)) }
    )
    DropdownMenuItem(
        onClick = { toMulti() },
        text = { Text(stringResource(R.string.multi_choice_card)) }
    )
    DropdownMenuItem(
        onClick = { toNotation() },
        text = { Text("Notation Card") }
    )
}

@Composable
fun ToggleKeyBoard(
    navVM: NavViewModel, getUIStyle: GetUIStyle, type: String
) {
    val showKB by navVM.showKatexKeyboard.collectAsStateWithLifecycle()
    val selectedKB by navVM.selectedKB.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        navVM.retrieveKB()
        delay(1400)
        Log.d("card types", "type: $type\nshowKB: $showKB\nselectedKB: $selectedKB\n")
    }
    if (type == Type.NOTATION && selectedKB != null) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { navVM.toggleKeyboard() },
                        onLongPress = { navVM.resetOffset() }
                    )
                }
        ) {
            if (!showKB) {
                Icon(
                    painterResource(R.drawable.twotone_keyboard),
                    contentDescription = "Keyboard",
                    tint = getUIStyle.defaultIconColor()
                )
            } else {
                Icon(
                    painterResource(R.drawable.twotone_keyboard_hide),
                    contentDescription = "Hide Keyboard",
                    tint = getUIStyle.defaultIconColor()
                )
            }
        }
    }
}

@Composable
fun CardListOptions(
    onDelete: () -> Unit, onCopyMoveCL: (Decision, Int) -> Unit,
    onSelectAll: () -> Unit, onDeselectAll: () -> Unit, onDuplicate: () -> Unit,
    getUIStyle: GetUIStyle, enabled: Boolean, dialogs: Dialogs,
    onDelDialogToggle: (Boolean) -> Unit, expanded: MutableState<Boolean>,
    selectedDeck: Deck?, onCORMDialogToggle: (Boolean) -> Unit, selectable: Boolean,
    onDupDialogToggle: (Boolean) -> Unit, deckList: List<Deck>
) {
    var decision by rememberSaveable { mutableStateOf<Decision>(Decision.Idle) }
    Box(Modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(
            onClick = { expanded.value = true },
            modifier = Modifier
                .padding(4.dp)
                .size(54.dp)
        ) {
            Icon(
                Icons.Default.MoreVert, contentDescription = null, tint = getUIStyle.titleColor()
            )
        }
        DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
            DropdownMenuItem(
                onClick = { onSelectAll() },
                text = { Text(stringResource(R.string.select_all)) },
            )
            DropdownMenuItem(
                onClick = { onDeselectAll() },
                text = { Text(stringResource(R.string.deselect_all)) }
            )
            DropdownMenuItem(
                onClick = { onCORMDialogToggle(true); decision = Decision.Copy },
                enabled = selectable, text = { Text(stringResource(R.string.copy_cards)) }
            )
            DropdownMenuItem(
                onClick = { onCORMDialogToggle(true); decision = Decision.Move },
                enabled = selectable, text = { Text(stringResource(R.string.move_cards)) }
            )
            DropdownMenuItem(
                onClick = { onDupDialogToggle(true) }, enabled = selectable,
                text = { Text(stringResource(R.string.duplicate_cards)) })
            DropdownMenuItem(
                onClick = { onDelDialogToggle(true) }, enabled = selectable,
                text = { Text(stringResource(R.string.delete_card_list)) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        modifier = Modifier
                            .size(28.dp),
                        contentDescription = stringResource(R.string.delete_card),
                        tint = getUIStyle.iconColor()
                    )
                }
            )
        }
        DeleteCards(
            showDialog = dialogs.showDelete, onDismiss = { onDelDialogToggle(it) },
            enabled = enabled, getUIStyle = getUIStyle, onDelete = { onDelete() }
        )
        CopyMoveCardList(
            showDialog = dialogs.showMoveCopy, onDismiss = { onCORMDialogToggle(it) },
            enabled = enabled, getUIStyle = getUIStyle, onCopyOrMove = { id ->
                if (decision !is Decision.Idle) onCopyMoveCL(decision, id)
            }, deckList = deckList, selectedDeck = selectedDeck
        )
        DuplicateCards(
            showDialog = dialogs.showDuplicate, onDismiss = { onDupDialogToggle(it) },
            enabled = enabled, getUIStyle = getUIStyle, onDuplicate = { onDuplicate() }
        )
    }
}