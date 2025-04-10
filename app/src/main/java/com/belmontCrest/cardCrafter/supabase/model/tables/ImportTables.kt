package com.belmontCrest.cardCrafter.supabase.model.tables

import com.belmontCrest.cardCrafter.localDatabase.tables.BasicCard
import com.belmontCrest.cardCrafter.localDatabase.tables.HintCard
import com.belmontCrest.cardCrafter.localDatabase.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.localDatabase.tables.NotationCard
import com.belmontCrest.cardCrafter.localDatabase.tables.ThreeFieldCard
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** The Card Columns that will be mapped to a
 * SealedCT (SealedCardType) with it's respective Card type */
@Serializable
sealed class SBCardColsWithCT {
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
data class SBCardColsBasic(
    override val id: Int = -1,
    @SerialName("deckUUID")
    override val deckUUID: String,
    override val type: String,
    @SerialName("cardIdentifier")
    override val cardIdentifier: String,
    val basicCard: BasicCard
) : SBCardColsWithCT()
@Serializable
data class SBCardColsHint(
    override val id: Int = -1,
    @SerialName("deckUUID")
    override val deckUUID: String,
    override val type: String,
    @SerialName("cardIdentifier")
    override val cardIdentifier: String,
    val hintCard: HintCard
) : SBCardColsWithCT()

@Serializable
data class SBCardColsThree(
    override val id: Int = -1,
    @SerialName("deckUUID")
    override val deckUUID: String,
    override val type: String,
    @SerialName("cardIdentifier")
    override val cardIdentifier: String,
    val threeCard: ThreeFieldCard
) : SBCardColsWithCT()
@Serializable
data class SBCardColsMulti(
    override val id: Int = -1,
    @SerialName("deckUUID")
    override val deckUUID: String,
    override val type: String,
    @SerialName("cardIdentifier")
    override val cardIdentifier: String,
    val multiCard: SBMultiCardDto
) : SBCardColsWithCT()

@Serializable
data class SBCardColsNotation(
    override val id: Int = -1,
    @SerialName("deckUUID")
    override val deckUUID: String,
    override val type: String,
    @SerialName("cardIdentifier")
    override val cardIdentifier: String,
    val notationCard: SBNotationCardDto
) : SBCardColsWithCT()

/** SealedCT to import to the local database, contains a Supabase Card
 *  which will be converted to the local Card
 *  once it's in the transaction phase.
 */
sealed class SealedCTToImport {
    data class Basic(
        val card: SBCardDto,
        val basicCard: BasicCard
    ) : SealedCTToImport()
    data class Three(
        val card: SBCardDto,
        val threeCard: ThreeFieldCard,
    ): SealedCTToImport()
    data class Hint(
        val card: SBCardDto,
        val hintCard: HintCard
    ): SealedCTToImport()
    data class Multi(
        val card: SBCardDto,
        val multiCard: MultiChoiceCard
    ): SealedCTToImport()
    data class Notation(
        val card: SBCardDto,
        val notationCard: NotationCard
    ): SealedCTToImport()
}