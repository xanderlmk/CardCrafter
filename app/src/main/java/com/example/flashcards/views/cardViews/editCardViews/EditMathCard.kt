package com.example.flashcards.views.cardViews.editCardViews

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.R
import com.example.flashcards.model.tablesAndApplication.MathCard
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.ui.theme.GetModifier
import com.example.flashcards.views.miscFunctions.EditTextFieldNonDone
import com.example.flashcards.views.miscFunctions.createMathCardDetails

@Composable
fun EditMathCard(
    mathCard: MathCard,
    fields: Fields,
    getModifier : GetModifier
) {

    val cardDetails by remember {
        mutableStateOf(
            createMathCardDetails(
                mathCard.question,
                mathCard.steps,
                mathCard.answer
            )
        )
    }

    fields.question = rememberSaveable { mutableStateOf(cardDetails.question.value) }
    fields.stringList = rememberSaveable { cardDetails.stringList }
    fields.answer = rememberSaveable { mutableStateOf(cardDetails.answer.value) }
    var steps by rememberSaveable { mutableIntStateOf(cardDetails.stringList.size) }

    EditTextFieldNonDone(
        value = fields.question.value,
        onValueChanged = { fields.question.value = it },
        labelStr = stringResource(R.string.question),
        modifier = Modifier.fillMaxWidth()
    )
    if (steps == 0){
        Text(
            text = "Add a step",
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    steps = 1
                    fields.stringList.add(mutableStateOf(""))
                }
                .padding(8.dp),
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
    } else {
        var index = 0
        fields.stringList.forEach { string ->
            index += 1
            EditTextFieldNonDone(
                value = string.value,
                onValueChanged = { string.value = it },
                labelStr = "Step: $index",
                modifier = Modifier.fillMaxWidth()
            )
        }
        Text(
            text = "Add a step",
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    steps += 1
                    fields.stringList.add(mutableStateOf(""))
                }
                .padding(8.dp),
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
        Button(
            onClick = {
                steps -= 1
                fields.stringList.removeAt(steps)
            },
            modifier = Modifier.padding(top = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = getModifier.secondaryButtonColor(),
                contentColor = getModifier.buttonTextColor()
            )
        ) {
            Text("Remove Step")
        }
    }
    EditTextFieldNonDone(
        value = fields.answer.value,
        onValueChanged = { fields.answer.value = it },
        labelStr = stringResource(R.string.answer),
        modifier = Modifier.fillMaxWidth()
    )
}