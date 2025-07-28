package com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.belmontCrest.cardCrafter.localDatabase.tables.PartOfQorA
import com.belmontCrest.cardCrafter.localDatabase.tables.ThreeFieldCard
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle

@Composable
fun ThreeFrontCard(
    threeCard: ThreeFieldCard,
    getUIStyle: GetUIStyle
) {
    val focusManager = LocalFocusManager.current // Get focus manager
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        SelectionContainer(
            modifier = Modifier.clickable(
                interactionSource = null,
                indication = null
            ) { focusManager.clearFocus() }
        ) {
            Column {
                Text(
                    text = threeCard.question,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getUIStyle.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)

                )
                if (threeCard.field is PartOfQorA.Q) {
                    Text(
                        text = threeCard.middle,
                        fontSize = 20.sp,
                        lineHeight = 22.sp,
                        color = getUIStyle.titleColor(),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun ThreeBackCard(
    threeCard: ThreeFieldCard,
    getUIStyle: GetUIStyle
) {
    val focusManager = LocalFocusManager.current // Get focus manager
    Box(Modifier.fillMaxWidth()) {
        SelectionContainer(
            modifier = Modifier
                .clickable(
                    interactionSource = null,
                    indication = null
                ) { focusManager.clearFocus() }
        ) {
            Column {
                Text(
                    text = threeCard.question,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getUIStyle.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = threeCard.middle,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getUIStyle.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = threeCard.answer,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getUIStyle.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}