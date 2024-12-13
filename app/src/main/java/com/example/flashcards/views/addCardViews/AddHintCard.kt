package com.example.flashcards.views.addCardViews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.R
import com.example.flashcards.controller.viewModels.HintCardViewModel
import com.example.flashcards.model.Fields
import com.example.flashcards.ui.theme.buttonColor
import com.example.flashcards.ui.theme.textColor
import com.example.flashcards.ui.theme.titleColor
import com.example.flashcards.views.miscFunctions.EditTextField
import kotlinx.coroutines.delay

@Composable
fun AddHintCard(viewModel: HintCardViewModel,deckId: Int, fields: Fields) {
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    val fillOutFields = stringResource(R.string.fill_out_all_fields).toString()
    val cardAdded = stringResource(R.string.card_added).toString()

    Text(
        text = stringResource(R.string.question),
        fontSize = 45.sp,
        textAlign = TextAlign.Center,
        lineHeight = 116.sp,
        color = titleColor,
        modifier = Modifier
            .padding(top = 15.dp)
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
        text = stringResource(R.string.middle_field),
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
            value = fields.middleField.value,
            onValueChanged = { newText ->
                fields.middleField.value =
                    newText
            },
            labelStr = stringResource(R.string.middle_field),
            modifier = Modifier
                .weight(1f)
        )
    }
    Text(
        text = stringResource(R.string.answer),
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
            value = fields.answer.value,
            onValueChanged = { newText ->
                fields.answer.value =
                    newText
            },
            labelStr = stringResource(R.string.answer),
            modifier = Modifier
                .weight(1f)
        )
    }

    if (errorMessage.isNotEmpty()) {
        Text(
            text = errorMessage,
            color = Color.Red,
            modifier = Modifier.padding(8.dp),
            fontSize = 16.sp
        )
    } else {
        Spacer(modifier = Modifier.padding(20.dp))
    }
    if (successMessage.isNotEmpty()) {
        Text(
            text = successMessage,
            color = textColor,
            modifier = Modifier.padding(8.dp),
            fontSize = 16.sp
        )
    } else {
        Spacer(modifier = Modifier.padding(20.dp))
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
                if (fields.question.value.isBlank() ||
                    fields.answer.value.isBlank() ||
                    fields.middleField.value.isBlank()) {
                    errorMessage = fillOutFields
                    successMessage = ""
                } else {
                    viewModel.addHintCard(deckId, fields.question.value,
                        fields.middleField.value, fields.answer.value)
                    fields.question.value = ""
                    fields.middleField.value = ""
                    fields.answer.value = ""
                    errorMessage = ""
                    successMessage = cardAdded
                }
            },
            modifier = Modifier.padding(top = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = textColor
            )
        ) {
            Text(stringResource(R.string.submit))
        }
    }
}