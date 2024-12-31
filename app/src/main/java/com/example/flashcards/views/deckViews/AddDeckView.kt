package com.example.flashcards.views.deckViews


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.flashcards.controller.viewModels.DeckViewModel
import androidx.compose.ui.res.stringResource
import com.example.flashcards.R
import com.example.flashcards.views.miscFunctions.BackButton
import com.example.flashcards.views.miscFunctions.EditTextField
import com.example.flashcards.ui.theme.GetModifier


class AddDeckView(private var viewModel: DeckViewModel,
    private var getModifier : GetModifier) {


    @Composable
    fun AddDeck(onNavigate: () -> Unit) {
        var errorMessage by remember { mutableStateOf("")}
        var deckName by remember {mutableStateOf("")  }
        val coroutineScope =  rememberCoroutineScope()
        val fieldOutAllFields = stringResource(R.string.fill_out_all_fields).toString()
        val deckNameAlreadyExists = stringResource(R.string.deck_already_exists).toString()
        val error = stringResource(R.string.error).toString()
        val presetModifier = Modifier
            .padding(top = 16.dp,start = 16.dp, end = 16.dp)
            .size(54.dp)
        Box(
            modifier = getModifier.boxViewsModifier()
        ) {
            BackButton(
                onBackClick = {
                    onNavigate()
                },
                modifier = presetModifier,
                getModifier = getModifier
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.add_deck),
                    fontSize = 40.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 116.sp,
                    color = getModifier.titleColor(),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
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
                        labelStr = stringResource(R.string.deck_name),
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
                                errorMessage = fieldOutAllFields
                            } else {
                                coroutineScope.launch {
                                    try {
                                        val exists = viewModel.checkIfDeckExists(deckName)
                                        if (exists > 0) {
                                            errorMessage = deckNameAlreadyExists
                                        } else {
                                            viewModel.addDeck(deckName)
                                            deckName = ""
                                            onNavigate()
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "$error: ${e.message}"
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getModifier.secondaryButtonColor(),
                            contentColor = getModifier.buttonTextColor()
                        ),
                        modifier = Modifier.padding(top = 48.dp)
                    ) {
                        Text(stringResource(R.string.submit))
                    }
                }
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}