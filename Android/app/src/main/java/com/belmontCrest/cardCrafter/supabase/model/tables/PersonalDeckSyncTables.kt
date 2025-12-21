package com.belmontCrest.cardCrafter.supabase.model.tables

import com.belmontCrest.cardCrafter.BuildConfig
import com.belmontCrest.cardCrafter.local.db.tables.AllCardTypes
import com.belmontCrest.cardCrafter.local.db.tables.CT
import com.belmontCrest.cardCrafter.local.db.tables.Deck
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

private const val SB_PD_TN = BuildConfig.SB_PD_TN
@Serializable
@SerialName(SB_PD_TN)
data class PersonalDecks(
    val id: String = "",
    @SerialName("user_id")
    val userId: String,
    /** The whole db serialized into a json object */
    val data: JsonObject,
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("updated_on")
    val updatedOn: String = ""
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

@Serializable
data class PDUpdatedOn(
    @SerialName("updated_on")
    val updatedOn: String
)


