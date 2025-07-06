package com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ParamTypeEnum")
@Parcelize
sealed class ParamType : Parcelable {
    @Serializable
    @SerialName("ParamType.Paired")
    @Parcelize
    data class Pair(val first: LT, val second: LT) : ParamType()

    @Serializable
    @SerialName("ParamType.LT")
    @Parcelize
    sealed class LT : ParamType() {
        @Serializable
        @SerialName("ParamType.LT.STRING")
        @Parcelize
        data object STRING : LT()

        @Serializable
        @SerialName("ParamType.LT.StringList")
        @Parcelize
        data object StringList : LT()

        @Serializable
        @SerialName("ParamType.LT.IMAGE")
        @Parcelize
        data object IMAGE : LT()

        @Serializable
        @SerialName("ParamType.LT.AUDIO")
        @Parcelize
        data object AUDIO : LT()

        @Serializable
        @SerialName("ParamType.LT.NOTATION")
        @Parcelize
        data object NOTATION : LT()
    }
}

@Serializable
@SerialName("MiddleTypeEnum")
@Parcelize
sealed class MiddleType : Parcelable {
    @Serializable
    @SerialName("MiddleType.Hint")
    @Parcelize
    data object HINT : MiddleType()

    @Serializable
    @SerialName("MiddleType.Choice")
    @Parcelize
    data object CHOICE : MiddleType()

    @Serializable
    @SerialName("MiddleType.WithParam")
    @Parcelize
    data class WithParam(val param: ParamType) : MiddleType()

    @Serializable
    @SerialName("MiddleType.Empty")
    @Parcelize
    data object EMPTY : MiddleType()
}

@Serializable
@SerialName("AnswerTypeEnum")
@Parcelize
sealed class AnswerType : Parcelable {
    @Serializable
    @SerialName("AnswerType.NotationList")
    @Parcelize
    data object NotationList : AnswerType()

    @Serializable
    @SerialName("AnswerType.WithParam")
    @Parcelize
    data class WithParam(val param: ParamType) : AnswerType()

}

fun ParamType.defaultParam(): Param = when (this) {
    is ParamType.LT.AUDIO -> Param.Type.Audio("")
    is ParamType.LT.IMAGE -> Param.Type.Image("")
    is ParamType.LT.NOTATION -> Param.Type.Notation("")
    is ParamType.LT.STRING -> Param.Type.String("")
    is ParamType.LT.StringList -> Param.Type.StringList(emptyList())
    is ParamType.Pair -> Param.Pair(this.first.defaultParam(), this.second.defaultParam())
}

fun ParamType.LT.defaultParam(): Param.Type = when (this) {
    is ParamType.LT.AUDIO -> Param.Type.Audio("")
    is ParamType.LT.IMAGE -> Param.Type.Image("")
    is ParamType.LT.NOTATION -> Param.Type.Notation("")
    is ParamType.LT.STRING -> Param.Type.String("")
    is ParamType.LT.StringList -> Param.Type.StringList(emptyList())
}

fun MiddleType.defaultParam(): MiddleParam = when (this) {
    is MiddleType.HINT -> MiddleParam.Hint("")
    is MiddleType.CHOICE -> MiddleParam.Choice(listOf("", ""), '-')
    is MiddleType.WithParam -> MiddleParam.WithParam(this.param.defaultParam())
    is MiddleType.EMPTY -> MiddleParam.Empty
}

fun AnswerType.defaultParam(): AnswerParam = when (this) {
    is AnswerType.NotationList -> AnswerParam.NotationList(emptyList(), "")
    is AnswerType.WithParam -> AnswerParam.WithParam(this.param.defaultParam())
}


fun Param.toParamType(): ParamType = when (this) {
    is Param.Pair -> ParamType.Pair(this.first.toParamType(), this.second.toParamType())
    is Param.Type.Audio -> ParamType.LT.AUDIO
    is Param.Type.Image -> ParamType.LT.IMAGE
    is Param.Type.Notation -> ParamType.LT.NOTATION
    is Param.Type.String -> ParamType.LT.STRING
    is Param.Type.StringList -> ParamType.LT.StringList
}

fun Param.Type.toParamType(): ParamType.LT = when (this) {
    is Param.Type.Audio -> ParamType.LT.AUDIO
    is Param.Type.Image -> ParamType.LT.IMAGE
    is Param.Type.Notation -> ParamType.LT.NOTATION
    is Param.Type.String -> ParamType.LT.STRING
    is Param.Type.StringList -> ParamType.LT.StringList
}

fun MiddleParam.toParamType(): MiddleType = when (this) {
    is MiddleParam.Choice -> MiddleType.CHOICE
    is MiddleParam.Empty -> MiddleType.EMPTY
    is MiddleParam.Hint -> MiddleType.HINT
    is MiddleParam.WithParam -> MiddleType.WithParam(this.param.toParamType())
}

fun AnswerParam.toParamType(): AnswerType = when (this) {
    is AnswerParam.NotationList -> AnswerType.NotationList
    is AnswerParam.WithParam -> AnswerType.WithParam(this.param.toParamType())
}