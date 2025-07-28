package com.belmontCrest.cardCrafter.localDatabase.tables

import android.os.Parcel
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.AnswerParam
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.MiddleParam
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.Param
import kotlinx.serialization.json.Json
import java.util.Date

fun toParcelableCard(parcel: Parcel): Card? =
    Card(
        id = parcel.readInt(),
        deckId = parcel.readInt(),
        deckUUID = parcel.readString()!!,
        reviewsLeft = parcel.readInt(),
        nextReview = Date(parcel.readLong()),
        passes = parcel.readInt(),
        prevSuccess = parcel.readByte() != 0.toByte(),
        totalPasses = parcel.readInt(),
        type = parcel.readString()!!,
        createdOn = parcel.readLong(),
        partOfList = parcel.readByte() != 0.toByte(),
        deckCardNumber = parcel.readInt(),
        cardIdentifier = parcel.readString()!!
    )

fun toParcelableBasicCard(parcel: Parcel): BasicCard? =
    BasicCard(
        cardId = parcel.readInt(),
        question = parcel.readString()!!,
        answer = parcel.readString()!!
    )

fun toParcelableThreeCard(parcel: Parcel): ThreeFieldCard? =
    ThreeFieldCard(
        cardId = parcel.readInt(),
        question = parcel.readString()!!,
        middle = parcel.readString()!!,
        answer = parcel.readString()!!,
        field = if (parcel.readByte() != 0.toByte()) PartOfQorA.Q else PartOfQorA.A
    )

fun toParcelableHintCard(parcel: Parcel): HintCard? =
    HintCard(
        cardId = parcel.readInt(),
        question = parcel.readString()!!,
        hint = parcel.readString()!!,
        answer = parcel.readString()!!
    )

fun toParcelableMultiCard(parcel: Parcel): MultiChoiceCard? =
    MultiChoiceCard(
        cardId = parcel.readInt(),
        question = parcel.readString()!!,
        choiceA = parcel.readString()!!,
        choiceB = parcel.readString()!!,
        choiceC = parcel.readString()!!,
        choiceD = parcel.readString()!!,
        correct = parcel.readString()!![0]
    )

fun toParcelableNotationCard(parcel: Parcel): NotationCard? =
    NotationCard(
        cardId = parcel.readInt(),
        question = parcel.readString()!!,
        steps = listOf(parcel.readString()!!),
        answer = parcel.readString()!!
    )

fun toParcelableNullableCustomCard(parcel: Parcel): NullableCustomCard? =
    NullableCustomCard(
        cardId = parcel.readInt(),
        Json.decodeFromString<Param>(parcel.readString()!!),
        parcel.readString()?.let { Json.decodeFromString<MiddleParam?>(it) },
        Json.decodeFromString<AnswerParam>(parcel.readString()!!)
    )

fun toParcelableCustomCard(parcel: Parcel): CustomCard? =
    CustomCard(
        cardId = parcel.readInt(),
        Json.decodeFromString<Param>(parcel.readString()!!),
        Json.decodeFromString<MiddleParam>(parcel.readString()!!),
        Json.decodeFromString<AnswerParam>(parcel.readString()!!)
    )
