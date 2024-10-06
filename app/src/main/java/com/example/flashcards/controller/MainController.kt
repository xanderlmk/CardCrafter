package com.example.flashcards.controller

import com.example.flashcards.model.Deck
import com.example.flashcards.model.MainModel

class MainController {
    private var model : MainModel = MainModel()


    fun getDeckList(): MutableList<Deck>  {
        return model.deckList
    }
}