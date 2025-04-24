package com.belmontCrest.cardCrafter.localDatabase.tables

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/** In order to process the cards into a sealed CT, we must get
 *  ALL card types before we map them into a seal CT.
 */
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
    val notationCard: NotationCard?
) : Parcelable {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Card::class.java.classLoader, Card::class.java)!!,
        parcel.readParcelable(BasicCard::class.java.classLoader, BasicCard::class.java),
        parcel.readParcelable(HintCard::class.java.classLoader, HintCard::class.java),
        parcel.readParcelable(ThreeFieldCard::class.java.classLoader, ThreeFieldCard::class.java),
        parcel.readParcelable(MultiChoiceCard::class.java.classLoader, MultiChoiceCard::class.java),
        parcel.readParcelable(NotationCard::class.java.classLoader, NotationCard::class.java)
    )

    companion object : Parceler<AllCardTypes> {

        override fun AllCardTypes.write(parcel: Parcel, flags: Int) {
            parcel.writeParcelable(card, flags)
            parcel.writeParcelable(basicCard, flags)
            parcel.writeParcelable(hintCard, flags)
            parcel.writeParcelable(threeFieldCard, flags)
            parcel.writeParcelable(multiChoiceCard, flags)
            parcel.writeParcelable(notationCard, flags)
        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun create(parcel: Parcel): AllCardTypes {
            return AllCardTypes(parcel)
        }
    }
}

/** A Sealed CT which represents a card with it's specialized card. */
@Serializable
@Parcelize
sealed class CT : Parcelable {
    @Serializable
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
    @Serializable
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
    @Serializable
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
    @Serializable
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
    @Serializable
    @Parcelize
    data class Notation(
        @Embedded var card: Card,
        @Relation(
            parentColumn = "id",
            entityColumn = "cardId"
        )
        val notationCard: NotationCard
    ) : CT() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        constructor(parcel: Parcel) : this(
            parcel.readParcelable(Card::class.java.classLoader, Card::class.java)!!,
            parcel.readParcelable(
                NotationCard::class.java.classLoader,
                NotationCard::class.java
            )!!,
        )
        companion object : Parceler<Notation>  {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun create(parcel: Parcel): Notation {
                return Notation(parcel)
            }
            override fun Notation.write(parcel: Parcel, flags: Int) {
                parcel.writeParcelable(card, flags)
                parcel.writeParcelable(notationCard, flags)
            }
        }
    }
}
