package com.belmontCrest.cardCrafter.views.cardViews.editCardViews

import android.os.Build
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.ui.theme.editCardModifier
import com.belmontCrest.cardCrafter.uiFunctions.CancelButton
import com.belmontCrest.cardCrafter.uiFunctions.SubmitButton
import com.belmontCrest.cardCrafter.uiFunctions.katex.KaTeXMenu
import com.belmontCrest.cardCrafter.uiFunctions.katex.SelectedAnnotation
import kotlinx.coroutines.launch

class EditingCardView(
    private var getUIStyle: GetUIStyle, private val navVM: NavViewModel
) {
    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun EditFlashCardView(
        ct: CT, fields: Fields, onNavigateBack: () -> Unit
    ) {
        val editCardVM: EditCardViewModel = viewModel(factory = AppViewModelProvider.Factory)
        fields.newType = rememberSaveable { mutableStateOf(ct.getCardType()) }
        val fillOutfields = stringResource(R.string.fill_out_all_fields).toString()
        val errorMessage by editCardVM.errorMessage.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()
        val cardTypeChanged = rememberSaveable { mutableStateOf(false) }
        val showKB by navVM.showKatexKeyboard.collectAsStateWithLifecycle()
        var offset by remember { mutableStateOf(Offset.Zero) }
        val resetOffset by navVM.resetOffset.collectAsStateWithLifecycle()
        var ktm by rememberSaveable { mutableStateOf(KaTeXMenu(null, SelectedAnnotation.Idle)) }
        //var initialPos by remember { mutableStateOf<Offset?>(null) }

        LaunchedEffect(resetOffset) {
            if (resetOffset) {
                offset = Offset.Zero
                navVM.resetDone()
            }
        }

        Box(
            modifier = Modifier
                .boxViewsModifier(getUIStyle.getColorScheme())
                .padding(top = 10.dp)
        ) {
            if (showKB) {
                BackHandler {
                    navVM.toggleKeyboard()
                }
                KaTeXMenu(
                    Modifier
                        .fillMaxSize()
                        /** .onGloballyPositioned { coordinates ->
                        initialPos = coordinates.localToWindow(Offset.Zero)
                        Log.i("KatexMenu", "$initialPos")
                        // Measure the menu’s total size (header + WebView):
                        }*/
                        .zIndex(2f)
                        .padding(6.dp),
                    { offset }, onDismiss = { navVM.toggleKeyboard() },
                    onOffset = { offset += it }, getUIStyle, //initialPos
                ) { notation, sa ->
                    ktm = KaTeXMenu(notation, sa)
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
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
                    vm = navVM,
                    getUIStyle = getUIStyle,
                    onUpdate = { ktm }
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
