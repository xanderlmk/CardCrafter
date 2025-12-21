package com.belmontCrest.cardCrafter.local.db.tables

import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.belmontCrest.cardCrafter.BuildConfig
import com.belmontCrest.cardCrafter.local.db.tables.customCardInit.AnswerParam
import com.belmontCrest.cardCrafter.local.db.tables.customCardInit.MiddleParam
import com.belmontCrest.cardCrafter.local.db.tables.customCardInit.MiddleParam.Companion.write
import com.belmontCrest.cardCrafter.local.db.tables.customCardInit.Param
import com.belmontCrest.cardCrafter.local.db.tables.customCardInit.Param.Companion.write
import com.belmontCrest.cardCrafter.local.db.tables.customCardInit.deleteFile
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.collections.toList

private const val CARD_ID = "card_id"
private const val BASIC = BuildConfig.SB_BASIC_TN
private const val THREE = BuildConfig.SB_THREE_TN
private const val HINT = BuildConfig.SB_HINT_TN
private const val MULTI = BuildConfig.SB_MULTI_TN
private const val NOTATION = BuildConfig.SB_NOTATION_TN

@Serializable
@SerialName(BASIC)
@Parcelize
@Entity(
    tableName = "basicCard",
    foreignKeys = [
        ForeignKey(
            entity = Card::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cardId"])]
)
data class BasicCard(
    @SerialName(CARD_ID)
    @PrimaryKey val cardId: Int,
    val question: String,
    val answer: String,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!
    )

    companion object : Parceler<BasicCard> {

        override fun BasicCard.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(cardId)
            parcel.writeString(question)
            parcel.writeString(answer)
        }

        override fun create(parcel: Parcel): BasicCard {
            return BasicCard(parcel)
        }
    }
}

@Serializable
@SerialName(THREE)
@Parcelize
@Entity(
    tableName = "threeFieldCard",
    foreignKeys = [
        ForeignKey(
            entity = Card::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cardId"])]
)
data class ThreeFieldCard(
    @SerialName(CARD_ID)
    @PrimaryKey val cardId: Int,
    val question: String,
    val middle: String,
    val answer: String,
    val field: PartOfQorA = PartOfQorA.A
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        if (parcel.readByte() != 0.toByte()) PartOfQorA.Q else PartOfQorA.A
    )

    companion object : Parceler<ThreeFieldCard> {

        override fun ThreeFieldCard.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(cardId)
            parcel.writeString(question)
            parcel.writeString(middle)
            parcel.writeString(answer)
            val byteValue: Byte = if (field is PartOfQorA.Q) 1 else 0
            parcel.writeByte(byteValue)
        }

        override fun create(parcel: Parcel): ThreeFieldCard {
            return ThreeFieldCard(parcel)
        }
    }
}

