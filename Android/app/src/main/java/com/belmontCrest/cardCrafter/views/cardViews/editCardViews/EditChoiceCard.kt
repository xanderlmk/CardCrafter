package com.belmontCrest.cardCrafter.views.cardViews.editCardViews


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels.EditCardViewModel
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.EditTextField
import com.belmontCrest.cardCrafter.uiFunctions.EditTextFieldNonDone
import com.belmontCrest.cardCrafter.views.misc.PickAnswerChar

@Composable
fun EditChoiceCard(vm: EditCardViewModel, getUIStyle: GetUIStyle) {
    val fields by vm.fields.collectAsStateWithLifecycle()
    EditTextFieldNonDone(
        value = fields.question,
        onValueChanged = { vm.updateQ(it) },
        labelStr = stringResource(R.string.question),
        modifier = Modifier.fillMaxWidth()
    )
    EditTextField(
        value = fields.choices[0],
        onValueChanged = { vm.updateCh(it, 0) },
        labelStr = stringResource(R.string.choice_a),
        modifier = Modifier.fillMaxWidth()
    )
    EditTextField(
        value = fields.choices[1],
        onValueChanged = { vm.updateCh(it, 1) },
        labelStr = stringResource(R.string.choice_b),
        modifier = Modifier.fillMaxWidth()
    )
    EditTextField(
        value = fields.choices[2],
        onValueChanged = { vm.updateCh(it, 2) },
        labelStr = stringResource(R.string.choice_c),
        modifier = Modifier.fillMaxWidth()
    )
    EditTextField(
        value = fields.choices[3],
        onValueChanged = { vm.updateCh(it, 3) },
        labelStr = stringResource(R.string.choice_d),
        modifier = Modifier.fillMaxWidth()
    )
    PickAnswerChar(fields, getUIStyle) { correct -> vm.updateCor(correct) }
}