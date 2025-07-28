package com.belmontCrest.cardCrafter.supabase.view.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle

@Composable
fun ProfileText(title: String, text: String, getUIStyle: GetUIStyle) {
    Column {
        Text(
            text = title, textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            fontSize = 22.sp, color = getUIStyle.titleColor(),
            fontWeight = FontWeight.Bold
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(.725f)
                .align(
                    Alignment.Start
                ), thickness = 2.5.dp
        )
        Text(
            text = text,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp, vertical = 4.dp),
            fontSize = 20.sp, color = getUIStyle.titleColor()
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(.725f)
                .align(
                    Alignment.End
                ), thickness = 2.5.dp
        )
    }
}