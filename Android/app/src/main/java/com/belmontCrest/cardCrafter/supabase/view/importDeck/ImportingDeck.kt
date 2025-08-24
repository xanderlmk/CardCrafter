package com.belmontCrest.cardCrafter.supabase.view.importDeck

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle

@Composable
fun ImportingDeck(progress: Float, getUIStyle: GetUIStyle) {
    Dialog(onDismissRequest = {}) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .background(getUIStyle.importingDeckColor()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Importing Deck...\n" +
                        "Please do not turn off your device.",
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                color = Color.White,
                trackColor = Color.Black,
            )
            Spacer(Modifier.height(4.dp))
            Text("${(progress * 100).toInt()}%")

        }

    }
}