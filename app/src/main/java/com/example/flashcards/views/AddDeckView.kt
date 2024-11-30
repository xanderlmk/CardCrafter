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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewModelScope
import com.example.flashcards.controller.MainViewModel
import com.example.flashcards.ui.theme.backgroundColor
import com.example.flashcards.ui.theme.titleColor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.text.font.FontWeight
import com.example.flashcards.ui.theme.buttonColor
import com.example.flashcards.ui.theme.textColor


class AddDeckView(private var viewModel: MainViewModel) {


    @Composable
    fun AddDeck(onNavigate: () -> Unit) {
        var errorMessage by remember { mutableStateOf("")}
        var deckName by remember {mutableStateOf("")  }
        val coroutineScope =  rememberCoroutineScope()
        val presetModifier = Modifier
            .padding(top = 16.dp,start = 16.dp, end = 16.dp)
            .size(54.dp)
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(24.dp)
                .background(backgroundColor)
        ) {
            BackButton(
                onBackClick = { onNavigate() },
                modifier = presetModifier
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Add A Deck",
                    fontSize = 40.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 116.sp,
                    color = titleColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Transparent)
                        .padding(top = 20.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    EditTextField(
                        value = deckName,
                        onValueChanged = { newText ->
                            deckName = newText
                        },
                        labelStr = "Deck Name",
                        modifier = Modifier
                            .weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            if (deckName.isBlank()) {
                                errorMessage = "Deck must be filled out"
                            } else {
                                coroutineScope.launch {
                                    try {
                                        val exists = viewModel.checkIfDeckExists(deckName)
                                        if (exists > 0) {
                                            errorMessage = "deck name already exists"
                                        } else {
                                            viewModel.addDeck(deckName)
                                            deckName = ""
                                            onNavigate()
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "Error: ${e.message}"
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor,  // Set your desired background color here
                            contentColor = textColor
                        ),
                        modifier = Modifier.padding(top = 48.dp)
                    ) {
                        Text("Submit")
                    }
                }
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = androidx.compose.ui.graphics.Color.Red,
                        modifier = Modifier.padding(8.dp),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}