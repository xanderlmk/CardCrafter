@file:Suppress("PropertyName")

package com.belmontCrest.cardCrafter.localDatabase.tables

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Date
import java.util.UUID

@Serializable
@Parcelize
@Entity(
    tableName = "decks",
    indices = [
        Index(value = ["name"], unique = true),
        Index(value = ["uuid"], unique = true)
    ]
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
    @Serializable(with = DateAsLong::class)
    val nextReview: Date,
    val cardsLeft: Int = 20,
    val cardsDone: Int = 0,
    @Serializable(with = DateAsLong::class)
    val lastUpdated: Date,
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
            parcel.writeInt(cardsDone)
            parcel.writeLong(lastUpdated.time)
        }

        override fun create(parcel: Parcel): Deck {
            return Deck(parcel)
        }
    }
}

@Serializable
@Parcelize
@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = Deck::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["deckId"]),
        Index(value = ["deckUUID", "deckCardNumber"], unique = true),
        Index(value = ["cardIdentifier"], unique = true)]
)
data class Card(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deckId: Int,
    val deckUUID: String,
    val reviewsLeft: Int,
    @Serializable(with = DateAsLong::class)
    val nextReview: Date,
    val passes: Int = 0,
    val prevSuccess: Boolean,
    val totalPasses: Int = 0,
    val type: String,
    val createdOn: Long = Date().time,
    val partOfList: Boolean = false,
    val deckCardNumber: Int?,
    val cardIdentifier: String
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
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readString()!!
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
            parcel.writeByte(if (partOfList) 1 else 0)
            parcel.writeInt(deckCardNumber!!)
            parcel.writeString(cardIdentifier)
        }

        override fun create(parcel: Parcel): Card {
            return Card(parcel)
        }
    }
}

@Parcelize
@Entity(
    tableName = "saved_card",
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
data class SavedCard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val createdOn: Date,
    val cardId: Int,
    val reviewsLeft: Int,
    val nextReview: Date,
    val passes: Int,
    val prevSuccess: Boolean,
    val totalPasses: Int,
    val partOfList: Boolean
) : Parcelable

data class CardRemains(
    val deckId: Int,
    val deckUUID: String,
    val type: String,
    val createdOn: Long,
    val deckCardNumber: Int?,
    val cardIdentifier: String
)

data class CIForID(val cardIdentifier: String)

object DateAsLong : KSerializer<Date> {
    override val descriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: Date) =
        encoder.encodeLong(value.time)

    override fun deserialize(decoder: Decoder) =
        Date(decoder.decodeLong())
}