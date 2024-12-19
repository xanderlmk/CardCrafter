package com.example.flashcards.views.deckViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.controller.viewModels.DeckViewModel
import com.example.flashcards.R
import com.example.flashcards.controller.DeleteDeck
import com.example.flashcards.controller.updateDeckName
import com.example.flashcards.controller.updateMultipliers
import com.example.flashcards.model.Fields
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.views.miscFunctions.BackButton
import com.example.flashcards.views.miscFunctions.EditNumberField
import com.example.flashcards.views.miscFunctions.EditTextField
import com.example.flashcards.views.miscFunctions.GetModifier
import kotlin.String

class EditDeckView(
    private var viewModel: DeckViewModel,
    private var fields: Fields,
    private var getModifier: GetModifier) {
    @Composable
    fun EditDeck(
        currentName: String, deck: Deck,
        onNavigate: () -> Unit, onDelete: () -> Unit
    ) {
        var newDeckName by remember { mutableStateOf(currentName) }
        var newGoodMultiplier by remember { mutableDoubleStateOf(deck.goodMultiplier) }
        var newBadMultiplier by remember { mutableDoubleStateOf(deck.badMultiplier) }
        val errorMessage = remember { mutableStateOf("") }
        val successful = remember { mutableStateOf("") }
        val isSubmitting = remember { mutableStateOf(false) }
        var expandedChangeName by remember { mutableStateOf(false) }
        val expandedEditMultiplier = remember { mutableStateOf(false) }
        val emptyDeckName = stringResource(R.string.empty_deck_name).toString()
        val deckNameExists = stringResource(R.string.deck_name_exists).toString()
        val deckNameFailed = stringResource(R.string.deck_name_failed).toString()
        val coroutineScope = rememberCoroutineScope()
        val snackBarHostState = remember { SnackbarHostState() }


        LaunchedEffect(successful.value) {
            if(successful.value.isNotEmpty()) {
                snackBarHostState.showSnackbar(
                    message = successful.value,
                    duration = SnackbarDuration.Short
                )
                successful.value = ""
            }
        }

        Box(
            modifier = getModifier.boxViewsModifier()
        ) {
            BackButton(
                onBackClick = {
                    onNavigate()
                },
                modifier = getModifier.backButtonModifier(),
                getModifier = getModifier
            )
            Column(
                modifier = Modifier
                    .padding(top = 20.dp, start = 15.dp, end = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.edit_deck) + ": $currentName",
                    fontSize = 30.sp,
                    lineHeight = 32.sp,
                    textAlign = TextAlign.Center,
                    color = getModifier.titleColor(),
                    fontWeight = Bold,
                    modifier = Modifier.padding(top = 20.dp,
                        start = 50.dp, end = 50.dp)
                )
                if (!expandedChangeName) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp,
                                vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                expandedChangeName = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getModifier.secondaryButtonColor(),
                                contentColor = getModifier.buttonTextColor()
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.change_deck_name))
                        }
                    }
                } else {
                    EditTextField(
                        value = newDeckName,
                        onValueChanged = {
                            newDeckName = it
                            errorMessage.value = "" // Clear error when user types
                        },
                        labelStr = stringResource(R.string.deck_name),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 10.dp,
                                vertical = 12.dp
                            ),
                    )

                    if (errorMessage.value.isNotEmpty()) {
                        Text(
                            text = errorMessage.value,
                            color = Color.Red,
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }else {
                        Spacer(modifier = Modifier.padding(12.dp))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                errorMessage.value = ""
                                newDeckName = currentName
                                expandedChangeName = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getModifier.tertiaryButtonColor(),
                                contentColor = getModifier.onTertiaryButtonColor()
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.cancel))
                        }

                        Button(
                            onClick = {
                                updateDeckName(
                                    viewModel, newDeckName,
                                    errorMessage, emptyDeckName,
                                    currentName, deckNameExists,
                                    deckNameFailed, isSubmitting, deck,
                                    onNavigate, coroutineScope)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getModifier.tertiaryButtonColor(),
                                contentColor = getModifier.onTertiaryButtonColor()
                            ),
                            enabled = !isSubmitting.value,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isSubmitting.value) {
                                CircularProgressIndicator(
                                    color = getModifier.titleColor(),
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(stringResource(R.string.submit))
                            }
                        }
                    }
                }
                if (!expandedEditMultiplier.value) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp,
                                vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SnackbarHost(
                            hostState = snackBarHostState,
                            modifier = Modifier
                                .background(
                                    color= getModifier.buttonColor(),
                                    shape = RoundedCornerShape(24.dp)),
                            )
                        Button(
                            onClick = {
                                expandedEditMultiplier.value = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getModifier.secondaryButtonColor(),
                                contentColor = getModifier.buttonTextColor()
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Edit Multiplier")
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 10.dp,
                            vertical = 12.dp
                        )) {
                        EditNumberField(
                            value = newGoodMultiplier.toString(),
                            onValueChanged = {
                                newGoodMultiplier = it.toDouble()
                                errorMessage.value = "" // Clear error when user types
                            },
                            labelStr = "Good Multiplier",
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .padding(end = 2.dp)
                        )
                        EditNumberField(
                            value = newBadMultiplier.toString(),
                            onValueChanged = {
                                newBadMultiplier = it.toDouble()
                                errorMessage.value = "" // Clear error when user types
                            },
                            labelStr = "Bad Multiplier",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 2.dp)
                        )
                    }

                    if (errorMessage.value.isNotEmpty()) {
                        Text(
                            text = errorMessage.value,
                            color = Color.Red,
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }else {
                        Spacer(modifier = Modifier.padding(12.dp))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                errorMessage.value = ""
                                newBadMultiplier = deck.badMultiplier
                                newGoodMultiplier = deck.goodMultiplier
                                expandedEditMultiplier.value = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getModifier.tertiaryButtonColor(),
                                contentColor = getModifier.onTertiaryButtonColor()
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.cancel))
                        }

                        Button(
                            onClick = {
                                updateMultipliers(viewModel,newGoodMultiplier,
                                    newBadMultiplier,errorMessage,isSubmitting,
                                    deck,expandedEditMultiplier,successful,coroutineScope)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getModifier.tertiaryButtonColor(),
                                contentColor = getModifier.onTertiaryButtonColor()
                            ),
                            enabled = !isSubmitting.value,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isSubmitting.value) {
                                CircularProgressIndicator(
                                    color = getModifier.titleColor(),
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(stringResource(R.string.submit))
                            }
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(50.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        DeleteDeck(viewModel,deck,
                            getModifier,coroutineScope,
                            fields, onDelete)
                    }
                }
            }
        }
    }
}
