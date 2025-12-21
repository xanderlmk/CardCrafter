package com.belmontCrest.cardCrafter.local.db.tables.customCardInit

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val PARAM_TYPE = "ParamType"
@Serializable
@SerialName("ParamTypeEnum")
@Parcelize
sealed class ParamType : Parcelable {
    @Serializable
    @SerialName("$PARAM_TYPE.Paired")
    @Parcelize
    data class Pair(val first: LT, val second: LT) : ParamType()

    @Serializable
    @SerialName("$PARAM_TYPE.LT")
    @Parcelize
    sealed class LT : ParamType() {
        @Serializable
        @SerialName("$PARAM_TYPE.LT.STRING")
        @Parcelize
        data object STRING : LT()

        @Serializable
        @SerialName("$PARAM_TYPE.LT.StringList")
        @Parcelize
        data object StringList : LT()

        @Serializable
        @SerialName("$PARAM_TYPE.LT.IMAGE")
        @Parcelize
        data object IMAGE : LT()

        @Serializable
        @SerialName("$PARAM_TYPE.LT.AUDIO")
        @Parcelize
        data object AUDIO : LT()

        @Serializable
        @SerialName("$PARAM_TYPE.LT.NOTATION")
        @Parcelize
        data object NOTATION : LT()
    }
}

private const val MIDDLE_TYPE = "MiddleType"
@Serializable
@SerialName("MiddleTypeEnum")
@Parcelize
sealed class MiddleType : Parcelable {
    @Serializable
    @SerialName("$MIDDLE_TYPE.Hint")
    @Parcelize
    data object HINT : MiddleType()

    @Serializable
    @SerialName("$MIDDLE_TYPE.WithParam")
    @Parcelize
    data class WithParam(val param: ParamType) : MiddleType()

    @Serializable
    @SerialName("$MIDDLE_TYPE.Empty")
    @Parcelize
    data object EMPTY : MiddleType()
}

private const val ANSWER_TYPE = "AnswerType"
@Serializable
@SerialName("AnswerTypeEnum")
@Parcelize
sealed class AnswerType : Parcelable {
    @Serializable
    @SerialName("$ANSWER_TYPE.Choice")
    @Parcelize
    data object CHOICE : AnswerType()
    @Serializable
    @SerialName("$ANSWER_TYPE.NotationList")
    @Parcelize
    data object NotationList : AnswerType()

    @Serializable
    @SerialName("$ANSWER_TYPE.WithParam")
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
    is MiddleType.WithParam -> MiddleParam.WithParam(this.param.defaultParam())
    is MiddleType.EMPTY -> MiddleParam.Empty
}

fun AnswerType.defaultParam(): AnswerParam = when (this) {
    is AnswerType.CHOICE -> AnswerParam.Choice(listOf("", ""), '-')
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
    is MiddleParam.Empty -> MiddleType.EMPTY
    is MiddleParam.Hint -> MiddleType.HINT
    is MiddleParam.WithParam -> MiddleType.WithParam(this.param.toParamType())
}

fun AnswerParam.toParamType(): AnswerType = when (this) {
    is AnswerParam.Choice -> AnswerType.CHOICE
    is AnswerParam.NotationList -> AnswerType.NotationList
    is AnswerParam.WithParam -> AnswerType.WithParam(this.param.toParamType())
}