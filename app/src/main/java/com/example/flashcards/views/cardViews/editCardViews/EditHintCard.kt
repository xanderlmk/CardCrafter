package com.example.flashcards.views.cardViews.editCardViews

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.flashcards.R
import com.example.flashcards.model.Fields
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.ui.theme.textColor
import com.example.flashcards.views.miscFunctions.GetModifier

@Composable
fun EditHintCard(hintCard: HintCard,
                  fields: Fields,
                 getModifiers: GetModifier) {
    fields.question = remember { mutableStateOf(hintCard.question) }
    fields.middleField = remember { mutableStateOf(hintCard.hint) }
    fields.answer = remember { mutableStateOf(hintCard.answer) }

    Text(
        text = stringResource(R.string.edit_flashcard),
        fontSize = 35.sp,
        lineHeight = 40.sp,
        textAlign = TextAlign.Center,
        color = getModifiers.titleColor(),
        modifier = getModifiers.editCardModifier()
    )
    TextField(
        value = fields.question.value,
        onValueChange = { fields.question.value = it },
        label = { Text(stringResource(R.string.question), color = textColor) },
        modifier = Modifier.fillMaxWidth()
    )
    TextField(
        value = fields.middleField.value,
        onValueChange = { fields.middleField.value = it },
        label = { Text(stringResource(R.string.middle_field), color = textColor) },
        modifier = Modifier.fillMaxWidth()
    )
    TextField(
        value = fields.answer.value,
        onValueChange = { fields.answer.value = it },
        label = { Text(stringResource(R.string.answer), color = textColor) },
        modifier = Modifier.fillMaxWidth()
    )
}