package com.belmontCrest.cardCrafter.views.miscFunctions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.belmontCrest.cardCrafter.R
import kotlinx.coroutines.delay
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.belmontCrest.cardCrafter.controller.cardHandlers.returnReviewsLeft
import com.belmontCrest.cardCrafter.localDatabase.tables.PartOfQorA
import com.belmontCrest.cardCrafter.model.ui.Fields
import com.belmontCrest.cardCrafter.model.ui.SealedDueCTs
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle


@Composable
fun AgainText(getUIStyle: GetUIStyle) {
    Text(
        "-----",
        color = getUIStyle.titleColor(),
        fontSize = 12.sp,
        lineHeight = 14.sp
    )
}

@Composable
fun HardText(
    updatedDueCards: SealedDueCTs,
    index: Int, hard: Int,
    getUIStyle: GetUIStyle
) {
    Text(
        text =
            if (returnReviewsLeft(updatedDueCards.allCTs[index]) == 1) {
                "$hard " + stringResource(R.string.days)
            } else {
                "${returnReviewsLeft(updatedDueCards.allCTs[index])} " + "reviews left"
            },
        color = getUIStyle.titleColor(),
        fontSize = 12.sp,
        lineHeight = 14.sp
    )

}

@Composable
fun GoodText(
    updatedDueCards: SealedDueCTs,
    index: Int, good: Int,
    getUIStyle: GetUIStyle
) {
    Text(
        text =
            if (returnReviewsLeft(updatedDueCards.allCTs[index]) == 1) {
                "$good " + stringResource(R.string.days)
            } else {
                "${
                    returnReviewsLeft(updatedDueCards.allCTs[index]) - 1
                } " + "reviews left"
            },
        color = getUIStyle.titleColor(),
        fontSize = 12.sp,
        lineHeight = 14.sp
    )
}

@Composable
fun NoDueCards(getUIStyle: GetUIStyle) {
    var delay by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(500)
        delay = true
    }
    if (delay) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(R.string.no_due_cards),
                fontSize = 25.sp,
                lineHeight = 26.sp,
                textAlign = TextAlign.Center,
                color = getUIStyle.titleColor(),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
fun PickAnswerChar(fields: Fields, getUIStyle: GetUIStyle) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
        ) {
            Row {
                Text(
                    text = stringResource(R.string.answer) +
                            ": ${fields.correct.value.uppercase()}",
                    modifier = Modifier.padding(2.dp)
                )
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Answer",
                    tint = getUIStyle.titleColor(),
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
        Box(
            Modifier.fillMaxWidth(.25f),
            contentAlignment = Alignment.BottomEnd
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        fields.correct.value = 'a'
                        expanded = false
                    },
                    text = { Text("A") },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                DropdownMenuItem(
                    onClick = {
                        fields.correct.value = 'b'
                        expanded = false
                    },
                    text = { Text("B") },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                if (fields.choices[2].value.isNotBlank()) {
                    DropdownMenuItem(
                        onClick = {
                            fields.correct.value = 'c'
                            expanded = false
                        },
                        text = { Text("C") },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )
                }
                if (fields.choices[3].value.isNotBlank()) {
                    DropdownMenuItem(
                        onClick = {
                            fields.correct.value = 'd'
                            expanded = false
                        },
                        text = { Text("D") },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun returnReviewError(): List<String> {
    return listOf(
        stringResource(R.string.review_amount_0).toString(),
        stringResource(R.string.review_amount_10).toString(),
        stringResource(R.string.review_amount_same).toString(),
        stringResource(R.string.failed_review).toString()
    )
}

@Composable
fun returnMultiplierError(): List<String> {
    return listOf(
        stringResource(R.string.good_multiplier_1).toString(),
        stringResource(R.string.bad_multiplier_1).toString(),
        stringResource(R.string.multipliers_same).toString(),
        stringResource(R.string.failed_multiplier).toString()
    )
}

@Composable
fun returnDeckError(): List<String> {
    return listOf(
        stringResource(R.string.empty_deck_name).toString(),
        stringResource(R.string.deck_name_exists).toString(),
        stringResource(R.string.deck_name_failed).toString()
    )
}

@Composable
fun returnCardAmountError(): List<String> {
    return listOf(
        stringResource(R.string.card_amount_under_5).toString(),
        stringResource(R.string.card_amount_over_1k).toString(),
        stringResource(R.string.card_amount_same).toString(),
        stringResource(R.string.failed_card_amount).toString()
    )
}

@Composable
fun getSavableFields(fields: Fields): Fields {
    return Fields(
        question = rememberSaveable { mutableStateOf("") },
        middleField = rememberSaveable { mutableStateOf("") },
        answer = rememberSaveable { mutableStateOf("") },
        choices = rememberSaveable { MutableList(4) { mutableStateOf("") } },
        correct = rememberSaveable { mutableStateOf('?') },
        scrollPosition = fields.scrollPosition,
        isQOrA = rememberSaveable { mutableStateOf(PartOfQorA.A) },
        inDeckClicked = fields.inDeckClicked,
        isEditing = fields.isEditing,
        mainClicked = fields.mainClicked,
        leftDueCardView = fields.leftDueCardView,
        newType = fields.newType,
    )
}