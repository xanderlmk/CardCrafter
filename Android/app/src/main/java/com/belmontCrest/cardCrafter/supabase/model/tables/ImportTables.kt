package com.belmontCrest.cardCrafter.supabase.model.tables

import com.belmontCrest.cardCrafter.BuildConfig
import com.belmontCrest.cardCrafter.local.db.tables.BasicCard
import com.belmontCrest.cardCrafter.local.db.tables.HintCard
import com.belmontCrest.cardCrafter.local.db.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.local.db.tables.NotationCard
import com.belmontCrest.cardCrafter.local.db.tables.ThreeFieldCard
import com.belmontCrest.cardCrafter.model.Type
import com.belmontCrest.cardCrafter.supabase.controller.converters.RawColumns
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
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

data class SBCardList(
    val cts: List<SBCardColsWithCT> = listOf()
)

private const val SB_CARD_TN = BuildConfig.SB_CARD_TN


fun List<SBCardColsWithCT>.toListOfSealedCTToImport(): List<SealedCTToImport> =
    this.map { cardColsWithCT ->
        when (val ct = cardColsWithCT) {
            is SBCardColsBasic -> {
                SealedCTToImport.Basic(
                    SBCardDto(ct.id, ct.deckUUID, ct.type, ct.cardIdentifier),
                    ct.basicCard
                )
            }

            is SBCardColsHint -> {
                SealedCTToImport.Hint(
                    SBCardDto(ct.id, ct.deckUUID, ct.type, ct.cardIdentifier),
                    ct.hintCard
                )
            }

            is SBCardColsMulti -> {
                SealedCTToImport.Multi(
                    SBCardDto(ct.id, ct.deckUUID, ct.type, ct.cardIdentifier),
                    ct.multiCard.toMultiChoiceCard()
                )
            }

            is SBCardColsNotation -> {
                SealedCTToImport.Notation(
                    SBCardDto(ct.id, ct.deckUUID, ct.type, ct.cardIdentifier),
                    ct.notationCard.toNotationCard()
                )
            }

            is SBCardColsThree -> {
                SealedCTToImport.Three(
                    SBCardDto(ct.id, ct.deckUUID, ct.type, ct.cardIdentifier),
                    ct.threeCard
                )
            }
        }
    }

suspend fun SBCardDto.toSBCardColsWithCT(
    supabase: SupabaseClient
): SBCardColsWithCT = when (this.type) {
    Type.BASIC -> {
        supabase.from(SB_CARD_TN).select(RawColumns.Basic) {
            filter { eq("id", id) }
        }.decodeSingle<SBCardColsBasic>()
    }

    Type.HINT -> {
        supabase.from(SB_CARD_TN).select(RawColumns.Hint) {
            filter { eq("id", id) }
        }.decodeSingle<SBCardColsHint>()
    }

    Type.THREE -> {
        supabase.from(SB_CARD_TN).select(RawColumns.Three) {
            filter { eq("id", id) }
        }.decodeSingle<SBCardColsThree>()
    }

    Type.MULTI -> {
        supabase.from(SB_CARD_TN).select(RawColumns.Multi) {
            filter {
                eq("id", id)
            }
        }.decodeSingle<SBCardColsMulti>()
    }

    Type.NOTATION -> {
        supabase.from(SB_CARD_TN).select(RawColumns.Notation) {
            filter {
                eq("id", id)
            }
        }.decodeSingle<SBCardColsNotation>()
    }

    else -> {
        throw IllegalStateException("Not a valid card type!")
    }
}

private const val BASIC = BuildConfig.SB_BASIC_TN
private const val THREE = BuildConfig.SB_THREE_TN
private const val HINT = BuildConfig.SB_HINT_TN
private const val MULTI = BuildConfig.SB_MULTI_TN
private const val NOTATION = BuildConfig.SB_NOTATION_TN

@Serializable
data class SBCardColsBasic(
    override val id: Int = -1,
    @SerialName("deckUUID")
    override val deckUUID: String,
    override val type: String,
    @SerialName("cardIdentifier")
    override val cardIdentifier: String,
    @SerialName(BASIC)
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
    @SerialName(HINT)
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
    @SerialName(THREE)
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
    @SerialName(MULTI)
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
    @SerialName(NOTATION)
    val notationCard: SBNotationCardDto
) : SBCardColsWithCT()

/** SealedCT to import to the local database, contains a Supabase Card
 *  which will be converted to the local Card
 *  once it's in the transaction phase.
 */
@Serializable
sealed class SealedCTToImport {
    @Serializable
    data class Basic(
        val card: SBCardDto,
        @SerialName(BASIC)
        val basicCard: BasicCard
    ) : SealedCTToImport()

    @Serializable
    data class Three(
        val card: SBCardDto,
        @SerialName(THREE)
        val threeCard: ThreeFieldCard,
    ) : SealedCTToImport()

    @Serializable
    data class Hint(
        val card: SBCardDto,
        @SerialName(HINT)

        val hintCard: HintCard
    ) : SealedCTToImport()

    @Serializable
    data class Multi(
        val card: SBCardDto,
        @SerialName(MULTI)
        val multiCard: MultiChoiceCard
    ) : SealedCTToImport()

    @Serializable
    data class Notation(
        val card: SBCardDto,
        @SerialName(NOTATION)
        val notationCard: NotationCard
    ) : SealedCTToImport()
}

fun SealedCTToImport.toCard(): SBCardDto = when (this) {
    is SealedCTToImport.Basic -> this.card

    is SealedCTToImport.Three -> this.card

    is SealedCTToImport.Hint -> this.card

    is SealedCTToImport.Multi -> this.card

    is SealedCTToImport.Notation -> this.card
}