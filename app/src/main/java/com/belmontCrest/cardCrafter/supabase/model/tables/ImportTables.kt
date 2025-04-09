package com.belmontCrest.cardCrafter.supabase.model.tables

import com.belmontCrest.cardCrafter.localDatabase.tables.BasicCard
import com.belmontCrest.cardCrafter.localDatabase.tables.HintCard
import com.belmontCrest.cardCrafter.localDatabase.tables.ThreeFieldCard
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** The Card Columns that will be mapped to a
 * SealedCT (SealedCardType) with it's respective Card type */
@Serializable
sealed class SBCardWithCT {
    abstract val id: Int
    abstract val deckUUID: String
    abstract val type: String
    abstract val cardIdentifier: String

    /**
     * Extracts the integer value from the substring
     * after the last '-' in cardIdentifier.
     */
    fun sortKey(): Int {
        return cardIdentifier.substringAfterLast('-').toInt()
    }
}


@Serializable
data class SBCardBasic(
    override val id: Int = -1,
    @SerialName("deckUUID")
    override val deckUUID: String,
    override val type: String,
    @SerialName("cardIdentifier")
    override val cardIdentifier: String,
    val basicCard: BasicCard
) : SBCardWithCT()
@Serializable
data class SBCardHint(
    override val id: Int = -1,
    @SerialName("deckUUID")
    override val deckUUID: String,
    override val type: String,
    @SerialName("cardIdentifier")
    override val cardIdentifier: String,
    val hintCard: HintCard
) : SBCardWithCT()

@Serializable
data class SBCardThree(
    override val id: Int = -1,
    @SerialName("deckUUID")
    override val deckUUID: String,
    override val type: String,
    @SerialName("cardIdentifier")
    override val cardIdentifier: String,
    val threeCard: ThreeFieldCard
) : SBCardWithCT()
@Serializable
data class SBCardMulti(
    override val id: Int = -1,
    @SerialName("deckUUID")
    override val deckUUID: String,
    override val type: String,
    @SerialName("cardIdentifier")
    override val cardIdentifier: String,
    val multiCard: SBMultiCardDto
) : SBCardWithCT()

@Serializable
data class SBCardNotation(
    override val id: Int = -1,
    @SerialName("deckUUID")
    override val deckUUID: String,
    override val type: String,
    @SerialName("cardIdentifier")
    override val cardIdentifier: String,
    val notationCard: SBNotationCardDto
) : SBCardWithCT()