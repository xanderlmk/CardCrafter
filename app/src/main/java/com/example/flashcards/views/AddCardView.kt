package com.example.flashcards.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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

class AddCardView(viewModel: MainViewModel) {
    private var viewModel = viewModel
    @Composable
    fun AddCard(deckId: Int, onDismiss: () -> Unit) {
        var question by remember { mutableStateOf("")  }
        var answer by remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Question",
                fontSize = 50.sp,
                textAlign = TextAlign.Center,
                lineHeight = 116.sp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                EditTextField(
                    value = question,
                    onValueChanged = { newText ->
                        question =
                            newText
                    },
                    labelStr = "Question",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(1f)
                )
            }
            Text(
                text = "Answer",
                fontSize = 50.sp,
                textAlign = TextAlign.Center,
                lineHeight = 116.sp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                EditTextField(
                    value = answer,
                    onValueChanged = { newText ->
                        answer =
                            newText
                    },
                    labelStr = "Answer",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(1f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        onDismiss()
                    },
                    modifier = Modifier.padding(top = 48.dp)
                ) {
                    Text("Return")
                }
                Button(
                    onClick = {
                        viewModel.addCard(deckId, question, answer)
                        question = ""
                        answer = ""
                    },
                    modifier = Modifier.padding(top = 48.dp)
                ) {
                    Text("Submit")
                }
            }
        }
    }
}