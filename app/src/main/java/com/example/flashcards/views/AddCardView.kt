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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.controller.MainViewModel
import com.example.flashcards.ui.theme.buttonColor
import com.example.flashcards.ui.theme.textColor
import com.example.flashcards.ui.theme.titleColor
import androidx.compose.runtime.LaunchedEffect
import com.example.flashcards.ui.theme.backgroundColor
import kotlinx.coroutines.delay

class AddCardView(private var viewModel: MainViewModel) {

    @Composable
    fun AddCard(deckId: Int, onNavigate: () -> Unit) {
        var question by remember { mutableStateOf("")  }
        var answer by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf("") }
        var successMessage by remember { mutableStateOf("") }
        val presetModifier = Modifier
            .padding(top = 16.dp,start = 16.dp, end = 16.dp)
            .size(54.dp)

        Box(
            modifier = Modifier.fillMaxSize()
            .padding(8.dp)
            .background(backgroundColor)
        ) {
            BackButton(
                onBackClick = { onNavigate() },
                modifier = presetModifier
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Question",
                    fontSize = 45.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 116.sp,
                    color = titleColor,
                    modifier = Modifier
                        .padding(top = 20.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    EditTextField(
                        value = question,
                        onValueChanged = { newText ->
                            question =
                                newText
                        },
                        labelStr = "Question",
                        modifier = Modifier
                            .weight(1f)
                    )
                }
                Text(
                    text = "Answer",
                    fontSize = 45.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 116.sp,
                    color = titleColor
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    EditTextField(
                        value = answer,
                        onValueChanged = { newText ->
                            answer =
                                newText
                        },
                        labelStr = "Answer",
                        modifier = Modifier
                            .weight(1f)
                    )
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = androidx.compose.ui.graphics.Color.Red,
                        modifier = Modifier.padding(8.dp),
                        fontSize = 16.sp
                    )
                }
                if (successMessage.isNotEmpty()) {
                    Text(
                        text = successMessage,
                        color = textColor,
                        modifier = Modifier.padding(8.dp),
                        fontSize = 16.sp
                    )
                }
                LaunchedEffect(successMessage) {
                    delay(1750)
                    successMessage = ""
                }
                LaunchedEffect(errorMessage) {
                    delay(1750)
                    errorMessage = ""
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            if (question.isBlank() || answer.isBlank()) {
                                errorMessage = "Both fields must be filled out"
                                successMessage = ""
                            } else {
                                viewModel.addCard(deckId, question, answer)
                                question = ""
                                answer = ""
                                errorMessage = ""
                                successMessage = "Card added!"
                            }
                        },
                        modifier = Modifier.padding(top = 48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor,
                            contentColor = textColor
                        )
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}