package com.example.flashcards.controller

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.example.flashcards.model.Deck
import com.example.flashcards.model.FlashCard
import com.example.flashcards.model.MainModel

class MainController {
    private var model : MainModel = MainModel()
    private var deck = Deck()


    fun getDeckList(): MutableList<Deck>  {
        return model.deckList
    }
    fun getModelDeckName() : String {
        return model.deck.name
    }

    fun addDeck(deckName: String) : Boolean {
        if (deckName.isNotEmpty()) {
            deck.name = deckName
            deck.id = model.deckList.size + 1
            model.deckList.add(deck)
        }

        return true
    }

    fun emptyDecision() {
        model.deck.name = ""
    }


    @Composable
    fun EditTextField(
        value: String,
        onValueChanged: (String) -> Unit,
        modifier: Modifier
    ) {
        TextField(
            value = value,
            singleLine = true,
            modifier = modifier,
            onValueChange = onValueChanged,
            label = { Text("Deck Name") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
    }
}