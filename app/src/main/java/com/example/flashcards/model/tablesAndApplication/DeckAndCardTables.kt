package com.example.flashcards.model.tablesAndApplication

import android.os.Parcel
import android.os.Parcelable
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

@Entity (tableName = "decks",
     indices = [Index(value = ["name"], unique = true)])
data class Deck(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val name : String,
    val uuid: String = UUID.randomUUID().toString(),
    val reviewAmount : Int = 1,
    val goodMultiplier : Double = 1.5,
    val badMultiplier : Double = 0.5,
    val createdOn: Long = Date().time
)
@Parcelize
@Entity(tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = Deck::class,
            parentColumns = ["id"],
            childColumns = ["deckId"]
        )
    ],
    indices = [Index(value = ["deckId"])]
    )
data class Card(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var deckId : Int,
    val deckUUID: String,
    var reviewsLeft : Int,
    var nextReview: Date,
    var passes: Int = 0,
    var prevSuccess: Boolean,
    var totalPasses: Int = 0,
    val type: String,
    val createdOn: Long = Date().time
) : Parcelable {
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
        parcel.readLong()
    )

    companion object : Parceler<Card> {

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
        }

        override fun create(parcel: Parcel): Card {
            return Card(parcel)
        }
    }
}

@Parcelize
@Entity(tableName = "savedCards")
data class SavedCard(
    @PrimaryKey val id: Int,
    var reviewsLeft : Int,
    var nextReview: Date,
    var passes: Int,
    var prevSuccess: Boolean,
    var totalPasses: Int,
) : Parcelable

// Decks has many cards, one card belongs to a deck
data class DeckWithCards(
    @Embedded val deck: Deck,
    @Relation(
        parentColumn = "id",
        entityColumn = "deckId",
    )
    val cards: List<Card>
)



class NonNullConverter {
    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }
    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}