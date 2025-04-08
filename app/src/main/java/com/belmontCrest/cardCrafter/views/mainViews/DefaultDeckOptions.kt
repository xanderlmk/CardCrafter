package com.belmontCrest.cardCrafter.views.mainViews

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.generalSettingsOptionsModifier
import com.belmontCrest.cardCrafter.uiFunctions.EditIntField

@Composable
fun DefaultDeckOptions(
    changeReviewAmount: () -> Unit,
    changeCardAmount: () -> Unit,
    reviewAmount: MutableState<String>,
    cardAmount: MutableState<String>,
    reviewSuccess: Boolean,
    cardSuccess: Boolean,
    clicked: Boolean,
    getUIStyle: GetUIStyle
) {
    var expanded by remember { mutableStateOf(false) }
    if (clicked) {
        expanded = false
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .border(
                width = 2.dp,
                shape = RoundedCornerShape(12.dp),
                color = if (getUIStyle.getIsDarkTheme() == true) {
                    Color.Gray
                } else {
                    Color.Black
                }
            )
    ) {
        Text(
            text = "Default Deck Options",
            fontSize = 20.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Bold,
            color = getUIStyle.titleColor(),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth()
                .clickable {
                    if (!clicked) {
                        expanded = !expanded
                    }
                }
        )
        if (expanded) {
            Column(
                modifier = Modifier
                    .generalSettingsOptionsModifier(getUIStyle.getColorScheme()),
            ) {
                Text(
                    text = stringResource(R.string.review_amount),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    fontStyle = FontStyle.Italic,
                    color = getUIStyle.titleColor(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    EditIntField(
                        value = reviewAmount.value,
                        onValueChanged = {
                            reviewAmount.value = it
                        },
                        labelStr = stringResource(R.string.review_amount),
                        modifier = Modifier
                            .fillMaxWidth(0.30f)
                            .padding(vertical = 1.dp, horizontal = 2.dp)
                    )
                    Button(
                        onClick = { changeReviewAmount() },
                        modifier = Modifier
                            .fillMaxWidth(0.44f)
                            .padding(vertical = 1.dp, horizontal = 2.dp)
                            .align(Alignment.CenterVertically),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getUIStyle.secondaryButtonColor(),
                            contentColor = getUIStyle.buttonTextColor()
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.save),
                            color = if (!reviewSuccess) {
                                getUIStyle.titleColor()
                            } else {
                                if (getUIStyle.getIsDarkTheme()) {
                                    Color.Green
                                } else {
                                    Color(32, 133, 36, 255)
                                }
                            }
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .generalSettingsOptionsModifier(getUIStyle.getColorScheme()),
            ) {
                Text(
                    text = stringResource(R.string.card_amount),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    fontStyle = FontStyle.Italic,
                    color = getUIStyle.titleColor(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    EditIntField(
                        value = cardAmount.value,
                        onValueChanged = {
                            cardAmount.value = it
                        },
                        labelStr = stringResource(R.string.card_amount),
                        modifier = Modifier
                            .fillMaxWidth(0.30f)
                            .padding(vertical = 1.dp, horizontal = 2.dp)
                    )
                    Button(
                        onClick = { changeCardAmount() },
                        modifier = Modifier
                            .fillMaxWidth(0.44f)
                            .padding(vertical = 1.dp, horizontal = 2.dp)
                            .align(Alignment.CenterVertically),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getUIStyle.secondaryButtonColor(),
                            contentColor = getUIStyle.buttonTextColor()
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.save),
                            color = if (!cardSuccess) {
                                getUIStyle.titleColor()
                            } else {
                                Color.Green
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InvalidXXAmount(
    pressed: MutableState<Boolean>,
    getUIStyle: GetUIStyle,
    error: String
) {
    if (pressed.value) {
        AlertDialog(
            onDismissRequest = { pressed.value = false },
            title = { Text("Invalid") },
            text = {
                Text(
                    text = error,
                    color = getUIStyle.titleColor()
                )
            },
            confirmButton = {},
            dismissButton = {
                Button(
                    onClick = { pressed.value = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getUIStyle.secondaryButtonColor(),
                        contentColor = getUIStyle.buttonTextColor()
                    )
                ) {
                    Text("Ok")
                }
            }
        )
    }
}