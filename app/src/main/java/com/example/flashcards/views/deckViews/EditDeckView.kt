package com.example.flashcards.views.deckViews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.launch
import com.example.flashcards.R
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.ui.theme.deleteTextColor
import com.example.flashcards.views.miscFunctions.BackButton
import com.example.flashcards.views.miscFunctions.EditTextField
import com.example.flashcards.views.miscFunctions.GetModifier

class EditDeckView(private var viewModel: DeckViewModel,
    private var getModifier: GetModifier) {
    @Composable
    fun EditDeck(
        currentName: String, deck: Deck,
        onNavigate: () -> Unit, onDelete: () -> Unit
    ) {
        var newDeckName by remember { mutableStateOf(currentName) }
        var errorMessage by remember { mutableStateOf("") }
        var isSubmitting by remember { mutableStateOf(false) }
        var expanded by remember { mutableStateOf(false) }
        val emptyDeckName = stringResource(R.string.empty_deck_name).toString()
        val deckNameExists = stringResource(R.string.deck_name_exists).toString()
        val deckNameFailed = stringResource(R.string.deck_name_failed).toString()
        val backModifier = getModifier.backButtonModifier()
        val coroutineScope = rememberCoroutineScope()
        Box(
            modifier = getModifier.boxViewsModifier()
        ) {
            BackButton(
                onBackClick = {
                    onNavigate()
                },
                modifier = backModifier,
                getModifier = getModifier
            )
            Column(
                modifier = Modifier.padding(top = 20.dp),
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
                if (!expanded) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                expanded = true
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
                            errorMessage = "" // Clear error when user types
                        },
                        labelStr = stringResource(R.string.deck_name),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 10.dp,
                                vertical = 12.dp
                            ),
                        // isError = errorMessage.isNotEmpty()
                    )

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
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
                                errorMessage = ""
                                expanded = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getModifier.secondaryButtonColor(),
                                contentColor = getModifier.buttonTextColor()
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.cancel))
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    if (newDeckName.isBlank()) {
                                        errorMessage = emptyDeckName
                                        return@launch
                                    }

                                    if (newDeckName == currentName) {
                                        onNavigate()
                                        return@launch
                                    }

                                    isSubmitting = true
                                    try {
                                        // First check if the deck name exists
                                        val exists = viewModel.checkIfDeckExists(newDeckName)
                                        if (exists > 0) {
                                            errorMessage =
                                                deckNameExists
                                            return@launch
                                        }

                                        val result =
                                            viewModel.updateDeckName(newDeckName, deck.id)
                                        if (result > 0) {
                                            onNavigate()
                                        } else {
                                            errorMessage =
                                                deckNameFailed
                                        }
                                    } catch (e: Exception) {
                                        errorMessage =
                                            e.message ?: R.string.error_occurred.toString()
                                    } finally {
                                        isSubmitting = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = getModifier.secondaryButtonColor(),
                                contentColor = getModifier.buttonTextColor()
                            ),
                            enabled = !isSubmitting,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isSubmitting) {
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
                        modifier = Modifier.weight(1f)
                    ) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.deleteDeck(deck)
                                    onDelete()
                                }
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
                    }
                }
            }
        }
    }
}