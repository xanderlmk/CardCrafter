package com.belmontCrest.cardCrafter.views.cardViews.editCardViews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.LatexKeyboard

@Composable
fun EditNotationCard(
    fields: Fields,
    getUIStyle: GetUIStyle
) {
    var steps by rememberSaveable { mutableIntStateOf(fields.stringList.size) }
    val focusRequester = remember { FocusRequester() }
    var selectedQSymbol by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedASymbol by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedSLSymbols = rememberSaveable { mutableListOf<String?>() }

    LatexKeyboard(
        value = fields.question.value,
        onValueChanged = { fields.question.value = it },
        labelStr = stringResource(R.string.question),
        modifier = Modifier.fillMaxWidth(),
        focusRequester = focusRequester,
        symbol = selectedQSymbol,
        onSymbol = { selectedQSymbol = null }
    )
    if (steps == 0) {
        Text(
            text = "Add a step",
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    steps = 1
                    fields.stringList.add(mutableStateOf(""))
                    selectedSLSymbols.add(null)
                }
                .padding(8.dp),
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
    } else {
        fields.stringList.forEachIndexed { index, string ->
            LatexKeyboard(
                value = string.value,
                onValueChanged = { string.value = it },
                labelStr = "Step: ${index + 1}",
                modifier = Modifier.fillMaxWidth(),
                focusRequester = focusRequester,
                symbol = selectedSLSymbols[index],
                onSymbol = { selectedSLSymbols[index] = null }
            )
        }
        Text(
            text = "Add a step",
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    steps += 1
                    fields.stringList.add(mutableStateOf(""))
                    selectedSLSymbols.add(null)
                }
                .padding(8.dp),
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
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
        modifier = Modifier.fillMaxWidth(),
        focusRequester = focusRequester,
        symbol = selectedASymbol,
        onSymbol = { selectedASymbol = null }
    )
}