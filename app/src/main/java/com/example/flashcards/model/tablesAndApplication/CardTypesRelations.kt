package com.example.flashcards.model.tablesAndApplication

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize


data class BasicCardType(
    @Embedded override val card: Card,
    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val basicCard: BasicCard?
) : SealedCT()

data class HintCardType(
    @Embedded override val card: Card,
    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val hintCard: HintCard?
)  : SealedCT()

data class ThreeCardType(
    @Embedded override val card: Card,
    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val threeFieldCard: ThreeFieldCard?
)  : SealedCT()

data class MultiChoiceCardType(
    @Embedded override val card: Card,
    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val multiChoiceCard: MultiChoiceCard?
) : SealedCT()

sealed class CardType {
    abstract val cardId: Int
}
sealed class SealedCT {
    abstract val card : Card
}

@Parcelize
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
) : Parcelable {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Card::class.java.classLoader, Card::class.java)!!,
        parcel.readParcelable(BasicCard::class.java.classLoader, BasicCard::class.java),
        parcel.readParcelable(HintCard::class.java.classLoader, HintCard::class.java),
        parcel.readParcelable(ThreeFieldCard::class.java.classLoader, ThreeFieldCard::class.java),
        parcel.readParcelable(MultiChoiceCard::class.java.classLoader, MultiChoiceCard::class.java)
    )

    companion object : Parceler<AllCardTypes> {

        override fun AllCardTypes.write(parcel: Parcel, flags: Int) {
            parcel.writeParcelable(card, flags)
            parcel.writeParcelable(basicCard, flags)
            parcel.writeParcelable(hintCard, flags)
            parcel.writeParcelable(threeFieldCard, flags)
            parcel.writeParcelable(multiChoiceCard, flags)
        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun create(parcel: Parcel): AllCardTypes {
            return AllCardTypes(parcel)
        }
    }
}

/** future reference for non-nullable relations
sealed class CT {
    data class Basic(
        @Embedded var card: Card,
        @Relation(
            parentColumn = "id",
            entityColumn = "cardId"
        )
        val basicCard: BasicCard
    ) : CT()

    data class Hint(
        @Embedded var card: Card,
        @Relation(
            parentColumn = "id",
            entityColumn = "cardId"
        )
        val hintCard: HintCard
    ) : CT()

    data class ThreeField(
        @Embedded var card: Card,
        @Relation(
            parentColumn = "id",
            entityColumn = "cardId"
        )
        val threeFieldCard: ThreeFieldCard
    ) : CT()

    data class MultiChoice(
        @Embedded var card: Card,
        @Relation(
            parentColumn = "id",
            entityColumn = "cardId"
        )
        val multiChoiceCard: MultiChoiceCard
    ) : CT()
}*/
