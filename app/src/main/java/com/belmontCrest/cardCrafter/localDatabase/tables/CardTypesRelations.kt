package com.belmontCrest.cardCrafter.localDatabase.tables

import android.os.Parcel
import android.os.Parcelable
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
    constructor(parcel: Parcel) : this(
        toParcelableCard(parcel)!!,
        toParcelableBasicCard(parcel),
        toParcelableHintCard(parcel),
        toParcelableThreeCard(parcel),
        toParcelableMultiCard(parcel),
        toParcelableNotationCard(parcel)
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
        constructor(parcel: Parcel) : this(
            toParcelableCard(parcel)!!, toParcelableBasicCard(parcel)!!,
        )

        companion object : Parceler<Basic> {
            override fun Basic.write(parcel: Parcel, flags: Int) {
                parcel.writeParcelable(card, flags)
                parcel.writeParcelable(basicCard, flags)
            }

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
        constructor(parcel: Parcel) : this(
            toParcelableCard(parcel)!!, toParcelableHintCard(parcel)!!
        )

        companion object : Parceler<Hint> {
            override fun Hint.write(parcel: Parcel, flags: Int) {
                parcel.writeParcelable(card, flags)
                parcel.writeParcelable(hintCard, flags)

            }

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
        constructor(parcel: Parcel) : this(
            toParcelableCard(parcel)!!, toParcelableThreeCard(parcel)!!
        )

        companion object : Parceler<ThreeField> {
            override fun ThreeField.write(parcel: Parcel, flags: Int) {
                parcel.writeParcelable(card, flags)
                parcel.writeParcelable(threeFieldCard, flags)
            }

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
        constructor(parcel: Parcel) : this(
            toParcelableCard(parcel)!!, toParcelableMultiCard(parcel)!!
        )

        companion object : Parceler<MultiChoice> {
            override fun MultiChoice.write(parcel: Parcel, flags: Int) {
                parcel.writeParcelable(card, flags)
                parcel.writeParcelable(multiChoiceCard, flags)
            }

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
        constructor(parcel: Parcel) : this(
            toParcelableCard(parcel)!!, toParcelableNotationCard(parcel)!!
        )

        companion object : Parceler<Notation> {
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