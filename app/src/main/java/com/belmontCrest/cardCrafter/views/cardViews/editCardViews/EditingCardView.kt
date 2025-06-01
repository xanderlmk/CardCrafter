package com.belmontCrest.cardCrafter.views.cardViews.editCardViews

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.controller.cardHandlers.getCardType
import com.belmontCrest.cardCrafter.model.application.AppViewModelProvider
import com.belmontCrest.cardCrafter.controller.cardHandlers.returnCardTypeHandler
import com.belmontCrest.cardCrafter.controller.onClickActions.saveCard
import com.belmontCrest.cardCrafter.controller.onClickActions.updateCardType
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.EditCardViewModel
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.model.Type
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.editCardModifier
import com.belmontCrest.cardCrafter.ui.theme.scrollableBoxViewModifier
import com.belmontCrest.cardCrafter.uiFunctions.CancelButton
import com.belmontCrest.cardCrafter.uiFunctions.SubmitButton
import kotlinx.coroutines.launch

class EditingCardView(
    private var getUIStyle: GetUIStyle
) {
    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun EditFlashCardView(
        ct: CT,
        fields: Fields,
        onNavigateBack: () -> Unit
    ) {
        val editCardVM: EditCardViewModel = viewModel(factory = AppViewModelProvider.Factory)
        fields.newType = rememberSaveable { mutableStateOf(ct.getCardType()) }
        val fillOutfields = stringResource(R.string.fill_out_all_fields).toString()
        val errorMessage by editCardVM.errorMessage.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()
        val cardTypeChanged = rememberSaveable { mutableStateOf(false) }
        val showKB by editCardVM.showKatexKeyboard.collectAsStateWithLifecycle()
        val selectedKB by editCardVM.selectedKB.collectAsStateWithLifecycle()

        Box(
            modifier = Modifier
                .scrollableBoxViewModifier(rememberScrollState(), getUIStyle.getColorScheme())
        ) {
            if (fields.newType.value == Type.NOTATION && selectedKB != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .size(30.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { editCardVM.toggleKeyboard() },
                                onLongPress = { editCardVM.resetOffset() }
                            )
                        }
                ) {
                    if (!showKB) {
                        Icon(
                            painterResource(R.drawable.twotone_keyboard),
                            contentDescription = "Keyboard"
                        )
                    } else {
                        Icon(
                            painterResource(R.drawable.twotone_keyboard_hide),
                            contentDescription = "Hide Keyboard"
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.edit_flashcard),
                    fontSize = 25.sp,
                    lineHeight = 30.sp,
                    textAlign = TextAlign.Center,
                    color = getUIStyle.titleColor(),
                    modifier = Modifier.editCardModifier()
                )

                val cardTypeHandler = returnCardTypeHandler(
                    fields.newType.value, ct.getCardType()
                )
                if (fields.newType.value != ct.getCardType()) {
                    cardTypeChanged.value = true
                }
                cardTypeHandler?.HandleCardEdit(
                    ct = ct, fields = fields,
                    changed = cardTypeChanged.value,
                    vm = editCardVM,
                    getUIStyle = getUIStyle,
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(2f)
                        .padding(6.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(-1f)
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CancelButton(
                        onClick = {
                            onNavigateBack()
                        }, true, getUIStyle,
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                    )
                    SubmitButton(
                        onClick = {
                            coroutineScope.launch {
                                if (fields.newType.value == ct.getCardType()) {
                                    val success = saveCard(fields, editCardVM, ct)
                                    if (success) {
                                        editCardVM.clearErrorMessage()
                                        onNavigateBack()
                                    } else {
                                        editCardVM.setErrorMessage(fillOutfields)
                                    }
                                } else {
                                    val success = updateCardType(
                                        fields, editCardVM, ct, fields.newType.value
                                    )
                                    if (success) {
                                        editCardVM.clearErrorMessage()
                                        onNavigateBack()
                                    } else {
                                        editCardVM.setErrorMessage(fillOutfields)
                                    }
                                }
                            }
                        }, true, getUIStyle, stringResource(R.string.save),
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                    )
                }
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
