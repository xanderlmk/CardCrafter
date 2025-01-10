package com.example.flashcards.views.cardViews.editCardViews


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.flashcards.R
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCard
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.ui.theme.GetModifier
import com.example.flashcards.views.miscFunctions.EditTextField
import com.example.flashcards.views.miscFunctions.PickAnswerChar
import com.example.flashcards.views.miscFunctions.createChoiceCardDetails

@Composable
fun EditChoiceCard(
    multiChoiceCard: MultiChoiceCard,
    fields: Fields,
    getModifier: GetModifier
) {
    val cardDetails by remember {
        mutableStateOf(
                createChoiceCardDetails(multiChoiceCard)
        )
    }
    fields.question = rememberSaveable { mutableStateOf(cardDetails.question.value) }
    fields.choices[0] = rememberSaveable { mutableStateOf(cardDetails.choices[0].value) }
    fields.choices[1] = rememberSaveable { mutableStateOf(cardDetails.choices[1].value) }
    fields.choices[2] = rememberSaveable { mutableStateOf(cardDetails.choices[2].value) }
    fields.choices[3] = rememberSaveable { mutableStateOf(cardDetails.choices[3].value) }
    fields.correct = rememberSaveable { mutableStateOf(cardDetails.correct.value) }


    EditTextField(
        value = fields.question.value,
        onValueChanged = {
            fields.question.value = it
        },
        labelStr = stringResource(R.string.question),
        modifier = Modifier.fillMaxWidth()
    )
    EditTextField(
        value = fields.choices[0].value,
        onValueChanged = { fields.choices[0].value = it },
        labelStr = stringResource(R.string.choice_a),
        modifier = Modifier.fillMaxWidth()
    )
    EditTextField(
        value = fields.choices[1].value,
        onValueChanged = { fields.choices[1].value = it },
        labelStr = stringResource(R.string.choice_b),
        modifier = Modifier.fillMaxWidth()
    )
    EditTextField(
        value = fields.choices[2].value,
        onValueChanged = { fields.choices[2].value = it },
        labelStr = stringResource(R.string.choice_c),
        modifier = Modifier.fillMaxWidth()
    )
    EditTextField(
        value = fields.choices[3].value,
        onValueChanged = { fields.choices[3].value = it },
        labelStr = stringResource(R.string.choice_d),
        modifier = Modifier.fillMaxWidth()
    )
    PickAnswerChar(fields, getModifier)
}