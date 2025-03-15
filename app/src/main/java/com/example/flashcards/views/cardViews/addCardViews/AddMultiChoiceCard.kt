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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashcards.R
import com.example.flashcards.controller.viewModels.cardViewsModels.AddCardViewModel
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.views.miscFunctions.EditTextField
import com.example.flashcards.ui.theme.GetUIStyle
import com.example.flashcards.views.miscFunctions.EditTextFieldNonDone
import com.example.flashcards.views.miscFunctions.PickAnswerChar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddMultiChoiceCard(
    vm: AddCardViewModel, deck: Deck,
    fields: Fields, getUIStyle: GetUIStyle
) {
    var successMessage by remember { mutableStateOf("") }
    val errorMessage by vm.errorMessage.collectAsStateWithLifecycle()
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
            color = getUIStyle.titleColor(),
            modifier = Modifier
                .padding(top = 10.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            EditTextFieldNonDone(
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
            color = getUIStyle.titleColor(),
            modifier = Modifier
                .padding(top = 8.dp)
                .background(
                    color =
                    if (fields.correct.value == 'a') {
                        getUIStyle.correctChoice()
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
                    getUIStyle.onCorrectChoice()
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
            color = getUIStyle.titleColor(),
            modifier = Modifier
                .padding(top = 8.dp)
                .background(
                    color =
                    if (fields.correct.value == 'b') {
                        getUIStyle.correctChoice()
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
                    getUIStyle.onCorrectChoice()
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
            color = getUIStyle.titleColor(),
            modifier = Modifier
                .padding(top = 8.dp)
                .background(
                    color =
                    if (fields.correct.value == 'c') {
                        getUIStyle.correctChoice()
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
                    getUIStyle.onCorrectChoice()
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
            color = getUIStyle.titleColor(),
            modifier = Modifier
                .padding(top = 8.dp)
                .background(
                    color =
                    if (fields.correct.value == 'd') {
                        getUIStyle.correctChoice()
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
                    getUIStyle.onCorrectChoice()
                } else {
                    Color.Transparent
                }
            )
        }

        PickAnswerChar(fields, getUIStyle)

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
                color = getUIStyle.titleColor(),
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
                        fields.resetFields()
                        successMessage = cardAdded
                        fields.cardsAdded.value += 1
                    }
                },
                modifier = Modifier.padding(top = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = getUIStyle.secondaryButtonColor(),
                    contentColor = getUIStyle.buttonTextColor()
                )
            ) {
                Text(stringResource(R.string.submit))
            }
        }
    }
}