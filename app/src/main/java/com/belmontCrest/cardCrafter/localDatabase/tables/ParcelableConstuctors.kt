package com.belmontCrest.cardCrafter.localDatabase.tables

import android.os.Parcel
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