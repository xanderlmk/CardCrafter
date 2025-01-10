package com.example.flashcards.views.cardViews.addCardViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.R
import com.example.flashcards.controller.viewModels.cardViewsModels.AddCardViewModel
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.views.miscFunctions.EditTextField
import com.example.flashcards.ui.theme.GetModifier
import com.example.flashcards.views.miscFunctions.PickAnswerChar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddMultiChoiceCard(
    vm : AddCardViewModel, deck: Deck,
    fields: Fields, getModifier: GetModifier
) {
    var successMessage by remember { mutableStateOf("") }
    val errorMessage by vm.errorMessage.collectAsState()
    val fillOutFields = stringResource(R.string.fill_out_all_fields).toString()
    val cardAdded = stringResource(R.string.card_added).toString()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .verticalScroll(scrollState)
    ) {
        Text(
            text = stringResource(R.string.question),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
            color = getModifier.titleColor(),
            modifier = Modifier
                .padding(top = 10.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            EditTextField(
                value = fields.question.value,
                onValueChanged = { newText ->
                    fields.question.value =
                        newText
                },
                labelStr = stringResource(R.string.question),
                modifier = Modifier
                    .weight(1f)
            )
        }
        Text(
            text = stringResource(R.string.choice_a),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
            color = getModifier.titleColor(),
            modifier = Modifier
                .padding(top = 8.dp)
                .background(
                    color =
                    if (fields.correct.value == 'a') {
                        getModifier.correctChoice()
                    } else {
                        Color.Transparent
                    }
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            EditTextField(
                value = fields.choices[0].value,
                onValueChanged = { newText ->
                    fields.choices[0].value =
                        newText
                },
                labelStr = stringResource(R.string.choice_a),
                modifier = Modifier
                    .weight(1f),
                inputColor =
                if (fields.correct.value == 'a') {
                    getModifier.onCorrectChoice()
                } else {
                    Color.Transparent
                }
            )
        }
        Text(
            text = stringResource(R.string.choice_b),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
            color = getModifier.titleColor(),
            modifier = Modifier
                .padding(top = 8.dp)
                .background(
                    color =
                    if (fields.correct.value == 'b') {
                        getModifier.correctChoice()
                    } else {
                        Color.Transparent
                    }
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            EditTextField(
                value = fields.choices[1].value,
                onValueChanged = { newText ->
                    fields.choices[1].value =
                        newText
                },
                labelStr = stringResource(R.string.choice_b),
                modifier = Modifier
                    .weight(1f),
                inputColor =
                if (fields.correct.value == 'b') {
                    getModifier.onCorrectChoice()
                } else {
                    Color.Transparent
                }
            )
        }
        Text(
            text = stringResource(R.string.choice_c),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
            color = getModifier.titleColor(),
            modifier = Modifier
                .padding(top = 8.dp)
                .background(
                    color =
                    if (fields.correct.value == 'c') {
                        getModifier.correctChoice()
                    } else {
                        Color.Transparent
                    }
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            EditTextField(
                value = fields.choices[2].value,
                onValueChanged = { newText ->
                    fields.choices[2].value =
                        newText
                },
                labelStr = stringResource(R.string.choice_c),
                modifier = Modifier
                    .weight(1f),
                inputColor =
                if (fields.correct.value == 'c') {
                    getModifier.onCorrectChoice()
                } else {
                    Color.Transparent
                }
            )
        }
        Text(
            text = stringResource(R.string.choice_d),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
            color = getModifier.titleColor(),
            modifier = Modifier
                .padding(top = 8.dp)
                .background(
                    color =
                    if (fields.correct.value == 'd') {
                        getModifier.correctChoice()
                    } else {
                        Color.Transparent
                    }
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            EditTextField(
                value = fields.choices[3].value,
                onValueChanged = { newText ->
                    fields.choices[3].value =
                        newText
                },
                labelStr = stringResource(R.string.choice_d),
                modifier = Modifier
                    .weight(1f),
                inputColor =
                if (fields.correct.value == 'd') {
                    getModifier.onCorrectChoice()
                } else {
                    Color.Transparent
                }
            )
        }

        PickAnswerChar(fields,getModifier)

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(4.dp),
                fontSize = 16.sp
            )
        } else {
            Spacer(modifier = Modifier.padding(16.dp))
        }
        if (successMessage.isNotEmpty()) {
            Text(
                text = successMessage,
                color = getModifier.titleColor(),
                modifier = Modifier.padding(4.dp),
                fontSize = 16.sp
            )
        } else {
            Spacer(modifier = Modifier.padding(16.dp))
        }
        LaunchedEffect(successMessage) {
            delay(1500)
            successMessage = ""
            coroutineScope.launch {
                scrollState.animateScrollTo(0)
            }
        }
        LaunchedEffect(errorMessage) {
            delay(1500)
            vm.clearErrorMessage()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (fields.question.value.isBlank() ||
                        fields.choices[0].value.isBlank() ||
                        fields.choices[1].value.isBlank() ||
                        fields.correct.value !in 'a'..'d'
                    ) {
                        vm.setErrorMessage(fillOutFields)
                        successMessage = ""
                    } else if (fields.choices[2].value.isBlank() &&
                        fields.choices[3].value.isNotBlank()
                    ) {
                        vm.setErrorMessage("Cannot skip choice C and fill choice D")
                        successMessage = ""
                    } else if ((fields.choices[2].value.isBlank() &&
                                fields.correct.value == 'c') ||
                        (fields.choices[3].value.isBlank() &&
                                fields.correct.value == 'd')
                    ) {
                        vm.setErrorMessage("Answer can't be a blank choice")
                        successMessage = ""
                    } else {
                        vm.addMultiChoiceCard(
                            deck, fields.question.value,
                            fields.choices[0].value,
                            fields.choices[1].value,
                            fields.choices[2].value,
                            fields.choices[3].value,
                            fields.correct.value
                        )
                        fields.question.value = ""
                        fields.choices = MutableList(4) { mutableStateOf("") }
                        fields.correct.value = '?'
                        successMessage = cardAdded
                    }
                },
                modifier = Modifier.padding(top = 4.dp),
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