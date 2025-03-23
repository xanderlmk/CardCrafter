package com.belmontCrest.cardCrafter.views.miscFunctions.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck

/**
 * Setting EditDeckView Params
 * creating deck details that will be saved.
 */
@Composable
fun createDeckDetails(deck: Deck): DeckDetails {
    return DeckDetails(
        name = rememberSaveable { mutableStateOf(deck.name) },
        gm = rememberSaveable { mutableStateOf(deck.goodMultiplier.toString()) },
        bm = rememberSaveable { mutableStateOf(deck.badMultiplier.toString()) },
        ra = rememberSaveable { mutableStateOf(deck.reviewAmount.toString()) },
        ca = rememberSaveable { mutableStateOf(deck.cardAmount.toString()) }
    )
}

data class DeckDetails(
    val name: MutableState<String> = mutableStateOf(""),
    val gm: MutableState<String> = mutableStateOf(""),
    val bm: MutableState<String> = mutableStateOf(""),
    val ra: MutableState<String> = mutableStateOf(""),
    val ca: MutableState<String> = mutableStateOf("")
)