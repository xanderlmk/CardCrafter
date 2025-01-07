package com.example.flashcards.model.tablesAndApplication

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(tableName = "basicCard",
    foreignKeys = [
        ForeignKey(
            entity = Card::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class BasicCard(
    @PrimaryKey override val  cardId : Int,
    val question : String,
    val answer : String,
) : CardType()

@Entity(tableName = "threeFieldCard",
    foreignKeys = [
        ForeignKey(
            entity = Card::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class ThreeFieldCard(
    @PrimaryKey override val cardId : Int,
    val question : String,
    val middle : String,
    val answer : String,
) : CardType()
@Entity(tableName = "hintCard",
    foreignKeys = [
        ForeignKey(
            entity = Card::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class HintCard(
    @PrimaryKey override val cardId : Int,
    val question : String,
    val hint : String,
    val answer : String,
) : CardType()

@Entity(tableName = "multiChoiceCard",
    foreignKeys = [
        ForeignKey(
            entity = Card::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class MultiChoiceCard(
    @PrimaryKey override val cardId : Int,
    val question : String,
    val choiceA : String,
    val choiceB : String,
    val choiceC : String = "",
    val choiceD : String = "",
    val correct: Char
): CardType()