package com.example.flashcards.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverter
import java.util.Date

@Entity (tableName = "decks",
     indices = [Index(value = ["name"], unique = true)])
data class Deck(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val name : String
)
@Entity(tableName = "cards")
data class Card(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deckId : Int,
    val question : String,
    val answer : String,
    var nextReview: Date?,
    var passes: Int,
    var prevSuccess: Boolean,
    var totalPasses: Int
)
// Decks has many cards, one card belongs to a deck
data class DeckWithCards(
    @Embedded val deck: Deck,
    @Relation(
        parentColumn = "id",
        entityColumn = "deckId"
    )
    val cards: List<Card>
)

@Entity(tableName = "basicCard")
data class BasicCard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cardId : Int,
    val question : String,
    val answer : String,
)

@Entity(tableName = "threeFieldCard")
data class ThreeFieldCard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cardId : Int,
    val question : String,
    val answer1 : String,
    val answer2 : String,
)

@Entity(tableName = "hintCard")
data class HintCard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cardId : Int,
    val question : String,
    val hint : String,
    val answer : String,
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