package com.belmontCrest.cardCrafter.ui.functions

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.belmontCrest.cardCrafter.ui.GetUIStyle

/**
 * A Boxed Content where on click will show the content
 * @param getUIStyle The UI Style
 * @param text Title text
 * @param modifier Modifier
 * @param content Content to display on expanded.
 */
@Composable
fun ColumnContentWithBorder(
    getUIStyle: GetUIStyle, text: String, modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .border(
                width = 2.dp, shape = RoundedCornerShape(12.dp),
                color = if (getUIStyle.getIsDarkTheme()) Color.Gray else Color.Black
            )
    ) {
        Text(
            text = text, fontSize = 20.sp, lineHeight = 22.sp,
            fontWeight = FontWeight.Bold, color = getUIStyle.titleColor(),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        )
        if (expanded) {
            HorizontalDivider(modifier = Modifier.fillMaxWidth(0.95f), thickness = 2.dp)
            content()
        }
    }
}