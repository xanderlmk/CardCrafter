package com.belmontCrest.cardCrafter.model.ui.states

import android.os.Parcelable
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.AnswerType
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.MiddleType
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.ParamType
import com.belmontCrest.cardCrafter.localDatabase.tables.SavedCard
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

/** MainViewModel States */
@Parcelize
data class DeckUiState(
    val deckList: List<Deck> = emptyList(),
    val cardAmount: List<Int> = emptyList()
) : Parcelable

@Serializable
@Parcelize
data class CardListUiCount(val cardListCount: List<Int> = emptyList()) : Parcelable

@Parcelize
data class SealedAllCTs(var allCTs: MutableList<CT> = mutableListOf()) : Parcelable


/** NavViewModel States */
@Serializable
@Parcelize
data class StringVar(
    val name: String = ""
) : Parcelable

@Parcelize
data class WhichDeck(
    val deck: Deck? = null
) : Parcelable

@Parcelize
data class SelectedCard(
    val ct: CT?
) : Parcelable

@Parcelize
@Serializable
@SerialName("TypesForCards")
data class Types(
    val ts: List<TypeInfo> = emptyList()
) : Parcelable

@Serializable
@SerialName("TypeInfo")
@Parcelize
data class TypeInfo(
    val t: String, val q: ParamType,
    val m: MiddleType, val a: AnswerType
) : Parcelable

fun TypeInfo.hasNotations(): Boolean =
    this.q.hasNotations() ||
            (this.m is MiddleType.WithParam && this.m.param.hasNotations()) ||
            this.a is AnswerType.NotationList ||
            (this.a is AnswerType.WithParam && this.a.param.hasNotations())

private fun ParamType.hasNotations(): Boolean =
    this is ParamType.LT.NOTATION ||
            (this is ParamType.Pair &&
                    (this.first is ParamType.LT.NOTATION ||
                            this.second is ParamType.LT.NOTATION))

/** DueCards States */
data class DueDeckDetails(
    val id: Int = 0,
    var cardsLeft: Int = 0,
    val cardAmount: Int = 0,
    val reviewAmount: Int = 0,
    val nextReview: Date = Date()
)

/** For CardDeckVM */
@Parcelize
data class SealedDueCTs(
    var allCTs: MutableList<CT> = mutableListOf(),
    var savedCTs: MutableList<CT> = mutableListOf()
) : Parcelable

/** Also for our OnCreate MA */
data class SavedCardUiState(
    var savedCards: List<SavedCard> = emptyList()
)