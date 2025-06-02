package com.belmontCrest.cardCrafter.uiFunctions

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.controller.onClickActions.DeleteCard
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.model.Type
import com.belmontCrest.cardCrafter.navigation.NavViewModel


@Composable
fun PullDeck(modifier: Modifier, getUIStyle: GetUIStyle, onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = {
            onClick()
        }, modifier = modifier
            .padding(8.dp),
        containerColor = getUIStyle.semiTransButtonColor(),
        contentColor = getUIStyle.titleColor(),
        elevation = FloatingActionButtonDefaults.elevation((Int.MAX_VALUE / 2).dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.merge),
            contentDescription = "Merge deck",
        )
        Text("Merge deck")
    }
}

@Composable
fun SmallAddButton(
    onClick: () -> Unit, iconSize: Int = 45,
    getUIStyle: GetUIStyle, modifier: Modifier
) {
    FloatingActionButton(
        onClick = {
            onClick()
        },
        modifier = modifier
            .padding(16.dp),
        containerColor = getUIStyle.buttonColor()
    ) {
        Icon(
            Icons.Outlined.Add,
            "Add Deck",
            modifier = Modifier.size(iconSize.dp),
            tint = getUIStyle.iconColor(),
        )
    }
}

@Composable
fun AddCardButton(
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        modifier = Modifier
            .padding(16.dp)
    ) {
        Icon(Icons.Outlined.Add, "Add Card")
        Text(text = stringResource(R.string.add_card))
    }

}

@Composable
fun ExportDeckButton(
    onClick: () -> Unit,
    modifier: Modifier
) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        modifier = modifier
            .padding(8.dp)
    ) {
        Icon(Icons.Default.AddCircle, "Export Deck")
        Text(text = "Export Deck")
    }
}

@Composable
fun BackButton(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    getUIStyle: GetUIStyle
) {
    IconButton(
        onClick = {
            onBackClick()
        },
        modifier = modifier
            .background(
                color = getUIStyle.buttonColor(),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(6.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
            modifier = Modifier
                .size(24.dp),
            contentDescription = "Back",
            tint = getUIStyle.iconColor()
        )
    }
}

@Composable
fun RedoCardButton(
    onRedoClick: () -> Unit,
    modifier: Modifier = Modifier,
    getUIStyle: GetUIStyle
) {
    IconButton(
        onClick = {
            onRedoClick()
        },
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.return_arrow),
            modifier = Modifier
                .size(22.dp),
            contentDescription = "Redo",
            tint = getUIStyle.iconColor()
        )
    }

}

