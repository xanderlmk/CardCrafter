package com.example.flashcards.views.deckViews


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcards.R
import com.example.flashcards.controller.AppViewModelProvider
import com.example.flashcards.controller.viewModels.deckViewsModels.AddDeckViewModel
import com.example.flashcards.views.miscFunctions.BackButton
import com.example.flashcards.views.miscFunctions.EditTextField
import com.example.flashcards.ui.theme.GetModifier
import com.example.flashcards.views.miscFunctions.EditIntField


class AddDeckView(
    private var getModifier: GetModifier,
) {
    @Composable
    fun AddDeck(onNavigate: () -> Unit, reviewAmount : String,
                cardAmount : String) {
        val viewModel: AddDeckViewModel = viewModel(factory = AppViewModelProvider.Factory)
        var errorMessage by remember { mutableStateOf("") }
        var deckName by rememberSaveable {  mutableStateOf("") }
        var deckReviewAmount by rememberSaveable { mutableStateOf(reviewAmount) }
        var deckCardAmount by rememberSaveable { mutableStateOf(cardAmount) }
        val coroutineScope = rememberCoroutineScope()
        val fieldOutAllFields = stringResource(R.string.fill_out_all_fields).toString()
        val deckNameAlreadyExists = stringResource(R.string.deck_already_exists).toString()
        val reviewAmount0 = stringResource(R.string.review_amount_0).toString()
        val reviewAmount10 = stringResource(R.string.review_amount_10).toString()
        val wrongCardAmount = stringResource(R.string.card_amount_5_1000).toString()
        val error = stringResource(R.string.error).toString()
        val scrollState = rememberScrollState()

        Box(
            modifier = getModifier.scrollableBoxViewModifier(scrollState)
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
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.add_deck),
                    fontSize = 35.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 100.sp,
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
                        onValueChanged = {
                            deckName = it
                        },
                        labelStr = stringResource(R.string.deck_name),
                        modifier = Modifier
                            .weight(1f)
                    )
                }
                Text(
                    text = stringResource(R.string.review_amount),
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp,
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
                    EditIntField(
                        value = deckReviewAmount,
                        onValueChanged = {
                            deckReviewAmount = it
                        },
                        labelStr = stringResource(R.string.review_amount) +
                                " (" + stringResource(R.string.default_value) + " $reviewAmount)",
                        modifier = Modifier
                            .weight(1f)
                    )
                }
                Text(
                    text = stringResource(R.string.card_amount),
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp,
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
                    EditIntField(
                        value = deckCardAmount,
                        onValueChanged = {
                            deckCardAmount = it
                        },
                        labelStr = stringResource(R.string.card_amount) +
                                " (" + stringResource(R.string.default_value) + " $cardAmount)",
                        modifier = Modifier
                            .weight(1f)
                    )
                }
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp),
                        fontSize = 15.sp
                    )
                }else {
                    Spacer(Modifier.padding(20.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
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
                                        } else if ((deckReviewAmount.toIntOrNull() ?: 0) <= 0) {
                                            errorMessage = reviewAmount0
                                        } else if ((deckReviewAmount.toIntOrNull() ?: 0) >= 41) {
                                            errorMessage = reviewAmount10
                                        } else if ((deckCardAmount.toIntOrNull() ?: 0)
                                            !in 5..1000){
                                            errorMessage = wrongCardAmount

                                        } else {
                                            viewModel.addDeck(
                                                deckName,
                                                deckReviewAmount.toIntOrNull() ?: 1,
                                                deckCardAmount.toIntOrNull() ?: 20
                                            )
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
                        )
                    ) {
                        Text(stringResource(R.string.submit))
                    }
                }
            }
        }
    }
}