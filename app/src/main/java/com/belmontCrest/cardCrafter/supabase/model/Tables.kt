@file:Suppress("PropertyName")

package com.belmontCrest.cardCrafter.supabase.model

import com.belmontCrest.cardCrafter.model.tablesAndApplication.BasicCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.HintCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.MultiChoiceCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.NotationCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.ThreeFieldCard
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SBDeckDto(
    val deckUUID: String,
    val user_id: String,
    val name: String,
    val description: String
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

@Serializable
data class SBDeckToExportDto(
    val deck : SBDeckDto,
    val cts : List<SBCT>
)
@Serializable
sealed class SBCT {
    @Serializable
    data class Basic(
        val card: SBCardDto,
        val basicCard: BasicCard
    ) : SBCT()
    @Serializable
    data class Three(
        val card: SBCardDto,
        val threeCard: ThreeFieldCard,
    ): SBCT()
    @Serializable
    data class Hint(
        val card: SBCardDto,
        val hintCard: HintCard
    ): SBCT()
    @Serializable
    data class Multi(
        val card: SBCardDto,
        val multiCard: SBMultiCardDto
    ): SBCT()
    @Serializable
    data class Notation(
        val card: SBCardDto,
        val notationCard: SBNotationCardDto
    ): SBCT()
}

sealed class SealedCT {
    data class Basic(
        val card: SBCardDto,
        val basicCard: BasicCard
    ) : SealedCT()
    data class Three(
        val card: SBCardDto,
        val threeCard: ThreeFieldCard,
    ): SealedCT()
    data class Hint(
        val card: SBCardDto,
        val hintCard: HintCard
    ): SealedCT()
    data class Multi(
        val card: SBCardDto,
        val multiCard: MultiChoiceCard
    ): SealedCT()
    data class Notation(
        val card: SBCardDto,
        val notationCard: NotationCard
    ): SealedCT()
}