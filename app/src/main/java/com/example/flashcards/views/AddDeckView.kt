package com.example.flashcards.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import com.example.flashcards.controller.MainViewModel
import androidx.lifecycle.viewModelScope


class AddDeckView(viewModel: MainViewModel) {
    private val viewModel = viewModel

    @Composable
    fun addDeck(onDismiss: () -> Unit) {
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var deckName by remember {mutableStateOf("")  }

            Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Add deck",
                fontSize = 50.sp,
                textAlign = TextAlign.Center,
                lineHeight = 116.sp
            )
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                EditTextField(
                    value = deckName,
                    onValueChanged = { newText ->
                        deckName = newText
                    },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(1f)
                )
            }
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
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
                       /* if (viewModel.addDeck(deckName)) {
                            deckName = ""
                            onDismiss()
                        }
                        else {
                            errorMessage = "Deck name must be unique"
                        }*/
                        viewModel.addDeck(deckName)
                        deckName = ""
                        onDismiss()
                    },
                    modifier = Modifier.padding(top = 48.dp)
                ) {
                    Text("Submit")
                }
            }
        }
    }


    @Composable
    fun EditTextField(
        value: String,
        onValueChanged: (String) -> Unit,
        modifier: Modifier
    ) {
        TextField(
            value = value,
            singleLine = true,
            modifier = modifier,
            onValueChange = onValueChanged,
            label = { Text("Deck Name") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
    }
}