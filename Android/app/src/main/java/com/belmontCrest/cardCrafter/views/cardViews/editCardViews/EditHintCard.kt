package com.belmontCrest.cardCrafter.views.cardViews.editCardViews

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.EditCardViewModel
import com.belmontCrest.cardCrafter.uiFunctions.EditTextFieldNonDone

@Composable
fun EditHintCard(vm: EditCardViewModel) {
    val fields by vm.fields.collectAsStateWithLifecycle()
    EditTextFieldNonDone(
        value = fields.question,
        onValueChanged = { vm.updateQ(it) },
        labelStr = stringResource(R.string.question),
        modifier = Modifier.fillMaxWidth()
    )
    EditTextFieldNonDone(
        value = fields.middle,
        onValueChanged = { vm.updateM(it) },
        labelStr = stringResource(R.string.hint_field),
        modifier = Modifier.fillMaxWidth()
    )
    EditTextFieldNonDone(
        value = fields.answer,
        onValueChanged = { vm.updateA(it) },
        labelStr = stringResource(R.string.answer),
        modifier = Modifier.fillMaxWidth()
    )
}