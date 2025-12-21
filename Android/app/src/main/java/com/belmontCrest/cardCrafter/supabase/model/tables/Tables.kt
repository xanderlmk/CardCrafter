
package com.belmontCrest.cardCrafter.supabase.model.tables

import com.belmontCrest.cardCrafter.local.db.tables.BasicCard
import com.belmontCrest.cardCrafter.local.db.tables.HintCard
import com.belmontCrest.cardCrafter.local.db.tables.ListStringConverter
import com.belmontCrest.cardCrafter.local.db.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.local.db.tables.NotationCard
import com.belmontCrest.cardCrafter.local.db.tables.ThreeFieldCard
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


private val StringToList = ListStringConverter()
private const val CARD_ID = "card_id"
private const val USER_ID = "user_id"

@Serializable
data class SBDeckDto(
    val deckUUID: String,
    @SerialName(USER_ID)
    val userId: String,
    val name: String,
    val description: String,
    @SerialName("updated_on")
    val updatedOn: String = ""
)

@Serializable
data class SBCardDto(
    val id: Int = -1,
    @SerialName("deckUUID") val deckUUID: String,
    val type: String,
    @SerialName("cardIdentifier") val cardIdentifier: String
)


@Serializable
data class SBMultiCardDto(
    @SerialName(CARD_ID)
    val cardId: Int = -1,
    val question: String,
    val choiceA: String,
    val choiceB: String,
    val choiceC: String?,
    val choiceD: String?,
    val correct: Char
)

fun SBMultiCardDto.toMultiChoiceCard(): MultiChoiceCard = MultiChoiceCard(
    cardId, question, choiceA, choiceB, choiceC ?: "", choiceD ?: "", correct
)

@Serializable
data class SBDeckListDto(
    val list: List<SBDeckDto> = emptyList()
)

fun SBDeckListDto.toUUIDs(): List<String> = this.list.map {
    it.deckUUID
}

@Serializable
data class SBNotationCardDto(
    @SerialName(CARD_ID)
    val cardId: Int = -1,
    val question: String,
    val steps: String,
    val answer: String
)

fun SBNotationCardDto.toNotationCard(): NotationCard = NotationCard(
    cardId, question, StringToList.fromString(steps), answer
)

@Serializable
data class SBDeckUUIDDto(val deckUUID: String)

@Serializable
data class SBDeckOwnerDto(@SerialName(USER_ID) val userId: String)

@Serializable
data class SBDeckUpdatedOnDto(
    @SerialName("updated_on")
    val updatedOn: String
)

@Serializable
data class OwnerDto(
    @SerialName(USER_ID)
    val userId: String,
    val username: String,
    @SerialName("f_name")
    val fName: String,
    @SerialName("l_name")
    val lName: String
)

@Serializable
data class UserProfile(
    val user: UserInfo,
    val owner: OwnerDto? = null
)

/** Supabase Deck and it's cards to Export. */
@Serializable
data class SBDeckToExportDto(
    val deck: SBDeckDto,
    val cts: List<SBCTToExport>,
    val cardsToDisplay: CardsToDisplay,
    val lastUpdatedOn: String,
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
    ) : SBCTToExport()

    @Serializable
    data class Hint(
        val card: SBCardDto,
        val hintCard: HintCard
    ) : SBCTToExport()

    @Serializable
    data class Multi(
        val card: SBCardDto,
        val multiCard: SBMultiCardDto
    ) : SBCTToExport()

    @Serializable
    data class Notation(
        val card: SBCardDto,
        val notationCard: SBNotationCardDto
    ) : SBCTToExport()
}

