package com.example.flashcards.model.tablesAndApplication

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverter
import java.util.Date

@Entity (tableName = "decks",
     indices = [Index(value = ["name"], unique = true)])
data class Deck(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val name : String,
    var multiplier : Double = 1.5
)
@Entity(tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = Deck::class,
            parentColumns = ["id"],
            childColumns = ["deckId"]
        )
    ]
    )
data class Card(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deckId : Int,
    var nextReview: Date?,
    var passes: Int = 0,
    var prevSuccess: Boolean,
    var totalPasses: Int = 0,
    val type: String
)
// Decks has many cards, one card belongs to a deck
data class DeckWithCards(
    @Embedded val deck: Deck,
    @Relation(
        parentColumn = "id",
        entityColumn = "deckId",
    )
    val cards: List<Card>
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}