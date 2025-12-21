package com.belmontCrest.cardCrafter.local.db.tables

import android.os.Build
import android.os.Parcel
import com.belmontCrest.cardCrafter.local.db.tables.customCardInit.AnswerParam
import com.belmontCrest.cardCrafter.local.db.tables.customCardInit.MiddleParam
import com.belmontCrest.cardCrafter.local.db.tables.customCardInit.Param
import kotlinx.serialization.json.Json

fun Parcel.toParcelableBasicCard(): BasicCard? =
    BasicCard(
        cardId = this.readInt(),
        question = this.readString()!!,
        answer = this.readString()!!
    )

fun Parcel.toParcelableThreeCard(): ThreeFieldCard? =
    ThreeFieldCard(
        cardId = this.readInt(),
        question = this.readString()!!,
        middle = this.readString()!!,
        answer = this.readString()!!,
        field = if (this.readByte() != 0.toByte()) PartOfQorA.Q else PartOfQorA.A
    )

fun Parcel.toParcelableHintCard(): HintCard? =
    HintCard(
        cardId = this.readInt(),
        question = this.readString()!!,
        hint = this.readString()!!,
        answer = this.readString()!!
    )

fun Parcel.toParcelableMultiCard(): MultiChoiceCard? =
    MultiChoiceCard(
        cardId = this.readInt(),
        question = this.readString()!!,
        choiceA = this.readString()!!,
        choiceB = this.readString()!!,
        choiceC = this.readString()!!,
        choiceD = this.readString()!!,
        correct = this.readString()!![0]
    )

fun Parcel.toParcelableNotationCard(): NotationCard? =
    NotationCard(
        cardId = this.readInt(),
        question = this.readString()!!,
        steps = listOf(this.readString()!!),
        answer = this.readString()!!
    )

fun Parcel.toParcelableNullableCustomCard(): NullableCustomCard? =
    NullableCustomCard(
        cardId = this.readInt(),
        Param.create(this),
        this.readString()?.let { Json.decodeFromString<MiddleParam?>(it) },
        Json.decodeFromString<AnswerParam>(this.readString()!!)
    )

fun Parcel.toParcelableMiddleParam(): MiddleParam? {
    return when (val tag = this.readString()) {
        MiddleParam.EMPTY -> MiddleParam.Empty
        MiddleParam.HINT -> MiddleParam.Hint(this.readString()!!)
        MiddleParam.WITH_PARAM -> MiddleParam.WithParam(Param.create(this))
        else -> throw IllegalArgumentException("Unknown MiddleParam tag $tag")
    }
}

fun Parcel.toParcelableParam(): Param? =
    if (this.readByte() == 0.toByte())
        Param.Pair(this.toParcelableParamType()!!, this.toParcelableParamType()!!)
    else this.toParcelableParamType()


private fun Parcel.toParcelableParamType(): Param.Type? = when (val tag = this.readString()) {
    Param.STRING_LIST -> {
        val parcel = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Param.Type.StringList(
                arrayListOf<String>().apply {
                    parcel.readList(
                        this, String::class.java.classLoader, String::class.java
                    )
                }
            )
        } else {
            Param.Type.StringList(
                parcel.readBundle(this.javaClass.classLoader)!!
                    .getStringArrayList("list")!!
                    .toList()
            )
        }
    }

    Param.IMAGE -> {
        Param.Type.Image(this.readString()!!)
    }

    Param.AUDIO -> {
        Param.Type.Audio(this.readString()!!)
    }

    Param.NOTATION -> {
        Param.Type.Notation(this.readString()!!)
    }

    Param.STRING -> {
        Param.Type.String(this.readString()!!)
    }
    else -> throw IllegalArgumentException("Unknown Param tag $tag")
}

