@file:Suppress("PropertyName")

package com.belmontCrest.cardCrafter.supabase.model

import com.belmontCrest.cardCrafter.model.tablesAndApplication.BasicCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Card
import com.belmontCrest.cardCrafter.model.tablesAndApplication.HintCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.MultiChoiceCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.NotationCard
import com.belmontCrest.cardCrafter.model.tablesAndApplication.ThreeFieldCard
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
    val type: String,
    @SerialName("cardIdentifier") val cardIdentifier : String
)


@Serializable
data class SBMultiCard(
    val cardId: Int = -1,
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
data class Owner(
    val user_id : String,
    val username : String,
    val f_name : String,
    val l_name : String
)

@Serializable
data class SBDeckToExport(
    val deck : SBDecks,
    val cts : List<SBCT>
)
@Serializable
sealed class SBCT {
    @Serializable
    data class Basic(
        val card: SBCards,
        val basicCard: BasicCard
    ) : SBCT()
    @Serializable
    data class Three(
        val card: SBCards,
        val threeCard: ThreeFieldCard,
    ): SBCT()
    @Serializable
    data class Hint(
        val card: SBCards,
        val hintCard: HintCard
    ): SBCT()
    @Serializable
    data class Multi(
        val card: SBCards,
        val multiCard: SBMultiCard
    ): SBCT()
    @Serializable
    data class Notation(
        val card: SBCards,
        val notationCard: SBNotationCard
    ): SBCT()
}

sealed class SealedCT {
    data class Basic(
        val card: SBCards,
        val basicCard: BasicCard
    ) : SealedCT()
    data class Three(
        val card: SBCards,
        val threeCard: ThreeFieldCard,
    ): SealedCT()
    data class Hint(
        val card: SBCards,
        val hintCard: HintCard
    ): SealedCT()
    data class Multi(
        val card: SBCards,
        val multiCard: MultiChoiceCard
    ): SealedCT()
    data class Notation(
        val card: SBCards,
        val notationCard: NotationCard
    ): SealedCT()
}