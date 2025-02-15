package com.example.flashcards.model.tablesAndApplication

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

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
    val multiChoiceCard: MultiChoiceCard?,
    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val mathCard: MathCard?
) : Parcelable {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Card::class.java.classLoader, Card::class.java)!!,
        parcel.readParcelable(BasicCard::class.java.classLoader, BasicCard::class.java),
        parcel.readParcelable(HintCard::class.java.classLoader, HintCard::class.java),
        parcel.readParcelable(ThreeFieldCard::class.java.classLoader, ThreeFieldCard::class.java),
        parcel.readParcelable(MultiChoiceCard::class.java.classLoader, MultiChoiceCard::class.java),
        parcel.readParcelable(MathCard::class.java.classLoader, MathCard::class.java)
    )

    companion object : Parceler<AllCardTypes> {

        override fun AllCardTypes.write(parcel: Parcel, flags: Int) {
            parcel.writeParcelable(card, flags)
            parcel.writeParcelable(basicCard, flags)
            parcel.writeParcelable(hintCard, flags)
            parcel.writeParcelable(threeFieldCard, flags)
            parcel.writeParcelable(multiChoiceCard, flags)
            parcel.writeParcelable(mathCard, flags)
        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun create(parcel: Parcel): AllCardTypes {
            return AllCardTypes(parcel)
        }
    }
}

@Parcelize
sealed class CT : Parcelable {
    @Parcelize
    data class Basic(
        @Embedded var card: Card,
        @Relation(
            parentColumn = "id",
            entityColumn = "cardId"
        )
        val basicCard: BasicCard
    ) : CT() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        constructor(parcel: Parcel) : this(
            parcel.readParcelable(Card::class.java.classLoader, Card::class.java)!!,
            parcel.readParcelable(
                BasicCard::class.java.classLoader,
                BasicCard::class.java
            )!!,
        )

        companion object : Parceler<Basic> {
            override fun Basic.write(parcel: Parcel, flags: Int) {
                parcel.writeParcelable(card, flags)
                parcel.writeParcelable(basicCard, flags)
            }

            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun create(parcel: Parcel): Basic {
                return Basic(parcel)
            }
        }
    }

    @Parcelize
    data class Hint(
        @Embedded var card: Card,
        @Relation(
            parentColumn = "id",
            entityColumn = "cardId"
        )
        val hintCard: HintCard
    ) : CT() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        constructor(parcel: Parcel) : this(
            parcel.readParcelable(Card::class.java.classLoader, Card::class.java)!!,
            parcel.readParcelable(
                HintCard::class.java.classLoader,
                HintCard::class.java
            )!!,
        )

        companion object : Parceler<Hint> {
            override fun Hint.write(parcel: Parcel, flags: Int) {
                parcel.writeParcelable(card, flags)
                parcel.writeParcelable(hintCard, flags)

            }

            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun create(parcel: Parcel): Hint {
                return Hint(parcel)
            }
        }
    }

    @Parcelize
    data class ThreeField(
        @Embedded var card: Card,
        @Relation(
            parentColumn = "id",
            entityColumn = "cardId"
        )
        val threeFieldCard: ThreeFieldCard
    ) : CT() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        constructor(parcel: Parcel) : this(
            parcel.readParcelable(Card::class.java.classLoader, Card::class.java)!!,
            parcel.readParcelable(
                ThreeFieldCard::class.java.classLoader,
                ThreeFieldCard::class.java
            )!!,
        )

        companion object : Parceler<ThreeField> {
            override fun ThreeField.write(parcel: Parcel, flags: Int) {
                parcel.writeParcelable(card, flags)
                parcel.writeParcelable(threeFieldCard, flags)
            }

            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun create(parcel: Parcel): ThreeField {
                return ThreeField(parcel)
            }
        }
    }

    @Parcelize
    data class MultiChoice(
        @Embedded var card: Card,
        @Relation(
            parentColumn = "id",
            entityColumn = "cardId"
        )
        val multiChoiceCard: MultiChoiceCard
    ) : CT() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        constructor(parcel: Parcel) : this(
            parcel.readParcelable(Card::class.java.classLoader, Card::class.java)!!,
            parcel.readParcelable(
                MultiChoiceCard::class.java.classLoader,
                MultiChoiceCard::class.java
            )!!,
        )

        companion object : Parceler<MultiChoice> {
            override fun MultiChoice.write(parcel: Parcel, flags: Int) {
                parcel.writeParcelable(card, flags)
                parcel.writeParcelable(multiChoiceCard, flags)
            }

            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun create(parcel: Parcel): MultiChoice {
                return MultiChoice(parcel)
            }
        }
    }
    @Parcelize
    data class Math(
        @Embedded var card: Card,
        @Relation(
            parentColumn = "id",
            entityColumn = "cardId"
        )
        val mathCard: MathCard
    ) : CT() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        constructor(parcel: Parcel) : this(
            parcel.readParcelable(Card::class.java.classLoader, Card::class.java)!!,
            parcel.readParcelable(
                MathCard::class.java.classLoader,
                MathCard::class.java
            )!!,
        )
        companion object : Parceler<Math>  {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun create(parcel: Parcel): Math {
                return Math(parcel)
            }
            override fun Math.write(parcel: Parcel, flags: Int) {
                parcel.writeParcelable(card, flags)
                parcel.writeParcelable(mathCard, flags)
            }
        }
    }
}
