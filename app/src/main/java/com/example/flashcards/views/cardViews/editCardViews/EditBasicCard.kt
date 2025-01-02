package com.example.flashcards.views.cardViews.editCardViews

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.flashcards.R
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.views.miscFunctions.EditTextField


@Composable
fun EditBasicCard(basicCard: BasicCard,
                  fields: Fields) {
    fields.question = remember {mutableStateOf(basicCard.question)}
    fields.answer =  remember { mutableStateOf(basicCard.answer) }

    EditTextField(
        value = fields.question.value,
        onValueChanged = { fields.question.value = it },
        labelStr =  stringResource(R.string.question),
        modifier = Modifier.fillMaxWidth()
    )
    EditTextField(
        value = fields.answer.value,
        onValueChanged = { fields.answer.value = it },
        labelStr = stringResource(R.string.answer),
        modifier = Modifier.fillMaxWidth()
    )
}

