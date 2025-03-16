package com.example.flashcards.views.miscFunctions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.flashcards.R
import com.example.flashcards.controller.onClickActions.DeleteCard
import com.example.flashcards.controller.viewModels.cardViewsModels.EditCardViewModel
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.ui.theme.GetUIStyle
import kotlinx.coroutines.launch

@Composable
fun SmallAddButton(
    onClick: () -> Unit,
    iconSize: Int = 45,
    getUIStyle: GetUIStyle
) {
    FloatingActionButton(
        onClick = {
            onClick()
        },
        modifier = Modifier
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
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
            modifier = Modifier
                .size(32.dp),
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
    exportDeck: () -> Unit,
    clientExists: Boolean,
    modifier: Modifier = Modifier,
    getUIStyle: GetUIStyle,
    fields: Fields
) {
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    IconButton(
        onClick = {
            if (!fields.inDeckClicked.value) {
                expanded = true
            }
        },
        modifier = modifier
            .background(
                color = getUIStyle.buttonColor(),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Icon(
            imageVector = Icons.Filled.Edit,
            modifier = Modifier
                .size(24.dp),
            contentDescription = "Settings",
            tint = getUIStyle.iconColor()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(onClick = {
                expanded = false
                coroutineScope.launch {
                    fields.mainClicked.value = true
                    delayNavigate()
                    onNavigateToEditDeck()
                }
            },
                text = { Text(stringResource(R.string.edit_deck)) })
            DropdownMenuItem(onClick = {
                expanded = false
                fields.mainClicked.value = true
                onNavigateToEditCards()

            },
                text = { Text(stringResource(R.string.edit_flashcards)) })
            if (clientExists) {
                DropdownMenuItem(onClick = {
                    expanded = false
                    fields.mainClicked.value = true
                    exportDeck()

                },
                    text = { Text("Export Deck") })
            }
        }
    }
}


@Composable
fun MainSettingsButton(
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    getUIStyle: GetUIStyle,
    fields: Fields
) {
    IconButton(
        onClick = {
            if (!fields.mainClicked.value) {
                fields.mainClicked.value = true
                onNavigateToSettings()
            }
        },
        modifier = modifier
            .background(
                color = getUIStyle.buttonColor(),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Icon(
            imageVector = Icons.Filled.Settings,
            modifier = Modifier
                .size(24.dp),
            contentDescription = "Main Settings",
            tint = getUIStyle.iconColor()
        )
    }
}


@Composable
fun CardOptionsButton(
    editCardVM: EditCardViewModel,
    getUIStyle: GetUIStyle, card: Card,
    fields: Fields,
    type: MutableState<String>,
    expanded: MutableState<Boolean>,
    modifier: Modifier,
    onDelete: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val showDialog = remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(
            onClick = { expanded.value = true },
            modifier = Modifier
                .size(54.dp)
                .align(Alignment.TopEnd)
                .offset(y = (-8).dp)
        ) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = "Card Type",
                tint = getUIStyle.titleColor()
            )
        }
        DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
            DropdownMenuItem(
                onClick = { type.value = "basic" },
                text = { Text(stringResource(R.string.basic_card)) })
            DropdownMenuItem(
                onClick = { type.value = "three" },
                text = { Text(stringResource(R.string.three_field_card)) })
            DropdownMenuItem(
                onClick = { type.value = "hint" },
                text = { Text(stringResource(R.string.hint_card)) })
            DropdownMenuItem(
                onClick = { type.value = "multi" },
                text = { Text(stringResource(R.string.multi_choice_card)) })
            DropdownMenuItem(
                onClick = { type.value = "math" },
                text = { Text("Math") }
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
            editCardVM, coroutineScope,
            card, fields, showDialog,
            onDelete, getUIStyle
        )
    }
}