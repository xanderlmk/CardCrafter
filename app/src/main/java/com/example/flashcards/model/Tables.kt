package com.example.flashcards.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

// setting up tables (for now Deck -> Decks)
@Entity (tableName = "decks")
data class Decks(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val name : String
)
/*@Entity(tableName = "cards_table")
data class Card(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "deckId") val deckId : Int,
    @ColumnInfo(name = "question") val question : String,
    @ColumnInfo(name = "answer") val answer : String
)
// Decks has many cards, one card belongs to a deck
data class DeckWithCards(
    @Embedded val deck: Decks,
    @Relation(
        parentColumn = "id",
        entityColumn = "deckId"
    )
    val cards: List<Card>
)*/