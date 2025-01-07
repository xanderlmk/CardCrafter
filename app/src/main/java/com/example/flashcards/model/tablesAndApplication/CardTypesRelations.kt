package com.example.flashcards.model.tablesAndApplication

import androidx.room.Embedded
import androidx.room.Relation



data class BasicCardType(
    @Embedded val card: Card,
    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val basicCard: BasicCard?
)
data class HintCardType(
    @Embedded val card: Card,
    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val hintCard: HintCard?
)
data class ThreeCardType(
    @Embedded val card: Card,
    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val threeFieldCard: ThreeFieldCard?
)

data class MultiChoiceCardType(
    @Embedded val card: Card,
    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val multiChoiceCard: MultiChoiceCard?
)

sealed class CardType {
    abstract val cardId: Int
}


data class AllCardTypes(
    @Embedded var card: Card,
    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val basicCard: BasicCard?,
    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val hintCard: HintCard?,
    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val threeFieldCard: ThreeFieldCard?,
    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val multiChoiceCard: MultiChoiceCard?
)