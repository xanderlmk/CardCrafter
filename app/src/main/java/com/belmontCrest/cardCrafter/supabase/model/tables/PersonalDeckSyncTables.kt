package com.belmontCrest.cardCrafter.supabase.model.tables

import com.belmontCrest.cardCrafter.localDatabase.tables.AllCardTypes
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class PersonalDecks(
    val id: String = "",
    val user_id: String,
    val data: JsonObject,
    val created_at: String,
    val updated_on: String
)

@Serializable
data class DeckWithLotsCards(
    val deck: Deck,
    val cts: List<CT>
)

data class DeckWithCardTypes(
    val deck: Deck,
    val cardTypes: List<AllCardTypes>
)

@Serializable
data class ListOfDecks(
    val decks: List<DeckWithLotsCards>
)

