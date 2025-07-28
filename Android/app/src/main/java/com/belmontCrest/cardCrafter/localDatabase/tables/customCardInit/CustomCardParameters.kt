package com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import android.util.Log
import androidx.core.net.toUri
import androidx.room.TypeConverter
import com.belmontCrest.cardCrafter.views.miscFunctions.copyFileToInternalStorage
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

private const val PARAM = "Param"
private const val PARAM_TYPE = "Param.Type"

@Serializable
@SerialName(PARAM)
@Parcelize
sealed class Param : Parcelable {

    @Serializable
    @SerialName(PARAM_TYPE)
    @Parcelize
    sealed class Type : Param() {
        @Serializable
        @SerialName("$PARAM_TYPE.String")
        @Parcelize
        data class String(val s: kotlin.String) : Type()

        @Serializable
        @SerialName("$PARAM_TYPE.StringList")
        @Parcelize
        data class StringList(val list: List<kotlin.String>) : Type()

        @Serializable
        @SerialName("$PARAM_TYPE.Image")
        @Parcelize
        data class Image(val uri: kotlin.String) : Type()

        @Serializable
        @SerialName("$PARAM_TYPE.Audio")
        @Parcelize
        data class Audio(val uri: kotlin.String) : Type()

        @Serializable
        @SerialName("$PARAM_TYPE.Notation")
        @Parcelize
        data class Notation(val s: kotlin.String) : Type()
    }

    @Serializable
    @SerialName("$PARAM.Pair")
    @Parcelize
    data class Pair(val first: Type, val second: Type) : Param()
}

private const val MIDDLE_PARAM = "MiddleParam"

@Serializable
@SerialName(MIDDLE_PARAM)
@Parcelize
sealed class MiddleParam : Parcelable {
    @Serializable
    @SerialName("$MIDDLE_PARAM.Hint")
    @Parcelize
    data class Hint(val h: String) : MiddleParam()

    @Serializable
    @SerialName("$MIDDLE_PARAM.Choice")
    @Parcelize
    data class Choice(val choices: List<String>, val correct: Char) : MiddleParam()

    @Serializable
    @SerialName("$MIDDLE_PARAM.WithParam")
    @Parcelize
    data class WithParam(val param: Param) : MiddleParam()

    @Serializable
    @SerialName("$MIDDLE_PARAM.Empty")
    @Parcelize
    data object Empty : MiddleParam()
}

private const val ANSWER_PARAM = "AnswerParam"

@Serializable
@SerialName(ANSWER_PARAM)
@Parcelize
sealed class AnswerParam : Parcelable {
    @Serializable
    @SerialName("$ANSWER_PARAM.NotationList")
    @Parcelize
    data class NotationList(val steps: List<String>, val a: String) : AnswerParam()

    @Serializable
    @SerialName("$ANSWER_PARAM.WithParam")
    @Parcelize
    data class WithParam(val param: Param) : AnswerParam()
}

fun Param.toQuestion(): String = when (this) {
    is Param.Pair -> this.first.toQuestion()
    is Param.Type.Audio -> "Audio: ${this.uri}"
    is Param.Type.Image -> "Image: ${this.uri}"
    is Param.Type.Notation -> this.s
    is Param.Type.String -> this.s
    is Param.Type.StringList -> this.list[0]
}

fun Param.isBlank(): Boolean = when (this) {
    is Param.Pair -> this.first.isBlank() || this.second.isBlank()
    is Param.Type.Audio -> this.uri.isBlank()
    is Param.Type.Image -> this.uri.isBlank()
    is Param.Type.Notation -> this.s.isBlank()
    is Param.Type.String -> this.s.isBlank()
    is Param.Type.StringList -> this.list.isNotEmpty() && this.list.all { it.isBlank() }
}

fun Param.isNotBlankOrEmpty(): Boolean = when (this) {
    is Param.Pair -> this.first.isNotBlankOrEmpty() && this.second.isNotBlankOrEmpty()
    is Param.Type.Audio -> this.uri.isNotBlank()
    is Param.Type.Image -> this.uri.isNotBlank()
    is Param.Type.Notation -> this.s.isNotBlank()
    is Param.Type.String -> this.s.isNotBlank()
    is Param.Type.StringList -> this.list.isNotEmpty() && this.list.all { it.isNotBlank() }
}

