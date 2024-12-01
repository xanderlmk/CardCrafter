package com.example.flashcards.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.controller.MainViewModel
import com.example.flashcards.ui.theme.backgroundColor
import com.example.flashcards.ui.theme.buttonColor
import com.example.flashcards.ui.theme.textColor
import com.example.flashcards.ui.theme.titleColor
import kotlinx.coroutines.launch

class EditDeckName(private var viewModel: MainViewModel) {
    @Composable
    fun ChangeDeckName(currentName: String, deckId: Int, onNavigate: () -> Unit) {
        var newDeckName by remember { mutableStateOf(currentName) }
        var errorMessage by remember { mutableStateOf("") }
        var isSubmitting by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        Box(
            modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Change Deck Name: $currentName",
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    color = titleColor,
                    modifier = Modifier.padding(top = 10.dp)
                )

                EditTextField(
                    value = newDeckName,
                    onValueChanged = {
                        newDeckName = it
                        errorMessage = "" // Clear error when user types
                    },
                    labelStr = "Deck Name",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
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
                        onClick = { onNavigate() },
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
                                        errorMessage = "A deck with this name already exists"
                                        return@launch
                                    }

                                    val result = viewModel.updateDeckName(newDeckName, deckId)
                                    if (result > 0) {
                                        onNavigate()
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
}