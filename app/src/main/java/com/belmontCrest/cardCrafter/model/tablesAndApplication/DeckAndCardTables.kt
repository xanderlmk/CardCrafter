@file:Suppress("PropertyName")

package com.belmontCrest.cardCrafter.model.tablesAndApplication

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverter
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
@Entity(
    tableName = "decks",
    indices = [Index(value = ["name"], unique = true)]
)
data class Deck(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val uuid: String = UUID.randomUUID().toString(),
    val reviewAmount: Int = 1,
    val goodMultiplier: Double = 1.5,
    val badMultiplier: Double = 0.5,
    val createdOn: Long = Date().time,
    val cardAmount: Int = 20,
    var nextReview: Date,
    var cardsLeft: Int = 20,
    var lastUpdated : Date
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readLong(),
        parcel.readInt(),
        Date(parcel.readLong()),
        parcel.readInt(),
        Date(parcel.readLong())
        )

    companion object : Parceler<Deck> {
        override fun Deck.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(id)
            parcel.writeString(name)
            parcel.writeString(uuid)
            parcel.writeInt(reviewAmount)
            parcel.writeDouble(goodMultiplier)
            parcel.writeDouble(badMultiplier)
            parcel.writeLong(createdOn)
            parcel.writeInt(cardAmount)
            parcel.writeLong(nextReview.time)
            parcel.writeInt(cardsLeft)
            parcel.writeLong(lastUpdated.time)
        }
        override fun create(parcel: Parcel): Deck {
            return Deck(parcel)
        }
    }
}

@Parcelize
@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = Deck::class,
            parentColumns = ["id"],
            childColumns = ["deckId"]
        )
    ],
    indices = [
        Index(value = ["deckId"]),
        Index(value = ["deckUUID", "deckCardNumber"], unique = true)]
)
data class Card(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var deckId: Int,
    val deckUUID: String,
    var reviewsLeft: Int,
    var nextReview: Date,
    var passes: Int = 0,
    var prevSuccess: Boolean,
    var totalPasses: Int = 0,
    val type: String,
    val createdOn: Long = Date().time,
    var partOfList: Boolean = false,
    val deckCardNumber: Int?,
    val cardIdentifier: String
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Card) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        Date(parcel.readLong()),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readBoolean(),
        parcel.readInt(),
        parcel.readString()!!
    )

    companion object : Parceler<Card> {

        @RequiresApi(Build.VERSION_CODES.Q)
        override fun Card.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(id)
            parcel.writeInt(deckId)
            parcel.writeString(deckUUID)
            parcel.writeInt(reviewsLeft)
            parcel.writeLong(nextReview.time)
            parcel.writeInt(passes)
            parcel.writeByte(if (prevSuccess) 1 else 0)
            parcel.writeInt(totalPasses)
            parcel.writeString(type)
            parcel.writeLong(createdOn)
            parcel.writeBoolean(partOfList)
            parcel.writeInt(deckCardNumber!!)
            parcel.writeString(cardIdentifier)
        }
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun create(parcel: Parcel): Card {
            return Card(parcel)
        }
    }
}

@Parcelize
@Entity(tableName = "savedCards")
data class SavedCard(
    @PrimaryKey val id: Int,
    var reviewsLeft: Int,
    var nextReview: Date,
    var passes: Int,
    var prevSuccess: Boolean,
    var totalPasses: Int,
    var partOfList: Boolean
) : Parcelable

// Decks has many cards, one card belongs to a deck

@Parcelize
data class DeckWithCards(
    @Embedded val deck: Deck,
    @Relation(
        parentColumn = "id",
        entityColumn = "deckId",
    )
    val cards: List<Card>
) : Parcelable


class TimeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}