package com.belmontCrest.cardCrafter.views.cardViews.editCardViews

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.controller.AppViewModelProvider
import com.belmontCrest.cardCrafter.controller.cardHandlers.returnCardTypeHandler
import com.belmontCrest.cardCrafter.controller.onClickActions.saveCard
import com.belmontCrest.cardCrafter.controller.onClickActions.updateCardType
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.EditingCardListViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.EditCardViewModel
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Card
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.ui.theme.editCardModifier
import kotlinx.coroutines.launch

class EditingCardView(
    private var editingCardListVM: EditingCardListViewModel,
    private var getUIStyle: GetUIStyle
) {
    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun EditFlashCardView(
        card: Card,
        fields: Fields,
        index: Int,
        onNavigateBack: () -> Unit
    ) {
        val editCardVM: EditCardViewModel = viewModel(factory = AppViewModelProvider.Factory)
        fields.newType =  rememberSaveable { mutableStateOf(card.type) }
        val fillOutfields = stringResource(R.string.fill_out_all_fields).toString()
        val errorMessage by editCardVM.errorMessage.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()
        val cardTypeChanged = rememberSaveable { mutableStateOf(false) }
        val sealedAllCTs by editingCardListVM.sealedAllCTs.collectAsStateWithLifecycle()

        Box(
            modifier = Modifier
                .boxViewsModifier(getUIStyle.getColorScheme())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = stringResource(R.string.edit_flashcard),
                        fontSize = 25.sp,
                        lineHeight = 30.sp,
                        textAlign = TextAlign.Center,
                        color = getUIStyle.titleColor(),
                        modifier = Modifier.editCardModifier()
                    )
                }
                val cardTypeHandler = returnCardTypeHandler(
                    fields.newType.value, card.type
                )
                if (fields.newType.value != card.type) {
                    cardTypeChanged.value = true
                }
                cardTypeHandler?.HandleCardEdit(
                    ct = sealedAllCTs.allCTs[index],
                    fields = fields,
                    changed = cardTypeChanged.value,
                    getUIStyle = getUIStyle
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onNavigateBack()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getUIStyle.secondaryButtonColor(),
                            contentColor = getUIStyle.buttonTextColor()
                        )
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                if (fields.newType.value == card.type) {
                                    val success = saveCard(
                                        fields, editCardVM,
                                        sealedAllCTs.allCTs[index]
                                    )
                                    if (success) {
                                        editCardVM.clearErrorMessage()
                                        onNavigateBack()
                                    } else {
                                        editCardVM.setErrorMessage(fillOutfields)
                                    }
                                } else {
                                    val success = updateCardType(
                                        fields, editCardVM,
                                        sealedAllCTs.allCTs[index],
                                        fields.newType.value
                                    )
                                    if (success) {
                                        editCardVM.clearErrorMessage()
                                        onNavigateBack()
                                    } else {
                                        editCardVM.setErrorMessage(fillOutfields)
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getUIStyle.secondaryButtonColor(),
                            contentColor = getUIStyle.buttonTextColor()
                        )
                    ) {
                        Text(stringResource(R.string.save))
                    }
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
