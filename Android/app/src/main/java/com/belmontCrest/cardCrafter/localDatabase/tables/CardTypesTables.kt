package com.belmontCrest.cardCrafter.localDatabase.tables

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.AnswerParam
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.MiddleParam
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.Param
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.deleteFile
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
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
@Serializable
@Parcelize
data class NotationCard(
    @PrimaryKey val cardId: Int,
    val question: String,
    val steps: List<String> = emptyList(),
    val answer: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        listOf(parcel.readString()!!),
        parcel.readString()!!
    )

    companion object : Parceler<NotationCard> {
        override fun NotationCard.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(cardId)
            parcel.writeString(question)
            parcel.writeList(steps)
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
        Json.decodeFromString<Param>(parcel.readString()!!),
        parcel.readString()?.let { Json.decodeFromString<MiddleParam?>(it) },
        Json.decodeFromString<AnswerParam>(parcel.readString()!!),
    )

    companion object : Parceler<NullableCustomCard> {
        override fun NullableCustomCard.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(cardId)
            parcel.writeString(Json.encodeToString(Param.serializer(), question))
            parcel.writeString(middle?.let {
                Json.encodeToString(MiddleParam.serializer(), it)
            })
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
        Json.decodeFromString<Param>(parcel.readString()!!),
        Json.decodeFromString<MiddleParam>(parcel.readString()!!),
        Json.decodeFromString<AnswerParam>(parcel.readString()!!),
    )

    companion object : Parceler<CustomCard> {
        override fun CustomCard.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(cardId)
            parcel.writeString(Json.encodeToString(Param.serializer(), question))
            parcel.writeString(
                Json.encodeToString(
                    MiddleParam.serializer(),
                    middle
                )
            )
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


