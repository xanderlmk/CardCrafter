package com.example.flashcards.controller.onClickActions

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
import com.example.flashcards.controller.viewModels.deckViewsModels.EditDeckViewModel
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.ui.theme.deleteTextColor
import com.example.flashcards.ui.theme.GetUIStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun updateCardAmount(
    vm: EditDeckViewModel, newCA: Int,
    errorMessage: MutableState<String>,
    isSubmitting: MutableState<Boolean>, deck: Deck,
    successful : MutableState<String>,
    errorMessages: List<String>, successMessage: String,
    coroutineScope: CoroutineScope
){
    coroutineScope.launch{
        if (newCA < 5){
            errorMessage.value = errorMessages[0]
            return@launch
        }
        if (newCA > 1000){
            errorMessage.value = errorMessages[1]
            return@launch
        }
        if (newCA == deck.cardAmount){
            errorMessage.value = errorMessages[2]
            return@launch
        }
        isSubmitting.value = true
        try {
            val result = vm.updateDeckCardAmount(newCA, deck.id)
            if (result > 0 ){
                successful.value = successMessage
            } else {
                errorMessage.value =
                    errorMessages[3]
            }
        } catch (e : Exception){
            errorMessage.value =
                e.message ?: R.string.error_occurred.toString()
        }
        finally {
            isSubmitting.value = false
        }
    }
}
fun updateReviewAmount(
    vm: EditDeckViewModel, newRA : Int,
    errorMessage: MutableState<String>,
    isSubmitting: MutableState<Boolean>, deck: Deck,
    successful: MutableState<String>,
    errorMessages : List<String>, successMessage : String,
    coroutineScope: CoroutineScope
) {
    coroutineScope.launch {
        if (newRA <= 0) {
            errorMessage.value = errorMessages[0]
            return@launch
        }
        if (newRA >= 10) {
            errorMessage.value = errorMessages[1]
            return@launch
        }

        if (newRA == deck.reviewAmount) {
            errorMessage.value = errorMessages[2]
            return@launch
        }

        isSubmitting.value = true
        try {
            val result =
                vm.updateReviewAmount(newRA, deck.id)
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
    vm: EditDeckViewModel, newGM : Double, newBM : Double,
    errorMessage: MutableState<String>,
    isSubmitting: MutableState<Boolean>, deck: Deck,
    successful: MutableState<String>,
    errorMessages: List<String>, successMessage : String,
    coroutineScope: CoroutineScope
) {

    coroutineScope.launch {
        if (newGM <= 1.0) {
            errorMessage.value = errorMessages[0]
            return@launch
        }
        if (newBM >= 1.0 ||
            newBM < 0.0) {
            errorMessage.value = errorMessages[1]
            return@launch
        }
        if (newGM == deck.goodMultiplier &&
            newBM == deck.badMultiplier
        ) {
            errorMessage.value = errorMessages[2]
            return@launch
        }
        isSubmitting.value = true
        try {
            val result1 =
                vm.updateDeckGoodMultiplier(newGM, deck.id)
            val result2 =
                vm.updateDeckBadMultiplier(newBM, deck.id)
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
    vm: EditDeckViewModel, newName : String,
    errorMessage: MutableState<String>,
    deckErrors: List<String>, currentName: String,
    isSubmitting: MutableState<Boolean>, deck: Deck,
    onNavigate: () -> Unit,
    coroutineScope: CoroutineScope
) {
    coroutineScope.launch {
        if (newName.isBlank()) {
            errorMessage.value = deckErrors[0]
            return@launch
        }

        if (newName == currentName) {
            onNavigate()
            return@launch
        }

        isSubmitting.value = true
        try {
            // First check if the deck name exists
            val exists = vm.checkIfDeckExists(newName)
            if (exists > 0) {
                errorMessage.value = deckErrors[1]
                return@launch
            }

            val result =
                vm.updateDeckName(newName, deck.id)
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
    viewModel: EditDeckViewModel,
    deck: Deck,
    getUIStyle: GetUIStyle,
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
                containerColor = getUIStyle.secondaryButtonColor(),
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
                        color = getUIStyle.titleColor()
                    )
                },
                text = { Text(
                    text = stringResource(R.string.sure_to_delete_deck),
                    color = getUIStyle.titleColor()) },
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
                            containerColor = getUIStyle.secondaryButtonColor(),
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
}