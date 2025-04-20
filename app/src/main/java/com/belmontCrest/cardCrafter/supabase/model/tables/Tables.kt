@file:Suppress("PropertyName")

package com.belmontCrest.cardCrafter.supabase.model.tables

import com.belmontCrest.cardCrafter.localDatabase.tables.BasicCard
import com.belmontCrest.cardCrafter.localDatabase.tables.HintCard
import com.belmontCrest.cardCrafter.localDatabase.tables.ThreeFieldCard
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SBDeckDto(
    val deckUUID: String,
    val user_id: String,
    val name: String,
    val description: String,
    val updated_on: String = ""
)

@Serializable
data class SBCardDto(
    val id: Int = -1,
    @SerialName("deckUUID") val deckUUID: String,
    val type: String,
    @SerialName("cardIdentifier") val cardIdentifier : String
)


@Serializable
data class SBMultiCardDto(
    val cardId: Int = -1,
    val question: String,
    val choiceA: String,
    val choiceB: String,
    val choiceC: String?,
    val choiceD: String?,
    val correct: Char
)

@Serializable
data class SBDeckListDto(
    val list: List<SBDeckDto> = emptyList()
)

@Serializable
data class SBNotationCardDto(
    val cardId: Int = -1,
    val question: String,
    val steps: String,
    val answer: String
)

@Serializable
data class SBDeckUUIDDto(val deckUUID: String)
@Serializable
data class SBDeckOwnerDto(val user_id: String)

@Serializable
data class OwnerDto(
    val user_id : String,
    val username : String,
    val f_name : String,
    val l_name : String
)

@Serializable
data class UserProfile(
    val user : UserInfo,
    val owner : OwnerDto? = null
)
/** Supabase Deck and it's cards to Export*/
@Serializable
data class SBDeckToExportDto(
    val deck : SBDeckDto,
    val cts : List<SBCTToExport>,
    val cardsToDisplay : CardsToDisplay
)

/** Supabase Card with its respective Card Type to export to supabase. */
@Serializable
sealed class SBCTToExport {
    @Serializable
    data class Basic(
        val card: SBCardDto,
        val basicCard: BasicCard
    ) : SBCTToExport()
    @Serializable
    data class Three(
        val card: SBCardDto,
        val threeCard: ThreeFieldCard,
    ): SBCTToExport()
    @Serializable
    data class Hint(
        val card: SBCardDto,
        val hintCard: HintCard
    ): SBCTToExport()
    @Serializable
    data class Multi(
        val card: SBCardDto,
        val multiCard: SBMultiCardDto
    ): SBCTToExport()
    @Serializable
    data class Notation(
        val card: SBCardDto,
        val notationCard: SBNotationCardDto
    ): SBCTToExport()
}

