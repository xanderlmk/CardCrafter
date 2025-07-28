package com.belmontCrest.cardCrafter.views.cardViews.addCardViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.AddCardViewModel
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.uiFunctions.EditTextField
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.EditTextFieldNonDone
import com.belmontCrest.cardCrafter.views.miscFunctions.PickAnswerChar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddMultiChoiceCard(
    vm: AddCardViewModel, deck: Deck, getUIStyle: GetUIStyle
) {
    var successMessage by remember { mutableStateOf("") }
    val fields by vm.fields.collectAsStateWithLifecycle()
    val errorMessage by vm.errorMessage.collectAsStateWithLifecycle()
    val fillOutFields = stringResource(R.string.fill_out_all_fields)
    val cardAdded = stringResource(R.string.card_added)
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
            color = getUIStyle.titleColor()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            EditTextFieldNonDone(
                value = fields.question,
                onValueChanged = { newText ->
                    vm.updateQ(newText)
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
                        if (fields.correct == 'a') {
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
                value = fields.choices[0],
                onValueChanged = { newText ->
                    vm.updateCh(newText, 0)
                },
                labelStr = stringResource(R.string.choice_a),
                modifier = Modifier
                    .weight(1f),
                inputColor =
                    if (fields.correct == 'a') {
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
                        if (fields.correct == 'b') {
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
                value = fields.choices[1],
                onValueChanged = { newText ->
                    vm.updateCh(newText, 1)
                },
                labelStr = stringResource(R.string.choice_b),
                modifier = Modifier
                    .weight(1f),
                inputColor =
                    if (fields.correct == 'b') {
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
                        if (fields.correct == 'c') {
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
                value = fields.choices[2],
                onValueChanged = { newText ->
                    vm.updateCh(newText, 2)
                },
                labelStr = stringResource(R.string.choice_c),
                modifier = Modifier
                    .weight(1f),
                inputColor =
                    if (fields.correct == 'c') {
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
                        if (fields.correct == 'd') {
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
                value = fields.choices[3],
                onValueChanged = { newText ->
                    vm.updateCh(newText, 3)
                },
                labelStr = stringResource(R.string.choice_d),
                modifier = Modifier
                    .weight(1f),
                inputColor =
                    if (fields.correct == 'd') {
                        getUIStyle.onCorrectChoice()
                    } else {
                        Color.Transparent
                    }
            )
        }

        PickAnswerChar(fields, getUIStyle) { correct ->
            vm.updateCor(correct)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (fields.question.isBlank() ||
                        fields.choices[0].isBlank() ||
                        fields.choices[1].isBlank() ||
                        fields.correct !in 'a'..'d'
                    ) {
                        vm.setErrorMessage(fillOutFields)
                        successMessage = ""
                    } else if (fields.choices[2].isBlank() &&
                        fields.choices[3].isNotBlank()
                    ) {
                        vm.setErrorMessage("Cannot skip choice C and fill choice D")
                        successMessage = ""
                    } else if ((fields.choices[2].isBlank() &&
                                fields.correct == 'c') ||
                        (fields.choices[3].isBlank() &&
                                fields.correct == 'd')
                    ) {
                        vm.setErrorMessage("Answer can't be a blank choice")
                        successMessage = ""
                    } else {
                        coroutineScope.launch {
                            vm.addMultiChoiceCard(
                                deck, fields.question,
                                fields.choices[0],
                                fields.choices[1],
                                fields.choices[2],
                                fields.choices[3],
                                fields.correct
                            )
                            successMessage = cardAdded
                        }
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
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(4.dp),
                fontSize = 16.sp
            )
        }
        if (successMessage.isNotEmpty()) {
            Text(
                text = successMessage,
                color = getUIStyle.titleColor(),
                modifier = Modifier.padding(4.dp),
                fontSize = 16.sp
            )
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
    }
}