fun MiddleParam.isBlank(): Boolean = when (this) {
    is MiddleParam.Choice -> {
        (this.choices.isNotEmpty() && this.choices.all { it.isBlank() }) ||
                (correct == '-' || correct == Int.MIN_VALUE.toString()[0])
    }

    MiddleParam.Empty -> false
    is MiddleParam.Hint -> this.h.isBlank()
    is MiddleParam.WithParam -> this.param.isBlank()
}

fun MiddleParam.isNotBlankOrEmpty(): Boolean = when (this) {
    is MiddleParam.Choice -> {
        (this.choices.isNotEmpty() && this.choices.all { it.isNotBlank() }) &&
                (correct != '-' || correct != Int.MIN_VALUE.toString()[0])
    }

    MiddleParam.Empty -> true
    is MiddleParam.Hint -> this.h.isNotBlank()
    is MiddleParam.WithParam -> this.param.isNotBlankOrEmpty()
}

fun AnswerParam.isBlank(): Boolean = when (this) {
    is AnswerParam.NotationList -> this.steps.isNotEmpty() && this.steps.all { it.isBlank() } ||
            this.a.isBlank()

    is AnswerParam.WithParam -> this.param.isBlank()
}

fun AnswerParam.isNotBlankOrEmpty(): Boolean = when (this) {
    is AnswerParam.NotationList -> this.steps.isNotEmpty() && this.steps.all { it.isNotBlank() } &&
            this.a.isNotBlank()

    is AnswerParam.WithParam -> this.param.isNotBlankOrEmpty()
}


/**
 *  Delete the file if it's an Image or Audio.
 *  If it's a Pair, recursively call this function to delete the Image or Audio if it exists.
 */
fun Param.deleteFile() {
    when (this) {
        is Param.Type.Image -> {
            deleteFileIfLocal(this.uri.toUri())
        }

        is Param.Type.Audio -> {
            deleteFileIfLocal(this.uri.toUri())
        }

        is Param.Pair -> {
            this.first.deleteFile(); this.second.deleteFile()
        }

        else -> {
            return
        }
    }
}

fun Param.Type.Image.getUri(): Uri = this.uri.toUri()
fun Param.Type.Audio.getUri(): Uri = this.uri.toUri()


data class PairedUris(
    val first: Pair<Uri?, String>,
    val second: Pair<Uri?, String>
)

private const val IMAGE_STR = "image"
private const val AUDIO_STR = "audio"

fun Param.Pair.getPairedUri(): PairedUris =
    if (this.first is Param.Type.Image && this.second is Param.Type.Image)
        PairedUris(Pair(this.first.getUri(), IMAGE_STR), Pair(this.second.getUri(), IMAGE_STR))
    else if (this.first is Param.Type.Image && this.second is Param.Type.Audio)
        PairedUris(Pair(this.first.getUri(), IMAGE_STR), Pair(this.second.getUri(), AUDIO_STR))
    else if (this.first is Param.Type.Audio && this.second is Param.Type.Image)
        PairedUris(Pair(this.first.getUri(), AUDIO_STR), Pair(this.second.getUri(), IMAGE_STR))
    else if (this.first is Param.Type.Audio && this.second is Param.Type.Audio)
        PairedUris(Pair(this.first.getUri(), AUDIO_STR), Pair(this.second.getUri(), AUDIO_STR))
    else if (this.first is Param.Type.Image)
        PairedUris(Pair(this.first.getUri(), IMAGE_STR), Pair(null, ""))
    else if (this.first is Param.Type.Audio)
        PairedUris(Pair(this.first.getUri(), AUDIO_STR), Pair(null, ""))
    else if (this.second is Param.Type.Image)
        PairedUris(Pair(null, ""), Pair(this.second.getUri(), IMAGE_STR))
    else if (this.second is Param.Type.Audio)
        PairedUris(Pair(null, ""), Pair(this.second.getUri(), AUDIO_STR))
    else PairedUris(Pair(null, ""), Pair(null, ""))


