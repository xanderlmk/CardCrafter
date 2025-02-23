@file:Suppress("PropertyName")

package com.example.flashcards.supabase.model

import kotlinx.serialization.Serializable

@Serializable
data class SBDecks(
    val deckUUID: String,
    val user_id : String,
    val name : String,
    val description: String
)
@Serializable
data class SBCards(
    val id: Int = -1,
    val deckUUID: String,
    val type: String
)

@Serializable
data class SBDeckList(
    val list: List<SBDecks> = emptyList()
)

@Serializable
data class SBMathCard(
    val cardId : Int,
    val question : String,
    val steps : String,
    val answer : String
)


