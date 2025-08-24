package com.belmontCrest.cardCrafter.views.mainViews

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle

@Composable
fun KatexMenuOptions(
    getUIStyle: GetUIStyle, changeMenuHeight: () -> Unit, changeMenuWidth: () -> Unit,
    height: MutableState<String>, width: MutableState<String>,
    heightSuccess: Boolean, widthSuccess: Boolean
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .border(
                width = 2.dp,
                shape = RoundedCornerShape(12.dp),
                color = if (getUIStyle.getIsDarkTheme()) Color.Gray else Color.Black
            )
    ) {
        Text(
            text = "Katex Menu Options",
            fontSize = 20.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Bold,
            color = getUIStyle.titleColor(),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        )
        if (expanded) {
            ExpandedIntBoxedOptions(
                onClick = changeMenuHeight, getUIStyle = getUIStyle,
                string = height, success = heightSuccess,
                text = "Height"
            )
            ExpandedIntBoxedOptions(
                onClick = changeMenuWidth, getUIStyle = getUIStyle,
                string = width, success = widthSuccess,
                text = "Width"
            )
        }
    }
}