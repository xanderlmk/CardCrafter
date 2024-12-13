package com.example.flashcards.views.editCardViews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.R
import com.example.flashcards.controller.viewModels.HintCardViewModel
import com.example.flashcards.model.Fields
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.ui.theme.buttonColor
import com.example.flashcards.ui.theme.textColor
import com.example.flashcards.ui.theme.titleColor

@Composable
fun EditHintCard(hintCard: HintCard, onDismiss: () -> Unit,
                  hintViewModel: HintCardViewModel,
                  fields: Fields) {
    fields.question = remember { mutableStateOf(hintCard.question) }
    fields.middleField = remember { mutableStateOf(hintCard.hint) }
    fields.answer = remember { mutableStateOf(hintCard.answer) }
    var question by remember { mutableStateOf(TextFieldValue(fields.question.value)) }
    var middleField by remember { mutableStateOf(TextFieldValue(fields.middleField.value)) }
    var answer by remember { mutableStateOf(TextFieldValue(fields.answer.value)) }
    var errorMessage by remember { mutableStateOf("") }
    val fillOutfields = stringResource(R.string.fill_out_all_fields)

    Text(
        text = stringResource(R.string.edit_flashcard),
        fontSize = 40.sp,
        lineHeight = 42.sp,
        textAlign = TextAlign.Center,
        color = titleColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
            .wrapContentHeight(Alignment.CenterVertically)
    )
    TextField(
        value = question,
        onValueChange = { question = it },
        label = { Text(stringResource(R.string.question)) },
        modifier = Modifier.fillMaxWidth()
    )
    TextField(
        value = middleField,
        onValueChange = { middleField = it },
        label = { Text(stringResource(R.string.middle_field)) },
        modifier = Modifier.fillMaxWidth()
    )
    TextField(
        value = answer,
        onValueChange = { answer = it },
        label = { Text(stringResource(R.string.answer)) },
        modifier = Modifier.fillMaxWidth()
    )
    if (errorMessage.isNotEmpty()) {
        Text(
            text = errorMessage,
            color = Color.Red,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
        )
    } else {
        Spacer(modifier = Modifier.padding(12.dp))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = { onDismiss() },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = textColor
            )
        ) {
            Text(stringResource(R.string.cancel))
        }
        Button(
            onClick = {
                // Handle updating the flashcard logic here
                // e.g., viewModel.updateCard(card.copy(question = question.text, answer = answer.text))
                if (question.text.isNotBlank() && answer.text.isNotBlank()
                    && middleField.text.isNotBlank()
                ) {
                    hintViewModel.updateHintCard(
                        hintCard.cardId,
                        question.text,
                        middleField.text,
                        answer.text
                    )
                    onDismiss()
                } else {
                    errorMessage = fillOutfields.toString()
                }
            },
            modifier = Modifier
                .weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = textColor
            )
        ) {
            Text(stringResource(R.string.save))
        }
    }
}