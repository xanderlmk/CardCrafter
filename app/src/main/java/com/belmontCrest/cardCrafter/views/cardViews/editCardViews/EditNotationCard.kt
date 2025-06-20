package com.belmontCrest.cardCrafter.views.cardViews.editCardViews

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.EditCardViewModel
import com.belmontCrest.cardCrafter.model.ui.states.SelectedKeyboard
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.LatexKeyboard
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.KaTeXMenu
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.SelectedAnnotation
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage
import com.belmontCrest.cardCrafter.views.miscFunctions.collectNotationFieldsAsStates
import com.belmontCrest.cardCrafter.views.miscFunctions.collectTextRangesAsStates


private object KeyboardInputs {
    const val KK = "KatexKeyBoard"
}

@Composable
fun EditNotationCard(
    vm: EditCardViewModel,
    getUIStyle: GetUIStyle, onUpdate: () -> KaTeXMenu
) {
    val (fields, _, selectedKB) = collectNotationFieldsAsStates(vm)
    val kk = KeyboardInputs.KK
    var selectedQSymbol by rememberSaveable {
        mutableStateOf(KaTeXMenu(null, SelectedAnnotation.Idle))
    }
    var selectedASymbol by rememberSaveable {
        mutableStateOf(KaTeXMenu(null, SelectedAnnotation.Idle))
    }
    val selectedSLSymbols by vm.selectedSLSymbols.collectAsStateWithLifecycle()
    val (selection, composition) = collectTextRangesAsStates(vm)
    val context = LocalContext.current
    LaunchedEffect(Unit) { vm.onCreate() }
    LaunchedEffect(onUpdate()) {
        when (val sel = selectedKB) {
            is SelectedKeyboard.Question -> {
                selectedQSymbol = onUpdate()
            }

            is SelectedKeyboard.Step -> {
                vm.updateSelectedSymbol(onUpdate(), sel.index)
            }

            is SelectedKeyboard.Answer -> {
                selectedASymbol = onUpdate()
            }

            else -> {
                if (onUpdate().notation != null) {
                    showToastMessage(context, "Please select a text field first.")
                }
                /* No Keyboard, Do Nothing */
            }
        }
    }

    LatexKeyboard(
        value = fields.question,
        onValueChanged = { vm.updateQ(it) },
        labelStr = stringResource(R.string.question),
        modifier = Modifier
            .fillMaxWidth(),
        kt = selectedQSymbol,
        onFocusChanged = {
            Log.d(kk, "Focused on Question")
            vm.updateSelectedKB(SelectedKeyboard.Question)
        }, selection = selection, composition = composition,
        onUpdateTR = { sel, com -> vm.updateTRs(sel, com) },
        selectedKeyboard = selectedKB, actualKeyboard = SelectedKeyboard.Question,
        onIdle = { selectedQSymbol = KaTeXMenu(null, SelectedAnnotation.Idle) }
    )
    if (fields.steps.isEmpty()) {
        Button(
            onClick = {
                vm.addStep()
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
        fields.steps.forEachIndexed { index, string ->
            if (selectedSLSymbols.size != fields.steps.size) return@forEachIndexed
            val indexedModifier = if (index == 0) {
                Modifier.padding(top = 5.dp, start = 0.5.dp, end = 0.5.dp, bottom = 1.dp)
            } else {
                Modifier.padding(vertical = 2.dp)
            }
            LatexKeyboard(
                value = string,
                onValueChanged = { vm.updateStep(it, index) },
                labelStr = "Step: ${index + 1}",
                modifier = indexedModifier
                    .fillMaxWidth(),
                onFocusChanged = {
                    vm.updateSelectedKB(SelectedKeyboard.Step(index))
                    Log.d(kk, "Focused on Step #${index + 1}")
                }, kt = selectedSLSymbols[index], selection = selection, composition = composition,
                onUpdateTR = { sel, com -> vm.updateTRs(sel, com) },
                selectedKeyboard = selectedKB,
                actualKeyboard = SelectedKeyboard.Step(index),
                onIdle = {
                    vm.updateSelectedSymbol(KaTeXMenu(null, SelectedAnnotation.Idle), index)
                }
            )
        }
        Button(
            onClick = {
                vm.addStep()
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
                if (fields.steps.isNotEmpty()) {
                    val currently = selectedKB
                    if (currently is SelectedKeyboard.Step &&
                        (currently.index - 1) == fields.steps.lastIndex
                    ) {
                        vm.resetSelectedKB()
                        Log.d(kk, "Reset since Step #${currently.index} lost focus")
                    }
                    vm.removeStep()
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
        value = fields.answer,
        onValueChanged = { vm.updateA(it) },
        labelStr = stringResource(R.string.answer),
        modifier = Modifier
            .fillMaxWidth(),
        onFocusChanged = {
            vm.updateSelectedKB(SelectedKeyboard.Answer)
            //lastSelectedKB = SelectedKeyboard.Answer
            Log.d(kk, "Focused on Answer")
        }, kt = selectedASymbol, selection = selection, composition = composition,
        onUpdateTR = { sel, com -> vm.updateTRs(sel, com) },
        selectedKeyboard = selectedKB, actualKeyboard = SelectedKeyboard.Answer,
        onIdle = { selectedASymbol = KaTeXMenu(null, SelectedAnnotation.Idle) }
    )
}
