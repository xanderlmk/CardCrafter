package com.belmontCrest.cardCrafter.views.cardViews.editCardViews


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.EditTextField
import com.belmontCrest.cardCrafter.uiFunctions.EditTextFieldNonDone
import com.belmontCrest.cardCrafter.views.miscFunctions.PickAnswerChar

@Composable
fun EditChoiceCard(
    fields: Fields,
    getUIStyle: GetUIStyle
) {


    EditTextFieldNonDone(
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
    PickAnswerChar(fields, getUIStyle)
}