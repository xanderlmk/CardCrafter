package com.example.flashcards.model
import java.time.LocalDateTime
class Deck {
    var id : Int = -1
    var name : String = ""
    var cards : MutableList<FlashCard> = mutableListOf(FlashCard())
}

class FlashCard {
    val id: Int = -1
    val question: String = ""
    val answer: String = ""
    val deckId: Int = -1
    var nextReview: LocalDateTime = LocalDateTime.now()
    var passes: Int = 0
}