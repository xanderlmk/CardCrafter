package com.example.flashcards.views.miscFunctions

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.flashcards.controller.viewModels.deckViewsModels.EditDeckViewModel
import com.example.flashcards.model.tablesAndApplication.Deck

/**
 * Setting EditDeckView Params
 * Receiving DeckDetails if the view model saveStateHandle is active
 * Creating DeckDetails else wise
 */
fun setDeckFields(
    vm: EditDeckViewModel,
    deck: Deck, currentName: String
) {
    vm.updateNameField(currentName)
    vm.updateRAField(deck.reviewAmount.toString())
    vm.updateBMField(deck.badMultiplier)
    vm.updateGMField(deck.goodMultiplier)
    vm.updateCAField(deck.cardAmount.toString())
    vm.updateActivity()
}

fun createDeckDetails(deck: Deck) : DeckDetails{
    val dD = mutableStateOf(DeckDetails())
    dD.value.name.value = deck.name
    dD.value.gm.value = deck.goodMultiplier.toString()
    dD.value.bm.value = deck.badMultiplier.toString()
    dD.value.ra.value = deck.reviewAmount.toString()
    dD.value.ca.value = deck.cardAmount.toString()
    return dD.value
}
fun retrieveDeckDetails(vm : EditDeckViewModel) : DeckDetails{
    val dD = mutableStateOf(DeckDetails())
    dD.value.name.value = vm.deckName
    dD.value.gm.value = vm.deckGM.toString()
    dD.value.bm.value = vm.deckBM.toString()
    dD.value.ra.value = vm.deckRA
    dD.value.ca.value = vm.deckCA
    return dD.value
}

data class DeckDetails(
    val name : MutableState<String> = mutableStateOf(""),
    val gm : MutableState<String> = mutableStateOf(""),
    val bm : MutableState<String> = mutableStateOf(""),
    val ra : MutableState<String> = mutableStateOf(""),
    val ca : MutableState<String> = mutableStateOf("")
)