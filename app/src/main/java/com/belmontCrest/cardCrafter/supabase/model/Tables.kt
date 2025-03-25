@file:Suppress("PropertyName")

package com.belmontCrest.cardCrafter.supabase.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SBDecks(
    val deckUUID: String,
    val user_id: String,
    val name: String,
    val description: String
)

@Serializable
data class SBCards(
    val id: Int = -1,
    @SerialName("deckUUID") val deckUUID: String,
    val type: String
)


@Serializable
data class SBMultiCard(
    val cardId: Int,
    val question: String,
    val choiceA: String,
    val choiceB: String,
    val choiceC: String?,
    val choiceD: String?,
    val correct: Char
)

@Serializable
data class SBDeckList(
    val list: List<SBDecks> = emptyList()
)

@Serializable
data class SBNotationCard(
    val cardId: Int = -1,
    val question: String,
    val steps: String,
    val answer: String
)

@Serializable
data class SBDeckUUID(val deckUUID: String)
@Serializable
data class SBDeckOwner(val user_id: String)

@Serializable
data class CoOwner(
    val user_Id : String,
    val username : String
)