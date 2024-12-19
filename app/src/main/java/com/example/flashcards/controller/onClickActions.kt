package com.example.flashcards.controller

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SnackbarHostState
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
import com.example.flashcards.R
import com.example.flashcards.controller.viewModels.DeckViewModel
import com.example.flashcards.model.Fields
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.ui.theme.deleteTextColor
import com.example.flashcards.views.miscFunctions.GetModifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

fun updateMultipliers(
    viewModel: DeckViewModel, newGoodMultiplier : Double,
    newBadMultiplier : Double, errorMessage :  MutableState<String>,
    isSubmitting: MutableState <Boolean>, deck: Deck,
    expandedEditMultiplier: MutableState<Boolean>,
    successful: MutableState<String>,
    coroutineScope: CoroutineScope) {

    coroutineScope.launch {
        if (newGoodMultiplier <= 1.0) {
            errorMessage.value = "Good Multiplier must be greater than 1.0"
            return@launch
        }
        if (newBadMultiplier >= 1.0 || newBadMultiplier < 0.0){
            errorMessage.value = "Bad Multiplier must be less than 1.0 " +
                    "and greater than 0.0"
            return@launch
        }
        if (newGoodMultiplier == deck.goodMultiplier &&
            newBadMultiplier == deck.badMultiplier) {
            expandedEditMultiplier.value = false
            return@launch
        }
        isSubmitting.value = true
        try {
            val result1 =
                viewModel.updateDeckGoodMultiplier(newGoodMultiplier,deck.id)
            val result2 =
                viewModel.updateDeckBadMultiplier(newBadMultiplier, deck.id)
            if (result1 > 0 && result2 > 0) {
                successful.value = "Updated Multipliers!"
                expandedEditMultiplier.value = false
            } else {
                errorMessage.value =
                    "Failed to update multipliers"
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
    viewModel: DeckViewModel, newDeckName : String,
    errorMessage :  MutableState<String>,
    emptyDeckName : String, currentName : String,
    deckNameExists : String, deckNameFailed : String,
    isSubmitting: MutableState <Boolean>, deck: Deck,
    onNavigate: () -> Unit,
    coroutineScope: CoroutineScope
){
    coroutineScope.launch {
        if (newDeckName.isBlank()) {
            errorMessage.value = emptyDeckName
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
                errorMessage.value   =
                    deckNameExists
                return@launch
            }

            val result =
                viewModel.updateDeckName(newDeckName, deck.id)
            if (result > 0) {
                onNavigate()
            } else {
                errorMessage.value =
                    deckNameFailed
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
    getModifier : GetModifier,
    coroutineScope: CoroutineScope,
    fields: Fields,
    onDelete: () -> Unit
) {
    var showConfirmationDialog by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()){
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
            Text(stringResource(R.string.delete_deck))
        }

        if (showConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                title = { Text(stringResource(R.string.delete_deck)) },
                text = { Text(text = "Are you sure you want to delete this deck?") },
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
