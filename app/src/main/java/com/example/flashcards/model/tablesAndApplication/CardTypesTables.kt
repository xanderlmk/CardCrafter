package com.example.flashcards.model.tablesAndApplication

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

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
    @PrimaryKey override val cardId: Int,
    val question: String,
    val answer: String,
) : CardType(), Parcelable {
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
    @PrimaryKey override val cardId: Int,
    val question: String,
    val middle: String,
    val answer: String,
) : CardType(), Parcelable {
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
    @PrimaryKey override val cardId: Int,
    val question: String,
    val hint: String,
    val answer: String,
) : CardType(), Parcelable  {
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
    @PrimaryKey override val cardId: Int,
    val question: String,
    val choiceA: String,
    val choiceB: String,
    val choiceC: String = "",
    val choiceD: String = "",
    val correct: Char
) : CardType(), Parcelable {
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