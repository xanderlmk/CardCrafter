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
import com.belmontCrest.cardCrafter.localDatabase.tables.HintCard
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.customCardParams.HintStringView

@Composable
fun HintFrontCard(
    hintCard: HintCard,
    getUIStyle: GetUIStyle
) {
    val focusManager = LocalFocusManager.current
    Box(contentAlignment = Alignment.TopCenter) {
        SelectionContainer(
            modifier = Modifier
                .clickable(
                    interactionSource = null, indication = null
                ) { focusManager.clearFocus() }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = hintCard.question,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getUIStyle.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                HintStringView(hintCard.hint, getUIStyle, focusManager)
            }
        }
    }
}

@Composable
fun HintBackCard(
    hintCard: HintCard,
    getUIStyle: GetUIStyle
) {
    val focusManager = LocalFocusManager.current // Get focus manager
    Box {
        SelectionContainer(
            modifier = Modifier
                .clickable(
                    interactionSource = null, indication = null
                ) { focusManager.clearFocus() }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = hintCard.question,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getUIStyle.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = hintCard.answer,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    color = getUIStyle.titleColor(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}