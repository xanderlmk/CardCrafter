package com.belmontCrest.cardCrafter.localDatabase.tables

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import org.json.JSONArray

@Serializable
@Parcelize
@Entity(
    tableName = "basicCard",
    foreignKeys = [
        ForeignKey(
            entity = Card::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
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
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cardId"])]
)
data class ThreeFieldCard(
    @PrimaryKey val cardId: Int,
    val question: String,
    val middle: String,
    val answer: String,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    companion object : Parceler<ThreeFieldCard> {

        override fun ThreeFieldCard.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(cardId)
            parcel.writeString(question)
            parcel.writeString(middle)
            parcel.writeString(answer)
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
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cardId"])]
)
data class HintCard(
    @PrimaryKey val cardId: Int,
    val question: String,
    val hint: String,
    val answer: String,
) : Parcelable  {
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
            onDelete = ForeignKey.CASCADE
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
            onDelete = ForeignKey.CASCADE
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
    constructor(parcel: Parcel) : this (
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

class ListStringConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {

        if (value == "none"){ return emptyList() }
        val jsonArray = JSONArray(value)
        val list = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getString(i))
        }
        return list
    }
    @TypeConverter
    fun listToString(listOfStrings: List<String>): String {
        if (listOfStrings.isEmpty()) { return "none" }
        return JSONArray(listOfStrings).toString()
    }
}