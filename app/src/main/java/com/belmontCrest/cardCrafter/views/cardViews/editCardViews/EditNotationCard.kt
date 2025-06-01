package com.belmontCrest.cardCrafter.views.cardViews.editCardViews

import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.EditCardViewModel
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.model.uiModels.SelectedKeyboard
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.LatexKeyboard
import com.belmontCrest.cardCrafter.uiFunctions.katex.KaTeXMenu
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage

@Composable
fun EditNotationCard(
    fields: Fields, vm: EditCardViewModel,
    getUIStyle: GetUIStyle, modifier: Modifier
) {
    var steps by rememberSaveable { mutableIntStateOf(fields.stringList.size) }
    val focusRequester = remember { FocusRequester() }
    var selectedQSymbol by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedASymbol by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedSLSymbols = rememberSaveable { mutableListOf<String?>() }
    val showKB by vm.showKatexKeyboard.collectAsStateWithLifecycle()
    val selectedKB by vm.selectedKB.collectAsStateWithLifecycle()
    var offset by remember { mutableStateOf(Offset.Zero) }
    val context = LocalContext.current
    val resetOffset by vm.resetOffset.collectAsStateWithLifecycle()
    LaunchedEffect(resetOffset) {
        if (resetOffset) {
            offset = Offset(0f, 0f)
            vm.resetDone()
        }
    }
    Box {
        if (showKB) {
            KaTeXMenu(
                modifier.fillMaxSize(), offset, onDismiss = { vm.toggleKeyboard() },
                onOffset = { offset += it }, getUIStyle
            ) { symbol ->
                when (val sel = selectedKB) {
                    is SelectedKeyboard.Question -> {
                        selectedQSymbol = symbol
                    }

                    is SelectedKeyboard.Step -> {
                        selectedSLSymbols[sel.index] = symbol
                    }

                    is SelectedKeyboard.Answer -> {
                        selectedASymbol = symbol
                    }

                    else -> {
                        showToastMessage(context, "Please select a text field first.")
                        /* No Keyboard, Do Nothing */
                    }
                }

            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LatexKeyboard(
                value = fields.question.value,
                onValueChanged = { fields.question.value = it },
                labelStr = stringResource(R.string.question),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.hasFocus) {
                            vm.updateSelectedKB(SelectedKeyboard.Question)
                            //lastSelectedKB = SelectedKeyboard.Question
                            Log.d("AddCardVM", "Focused on Question")
                        }
                    }
                    .focusable(),
                focusRequester = focusRequester,
                symbol = selectedQSymbol,
                onSymbol = { selectedQSymbol = null }
            )
            if (steps == 0) {
                Button(
                    onClick = {
                        fields.stringList.add(mutableStateOf(""))
                        selectedSLSymbols.add(null)
                        steps += 1
                    }, modifier = Modifier
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getUIStyle.secondaryButtonColor(),
                        contentColor = getUIStyle.buttonTextColor()
                    ), enabled = true
                ) {
                    Text(
                        text = "Add a step",
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                fields.stringList.forEachIndexed { index, string ->
                    val indexedModifier = if (index == 0) {
                        Modifier.padding(top = 5.dp, start = 0.5.dp, end = 0.5.dp, bottom = 1.dp)
                    } else {
                        Modifier.padding(1.dp)
                    }
                    LatexKeyboard(
                        value = string.value,
                        onValueChanged = { string.value = it },
                        labelStr = "Step: ${index + 1}",
                        modifier = indexedModifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onFocusChanged { focusState ->
                                if (focusState.hasFocus) {
                                    vm.updateSelectedKB(SelectedKeyboard.Step(index))
                                    //lastSelectedKB = SelectedKeyboard.Step(index)
                                    Log.d("AddCardVM", "Focused on Step #${index + 1}")
                                }
                            }
                            .focusable(),
                        focusRequester = focusRequester,
                        symbol = selectedSLSymbols[index],
                        onSymbol = { selectedSLSymbols[index] = null }
                    )
                }
                Button(
                    onClick = {
                        fields.stringList.add(mutableStateOf(""))
                        selectedSLSymbols.add(null)
                        steps += 1
                    }, modifier = Modifier
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getUIStyle.secondaryButtonColor(),
                        contentColor = getUIStyle.buttonTextColor()
                    ), enabled = true
                ) {
                    Text(
                        text = "Add a step",
                        textAlign = TextAlign.Center,
                    )
                }
                Button(
                    onClick = {
                        if (steps > 0) {
                            steps -= 1
                            fields.stringList.removeAt(steps)
                            selectedSLSymbols.removeAt(steps)
                        }
                    },
                    modifier = Modifier.padding(top = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getUIStyle.secondaryButtonColor(),
                        contentColor = getUIStyle.buttonTextColor()
                    )
                ) {
                    Text("Remove Step")
                }
            }
            LatexKeyboard(
                value = fields.answer.value,
                onValueChanged = { fields.answer.value = it },
                labelStr = stringResource(R.string.answer),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.hasFocus) {
                            vm.updateSelectedKB(SelectedKeyboard.Answer)
                            //lastSelectedKB = SelectedKeyboard.Answer
                            Log.d("AddCardVM", "Focused on Answer")
                        } else {
                            vm.resetSelectedKB()
                            Log.d("AddCardVM", "Answer lost focus.")
                        }
                    }
                    .focusable(),
                focusRequester = focusRequester,
                symbol = selectedASymbol,
                onSymbol = { selectedASymbol = null }
            )
        }
    }
}