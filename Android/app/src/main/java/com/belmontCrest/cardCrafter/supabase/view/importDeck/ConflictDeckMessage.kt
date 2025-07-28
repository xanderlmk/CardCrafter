package com.belmontCrest.cardCrafter.supabase.view.importDeck

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun ConflictDeckMessage() {
    Text(
        text = """
        |A deck with this name or signature already exists!
        |Would you like to replace it or create a new deck?
        """.trimMargin(),
        modifier = Modifier.Companion.fillMaxWidth()
    )
    Text(
        text = "(NOTE: If the deck's signature already exist " +
                "you must replace the deck. " +
                "You cannot create a new deck.",
        modifier = Modifier.Companion.fillMaxWidth(),
        fontSize = 11.sp
    )
}