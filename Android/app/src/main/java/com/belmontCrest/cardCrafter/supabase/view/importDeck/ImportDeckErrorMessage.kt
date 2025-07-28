package com.belmontCrest.cardCrafter.supabase.view.importDeck

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.belmontCrest.cardCrafter.supabase.model.ReturnValues

@Composable
fun ImportDeckErrorMessage(result: Int, errorMessage: String) {
    if (result == ReturnValues.UUID_CONFLICT) {
        Text(
            text = """
                        |Deck signature already exists,
                        |You must replace it.
                    """.trimMargin(),
            modifier = Modifier.Companion.fillMaxWidth(),
            fontSize = 11.sp,
            textAlign = TextAlign.Companion.Center,
            color = Color.Companion.Red
        )
    } else if (result == ReturnValues.DECK_EXISTS) {
        Text(
            text = """
                        |Name already exists!
                        |Try another name.
                    """.trimMargin(),
            modifier = Modifier.Companion.fillMaxWidth(),
            fontSize = 11.sp,
            textAlign = TextAlign.Companion.Center,
            color = Color.Companion.Red
        )
    } else if (result == ReturnValues.EMPTY_STRING) {
        Text(
            text = """
                        |Name can't be empty!
                    """.trimMargin(),
            modifier = Modifier.Companion.fillMaxWidth(),
            fontSize = 11.sp,
            textAlign = TextAlign.Companion.Center,
            color = Color.Companion.Red
        )
    } else if (errorMessage.isNotBlank()) {
        Text(
            text = errorMessage,
            modifier = Modifier.Companion.fillMaxWidth(),
            fontSize = 11.sp,
            textAlign = TextAlign.Companion.Center,
            color = Color.Companion.Red
        )
    }
}