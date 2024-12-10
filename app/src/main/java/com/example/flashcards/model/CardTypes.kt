package com.example.flashcards.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

data class CardWithBasic(
    @Embedded val card: Card,
    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val basicCards: List<BasicCard>
)

@Entity(tableName = "basicCard")
data class BasicCard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cardId : Int,
    val question : String,
    val answer : String,
)

data class CardWithThree(
    @Embedded val card: Card,
    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val threeFieldCards: List<ThreeFieldCard>
)

@Entity(tableName = "threeFieldCard")
data class ThreeFieldCard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cardId : Int,
    val question : String,
    val answer1 : String,
    val answer2 : String,
)

data class CardWithHint(
    @Embedded val card: Card,
    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val hintCards: List<HintCard>
)

@Entity(tableName = "hintCard")
data class HintCard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cardId : Int,
    val question : String,
    val hint : String,
    val answer : String,
)