@Composable
fun SettingsButton(
    onNavigateToEditDeck: () -> Unit,
    onNavigateToEditCards: () -> Unit,
    modifier: Modifier = Modifier,
    getUIStyle: GetUIStyle,
    fields: Fields
) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(
        onClick = {
            if (!fields.inDeckClicked.value) {
                expanded = true
            }
        },
        modifier = modifier
            .background(
                color = getUIStyle.buttonColor(),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(6.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Edit,
            modifier = Modifier
                .size(24.dp),
            contentDescription = "Settings",
            tint = getUIStyle.iconColor()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    fields.mainClicked.value = true
                    onNavigateToEditDeck()
                },
                text = { Text(stringResource(R.string.edit_deck)) })
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    fields.mainClicked.value = true
                    onNavigateToEditCards()

                },
                text = { Text(stringResource(R.string.edit_flashcards)) })
        }
    }
}


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
        DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
            DropdownMenuItem(
                onClick = {
                    resetKeyboardStuff(navVM, fields.newType.value)
                    fields.newType.value = Type.BASIC
                },
                text = { Text(stringResource(R.string.basic_card)) })
            DropdownMenuItem(
                onClick = {
                    resetKeyboardStuff(navVM, fields.newType.value)
                    fields.newType.value = Type.THREE
                },
                text = { Text(stringResource(R.string.three_field_card)) })
            DropdownMenuItem(
                onClick = {
                    resetKeyboardStuff(navVM, fields.newType.value)
                    fields.newType.value = Type.HINT
                },
                text = { Text(stringResource(R.string.hint_card)) })
            DropdownMenuItem(
                onClick = {
                    resetKeyboardStuff(navVM, fields.newType.value)
                    fields.newType.value = Type.MULTI
                },
                text = { Text(stringResource(R.string.multi_choice_card)) })
            DropdownMenuItem(
                onClick = { fields.newType.value = Type.NOTATION },
                text = { Text("Notation Card") }
            )
            HorizontalDivider()
            DropdownMenuItem(
                onClick = {
                    showDialog.value = true
                },
                text = { Text("Delete Card") },
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

private fun resetKeyboardStuff(navVM: NavViewModel, type: String) {
    if (type == Type.NOTATION) {
        navVM.resetKeyboardStuff()
    }
}

@Composable
fun CardTypesButton(getUIStyle: GetUIStyle, navVM: NavViewModel) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val type by navVM.type.collectAsStateWithLifecycle()
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
                tint = getUIStyle.titleColor()
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                onClick = { navVM.updateType("basic") },
                text = { Text(stringResource(R.string.basic_card)) }
            )
            DropdownMenuItem(
                onClick = { navVM.updateType("three") },
                text = { Text(stringResource(R.string.three_field_card)) }
            )
            DropdownMenuItem(
                onClick = { navVM.updateType("hint") },
                text = { Text(stringResource(R.string.hint_card)) }
            )
            DropdownMenuItem(
                onClick = { navVM.updateType("multi") },
                text = { Text(stringResource(R.string.multi_choice_card)) }
            )
            DropdownMenuItem(
                onClick = { navVM.updateType("notation") },
                text = { Text("Notation Card") }
            )
        }
    }
}

@Composable
fun ToggleKeyBoard(
    navVM: NavViewModel, getUIStyle: GetUIStyle, type: String
) {
    val showKB by navVM.showKatexKeyboard.collectAsStateWithLifecycle()
    val selectedKB by navVM.selectedKB.collectAsStateWithLifecycle()
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
                    tint = getUIStyle.titleColor()
                )
            } else {
                Icon(
                    painterResource(R.drawable.twotone_keyboard_hide),
                    contentDescription = "Hide Keyboard",
                    tint = getUIStyle.titleColor()
                )
            }
        }
    }
}

@Composable
fun CancelButton(
    onClick: () -> Unit, enabled: Boolean, getUIStyle: GetUIStyle,
    modifier: Modifier = Modifier, fontSize: TextUnit = TextUnit.Unspecified
) {
    Button(
        onClick = {
            onClick()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = getUIStyle.secondaryButtonColor(),
            contentColor = getUIStyle.buttonTextColor()
        ),
        enabled = enabled, modifier = modifier
    ) { Text(text = stringResource(R.string.cancel), fontSize = fontSize) }
}

@Composable
fun SubmitButton(
    onClick: () -> Unit, enabled: Boolean,
    getUIStyle: GetUIStyle, string: String,
    modifier: Modifier = Modifier, fontSize: TextUnit = TextUnit.Unspecified,
    innerModifier: Modifier = Modifier
) {
    Button(
        onClick = {
            onClick()
        }, enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = getUIStyle.secondaryButtonColor(),
            contentColor = getUIStyle.buttonTextColor()
        ), modifier = modifier
    ) {
        Text(text = string, fontSize = fontSize, modifier = innerModifier)
    }
}

@Composable
fun MailButton(
    onClick: () -> Unit, getUIStyle: GetUIStyle
) {
    val ci = ContentIcons(getUIStyle)
    IconButton(onClick = { onClick() }) {
        ci.ContentIcon("mail", icon = Icons.Default.MailOutline, Modifier)
    }
}