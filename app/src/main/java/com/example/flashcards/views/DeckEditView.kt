package com.example.flashcards.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.controller.MainViewModel
import com.example.flashcards.ui.theme.backgroundColor
import com.example.flashcards.ui.theme.buttonColor
import com.example.flashcards.ui.theme.textColor
import com.example.flashcards.ui.theme.titleColor
import kotlinx.coroutines.launch

class DeckEditView(private var viewModel: MainViewModel){
        @Composable
        fun ChangeDeckName(currentName: String, deckID: Int, onDismiss: () -> Unit) {
            var newDeckName by remember { mutableStateOf(currentName) }
            var errorMessage by remember { mutableStateOf("") }
            var isSubmitting by remember { mutableStateOf(false) }
            val coroutineScope = rememberCoroutineScope()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Change Deck Name $deckID",
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    color = titleColor
                )

                EditTextField(
                    value = newDeckName,
                    onValueChanged = {
                        newDeckName = it
                        errorMessage = "" // Clear error when user types
                    },
                    labelStr = "Deck Name",
                    modifier = Modifier.fillMaxWidth(),
                   // isError = errorMessage.isNotEmpty()
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = androidx.compose.ui.graphics.Color.Red,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 16.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor,
                            contentColor = textColor
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                if (newDeckName.isBlank()) {
                                    errorMessage = "Deck name cannot be empty"
//                                    return@launch
                                }

                                if (newDeckName == currentName) {
                                    onDismiss()
 //                                   return@launch
                                }

                                isSubmitting = true
                                try {
                                    // First check if the deck name exists
                                    val exists = viewModel.checkIfDeckExists(newDeckName)
                                    if (exists > 0) {
                                        errorMessage = "A deck with this name already exists"
  //                                      return@launch
                                    }

                                    val result = viewModel.updateDeckName(newDeckName, deckID)
                                    if (result > 0) {
                                        onDismiss()
                                    } else {
                                        errorMessage = "Failed to update deck name"
                                    }
                                } catch (e: Exception) {
                                    errorMessage = e.message ?: "An error occurred"
                                } finally {
                                    isSubmitting = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor,
                            contentColor = textColor
                        ),
                        enabled = !isSubmitting,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                color = textColor,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text("Submit")
                        }
                    }
                }
            }
        }
    }