fun Param.saveFiles(context: Context): Pair<Boolean, Param> =
    try {
        when (this) {
            is Param.Pair -> {
                val (f, s) = this.getPairedUri()
                val firstUri = f.first
                val secondUri = s.first
                val new = if (firstUri != null && secondUri != null) {
                    val (fPrefix, fExt) =
                        if (f.second == IMAGE_STR) Pair("img", "jpg") else Pair("audio", "mp3")
                    val (sPrefix, sExt) =
                        if (s.second == IMAGE_STR) Pair("img", "jpg") else Pair("audio", "mp3")
                    val newF =
                        copyFileToInternalStorage(
                            context, firstUri, prefix = fPrefix, extName = fExt
                        )
                    val newS =
                        copyFileToInternalStorage(
                            context, secondUri, prefix = sPrefix, extName = sExt
                        )
                    Pair(
                        true, Param.Pair(
                            if (f.second == IMAGE_STR) Param.Type.Image(newF.toString())
                            else Param.Type.Audio(newF.toString()),
                            if (s.second == IMAGE_STR) Param.Type.Image(newS.toString())
                            else Param.Type.Audio(newS.toString())
                        )
                    )

                } else if (firstUri != null) {
                    val (fPrefix, fExt) =
                        if (f.second == IMAGE_STR) Pair("img", "jpg") else Pair("audio", "mp3")
                    val newF =
                        copyFileToInternalStorage(
                            context, firstUri, prefix = fPrefix, extName = fExt
                        )
                    Pair(
                        true, Param.Pair(
                            if (f.second == IMAGE_STR) Param.Type.Image(newF.toString())
                            else Param.Type.Audio(newF.toString()),
                            this.second
                        )
                    )

                } else if (secondUri != null) {
                    val (sPrefix, sExt) =
                        if (s.second == IMAGE_STR) Pair("img", "jpg") else Pair("audio", "mp3")
                    val newS =
                        copyFileToInternalStorage(
                            context, secondUri, prefix = sPrefix, extName = sExt
                        )
                    Pair(
                        true, Param.Pair(
                            this.first,
                            if (s.second == IMAGE_STR) Param.Type.Image(newS.toString())
                            else Param.Type.Audio(newS.toString())
                        )
                    )
                } else {
                    Pair(true, this)
                }
                new
            }

            is Param.Type.Image -> {
                val uri = this.getUri()
                val newUri =
                    copyFileToInternalStorage(context, uri, prefix = "img", extName = "jpg")
                Pair(true, Param.Type.Image(newUri.toString()))
            }

            is Param.Type.Audio -> {
                val uri = this.getUri()
                val newUri =
                    copyFileToInternalStorage(context, uri, prefix = "audio", extName = "mp3")

                Pair(true, Param.Type.Audio(newUri.toString()))
            }

            else -> {
                Pair(true, this)
            }
        }
    } catch (e: Exception) {
        Log.e("Param.saveFiles", "Failed to save files: $e")
        Pair(false, this)
    }

fun MiddleParam.saveFiles(context: Context): Pair<Boolean, MiddleParam> =
    if (this is MiddleParam.WithParam) {
        val (first, second) = this.param.saveFiles(context)
        Pair(first, MiddleParam.WithParam(second))
    } else Pair(true, this)


fun AnswerParam.saveFiles(context: Context): Pair<Boolean, AnswerParam> =
    if (this is AnswerParam.WithParam) {
        val (first, second) = this.param.saveFiles(context)
        Pair(first, AnswerParam.WithParam(second))
    } else Pair(true, this)

private fun deleteFileIfLocal(uri: Uri) {
    uri.takeIf { it.scheme == "file" }?.path?.let { File(it).delete() }
}

class ParamConverter {
    @TypeConverter
    fun fromStringToParam(value: String): Param = Json.decodeFromString<Param>(value)


    @TypeConverter
    fun toStringFromParam(value: Param): String = Json.encodeToString(Param.serializer(), value)


    @TypeConverter
    fun fromStringToMP(value: String?): MiddleParam =
        if (value == null) MiddleParam.Empty
        else Json.decodeFromString<MiddleParam>(value)


    @TypeConverter
    fun toStringFromMP(value: MiddleParam): String? =
        if (value is MiddleParam.Empty) null
        else Json.encodeToString(MiddleParam.serializer(), value)


    @TypeConverter
    fun fromStringToAP(value: String): AnswerParam = Json.decodeFromString<AnswerParam>(value)

    @TypeConverter
    fun toStringFromAP(value: AnswerParam): String =
        Json.encodeToString(AnswerParam.serializer(), value)
}
