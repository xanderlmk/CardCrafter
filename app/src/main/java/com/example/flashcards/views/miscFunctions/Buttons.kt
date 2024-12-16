package com.example.flashcards.views.miscFunctions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.flashcards.R
import kotlinx.coroutines.launch

@Composable
fun SmallAddButton(
    onClick:() -> Unit,
    iconSize: Int = 45,
    getModifier: GetModifier
) {
    FloatingActionButton(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .padding(16.dp),
        containerColor = getModifier.buttonColor()

    ) {
        Icon(
            Icons.Outlined.Add,
            "Add Deck",
            modifier = Modifier.size(iconSize.dp),
            tint = getModifier.iconColor(),
        )
    }
}

@Composable
fun AddCardButton (
    onClick:() -> Unit
){
    ExtendedFloatingActionButton(
        onClick = { onClick()},
        modifier = Modifier
            .padding(16.dp)
    ) {
        Icon(Icons.Filled.Add, "Add Card")
        Text(text = stringResource(R.string.add_card))
    }

}


@Composable
fun BackButton(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    getModifier: GetModifier
) {
    val coroutineScope = rememberCoroutineScope()
    IconButton(
        onClick = {
            coroutineScope.launch {
                delayNavigate()
                onBackClick()
            }
        },
        modifier = modifier
            .background(
                color= getModifier.buttonColor(),
                shape = RoundedCornerShape(16.dp))
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            modifier = Modifier
                .size(36.dp),
            contentDescription = "Back",
            tint = getModifier.iconColor()
        )
    }
}

@Composable
fun SettingsButton(
    onNavigateToEditDeck: () -> Unit,
    onNavigateToEditCards: () -> Unit,
    modifier: Modifier = Modifier,
    getModifier: GetModifier
) {
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    IconButton(
        onClick = {
            expanded = true
        },
        modifier = modifier
            .background(
                color= getModifier.buttonColor(),
                shape = RoundedCornerShape(16.dp))
    ) {
        Icon(
            imageVector = Icons.Filled.Settings,
            modifier = Modifier
                .size(28.dp),
            contentDescription = "Settings",
            tint = getModifier.iconColor()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(onClick = {
                coroutineScope.launch {
                    delayNavigate()
                    onNavigateToEditDeck()
                    expanded = false
                } },
                text = {Text(stringResource(R.string.edit_deck))})
            DropdownMenuItem(onClick = {
                coroutineScope.launch {
                    delayNavigate()
                    onNavigateToEditCards()
                    expanded = false
                } },
                text = {Text(stringResource(R.string.edit_flashcards))})
        }
    }
}


@Composable
fun MainSettingsButton(
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    getModifier: GetModifier
) {
    IconButton(
        onClick = {
            onNavigateToSettings()
        },
        modifier = modifier
            .background(
                color= Color.Transparent,
                shape = RoundedCornerShape(16.dp))
    ) {
        Icon(
            imageVector = Icons.Filled.Settings,
            modifier = Modifier
                .size(24.dp),
            contentDescription = "Main Settings",
            tint = getModifier.buttonColor()
        )
    }
}