@Serializable
@SerialName(HINT)
@Parcelize
@Entity(
    tableName = "hintCard",
    foreignKeys = [
        ForeignKey(
            entity = Card::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cardId"])]
)
data class HintCard(
    @SerialName(CARD_ID)
    @PrimaryKey val cardId: Int,
    val question: String,
    val hint: String,
    val answer: String,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    companion object : Parceler<HintCard> {

        override fun HintCard.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(cardId)
            parcel.writeString(question)
            parcel.writeString(hint)
            parcel.writeString(answer)
        }

        override fun create(parcel: Parcel): HintCard {
            return HintCard(parcel)
        }
    }
}

@Serializable
@SerialName(MULTI)
@Parcelize
@Entity(
    tableName = "multiChoiceCard",
    foreignKeys = [
        ForeignKey(
            entity = Card::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cardId"])]
)
data class MultiChoiceCard(
    @SerialName(CARD_ID)
    @PrimaryKey val cardId: Int,
    val question: String,
    val choiceA: String,
    val choiceB: String,
    val choiceC: String = "",
    val choiceD: String = "",
    val correct: Char
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!![0]
    )

    companion object : Parceler<MultiChoiceCard> {

        override fun MultiChoiceCard.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(cardId)
            parcel.writeString(question)
            parcel.writeString(choiceA)
            parcel.writeString(choiceB)
            parcel.writeString(choiceC)
            parcel.writeString(choiceD)
            parcel.writeString(correct.toString())
        }

        override fun create(parcel: Parcel): MultiChoiceCard {
            return MultiChoiceCard(parcel)
        }
    }
}

@Serializable
@SerialName(NOTATION)
@Parcelize
@Entity(
    tableName = "notationCard",
    foreignKeys = [
        ForeignKey(
            entity = Card::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cardId"])]
)
data class NotationCard(
    @SerialName(CARD_ID)
    @PrimaryKey val cardId: Int,
    val question: String,
    val steps: List<String> = emptyList(),
    val answer: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayListOf<String>().apply {
                parcel.readList(
                    this, String::class.java.classLoader, String::class.java
                )
            }

        } else {
            parcel.readBundle(parcel.javaClass.classLoader)!!
                .getStringArrayList("list")!!
                .toList()
        },
        parcel.readString()!!
    )

    companion object : Parceler<NotationCard> {
        override fun NotationCard.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(cardId)
            parcel.writeString(question)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                parcel.writeList(steps)
            else {
                val b = Bundle()
                b.putStringArrayList("list", ArrayList(steps))
                parcel.writeBundle(b)
            }
            parcel.writeString(answer)
        }

        override fun create(parcel: Parcel): NotationCard {
            return NotationCard(parcel)
        }
    }
}

@Entity(
    tableName = "custom_card",
    foreignKeys = [
        ForeignKey(
            entity = Card::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cardId"])]
)
@Serializable
@Parcelize
data class NullableCustomCard(
    @PrimaryKey val cardId: Int,
    val question: Param,
    @ColumnInfo(name = "middle")
    val middle: MiddleParam? = null,
    val answer: AnswerParam,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        Param.create(parcel),
        parcel.toParcelableMiddleParam(),
        Json.decodeFromString<AnswerParam>(parcel.readString()!!),
    )

    companion object : Parceler<NullableCustomCard> {
        override fun NullableCustomCard.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(cardId)
            question.write(parcel, flags)
            middle?.write(parcel, flags)
            parcel.writeString(Json.encodeToString(AnswerParam.serializer(), answer))
        }

        override fun create(parcel: Parcel): NullableCustomCard {
            return NullableCustomCard(parcel)
        }
    }
}

@Serializable
@Parcelize
data class CustomCard(
    val cardId: Int, val question: Param, val middle: MiddleParam, val answer: AnswerParam
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        Param.create(parcel),
        Json.decodeFromString<MiddleParam>(parcel.readString()!!),
        Json.decodeFromString<AnswerParam>(parcel.readString()!!),
    )

    companion object : Parceler<CustomCard> {
        override fun CustomCard.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(cardId)
            question.write(parcel, flags)
            middle.write(parcel, flags)
            parcel.writeString(Json.encodeToString(AnswerParam.serializer(), answer))
        }

        override fun create(parcel: Parcel): CustomCard {
            return CustomCard(parcel)
        }
    }
}

fun NullableCustomCard.toCustomCard(): CustomCard = CustomCard(
    cardId, question, middle ?: MiddleParam.Empty, answer
)

fun CustomCard.toNullableCustomCard(): NullableCustomCard = NullableCustomCard(
    cardId, question, middle, answer
)

fun List<CustomCard>.toNullableCustomCards(): List<NullableCustomCard> =
    this.map { it.toNullableCustomCard() }

fun CustomCard.deleteFiles(): Pair<String, Boolean> {
    try {
        this.question.deleteFile()
        if (this.middle is MiddleParam.WithParam) this.middle.param.deleteFile()
        if (this.answer is AnswerParam.WithParam) this.answer.param.deleteFile()
        return Pair("Success", true)
    } catch (e: Exception) {
        return Pair(e.message ?: "Failed to delete files", false)
    }
}

fun List<CustomCard>.deleteFiles(): Pair<String, Boolean> {
    try {
        this.forEach { it.deleteFiles() }
        return Pair("Success", true)
    } catch (e: Exception) {
        return Pair(e.message ?: "Failed to delete files", false)
    }
}


