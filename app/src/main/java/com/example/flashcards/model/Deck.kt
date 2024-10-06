package com.example.flashcards.model

class Deck {
    val id : Int = -1
    var name : String = ""
    var cards : MutableList<FlashCard> = mutableListOf(FlashCard())

}