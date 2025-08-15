package com.belmontCrest.cardCrafter.views.cardViews.editCardViews

import android.os.Build
import android.util.Log
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.controller.cardHandlers.getCardType
import com.belmontCrest.cardCrafter.controller.cardHandlers.returnCardTypeHandler
import com.belmontCrest.cardCrafter.controller.onClickActions.saveCard
import com.belmontCrest.cardCrafter.controller.onClickActions.updateCardType
import com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels.EditCardViewModel
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.model.application.preferences.PreferenceValues
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.ui.theme.editCardModifier
import com.belmontCrest.cardCrafter.uiFunctions.buttons.CancelButton
import com.belmontCrest.cardCrafter.uiFunctions.buttons.SubmitButton
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.KaTeXMenu
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.SelectedAnnotation
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.getWebView
import com.belmontCrest.cardCrafter.views.misc.collectNotationFieldsAsStates
import kotlinx.coroutines.launch

class EditingCardView(
    private var getUIStyle: GetUIStyle,
    private val pv: PreferenceValues,
) {
    @RequiresApi(Build.VERSION_CODES.Q)
    @Composable
    fun EditFlashCardView(
        ct: CT, newType: String, onNavigateBack: () -> Unit, editCardVM: EditCardViewModel
    ) {
        val (fields, showKB, selectedKB) = collectNotationFieldsAsStates(editCardVM)
        val fillOutfields = stringResource(R.string.fill_out_all_fields)
        val errorMessage by editCardVM.errorMessage.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()
        val cardTypeChanged = rememberSaveable { mutableStateOf(false) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        val resetOffset by editCardVM.resetOffset.collectAsStateWithLifecycle()
        val context = LocalContext.current
        var ktm by rememberSaveable { mutableStateOf(KaTeXMenu(null, SelectedAnnotation.Idle)) }
        val webView = remember(selectedKB) {
            getWebView(getUIStyle, context) { notation, sa ->
                ktm = KaTeXMenu(notation, sa)
            }
        }
        val webScrollState = rememberScrollState()
        DisposableEffect(webView) {
            onDispose {
                try {
                    webView.destroy()
                } catch (e: Exception) {
                    Log.w("KatexMenu", "Failed to destroy WebView: $e")
                }
            }
        }
        LaunchedEffect(resetOffset) {
            if (resetOffset) {
                offset = Offset.Zero; editCardVM.resetDone()
            }
        }

        Box(
            modifier = Modifier
                .boxViewsModifier(getUIStyle.getColorScheme())
                .padding(top = 10.dp)
        ) {
            if (showKB) {
                KaTeXMenu(
                    Modifier
                        .fillMaxSize()
                        .zIndex(2f)
                        .padding(6.dp),
                    offsetProvider = { offset },
                    onDismiss = { editCardVM.toggleKeyboard() },
                    onOffset = { offset += it },
                    getUIStyle = getUIStyle,
                    width = pv.width,
                    webView = webView,
                    scrollState = webScrollState,
                    height = pv.height
                ) {
                    ktm = KaTeXMenu("null", it)
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

                val cardTypeHandler = returnCardTypeHandler(newType, ct.getCardType())
                if (newType != ct.getCardType()) {
                    cardTypeChanged.value = true
                }
                cardTypeHandler?.HandleCardEdit(
                    vm = editCardVM, getUIStyle = getUIStyle, onUpdate = { ktm })
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(-1f)
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CancelButton(
                        onClick = { onNavigateBack() }, true, getUIStyle,
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                    )
                    SubmitButton(
                        onClick = {
                            coroutineScope.launch {
                                if (newType == ct.getCardType()) {
                                    val success = saveCard(fields, editCardVM, ct, newType, context)
                                    if (success) {
                                        editCardVM.clearErrorMessage()
                                        onNavigateBack()
                                    } else {
                                        editCardVM.setErrorMessage(fillOutfields)
                                    }
                                } else {
                                    val success =
                                        updateCardType(fields, editCardVM, ct, newType, context)
                                    if (success) {
                                        editCardVM.clearErrorMessage()
                                        onNavigateBack()
                                    } else {
                                        editCardVM.setErrorMessage(fillOutfields)
                                    }
                                }
                            }
                        },
                        true,
                        getUIStyle,
                        stringResource(R.string.save),
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
