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
    @PrimaryKey val cardId : Int,
    val question : String,
    val answer : String,
)

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
    @PrimaryKey val cardId : Int,
    val question : String,
    val middle : String,
    val answer : String,
)
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
    @PrimaryKey val cardId : Int,
    val question : String,
    val hint : String,
    val answer : String,
)

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
    @PrimaryKey val cardId : Int,
    val question : String,
    val choiceA : String,
    val choiceB : String,
    val choiceC : String = "",
    val choiceD : String = "",
    val correct: Char
)

