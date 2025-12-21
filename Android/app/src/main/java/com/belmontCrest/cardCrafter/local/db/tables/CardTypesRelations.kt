package com.belmontCrest.cardCrafter.local.db.tables

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.belmontCrest.cardCrafter.local.db.tables.BasicCard.Companion.write
import com.belmontCrest.cardCrafter.local.db.tables.Card.Companion.write
import com.belmontCrest.cardCrafter.local.db.tables.CustomCard.Companion.write
import com.belmontCrest.cardCrafter.local.db.tables.HintCard.Companion.write
import com.belmontCrest.cardCrafter.local.db.tables.MultiChoiceCard.Companion.write
import com.belmontCrest.cardCrafter.local.db.tables.NotationCard.Companion.write
import com.belmontCrest.cardCrafter.local.db.tables.NullableCustomCard.Companion.write
import com.belmontCrest.cardCrafter.local.db.tables.ThreeFieldCard.Companion.write
import com.belmontCrest.cardCrafter.views.misc.CARD_CRAFTER
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** In order to process the cards into a sealed CT, we must get
 *  ALL card types before we map them into a seal CT.
 */
@Parcelize
data class AllCardTypes(
    @Embedded var card: Card,
    @Relation(
        parentColumn = "id", entityColumn = "cardId"
    )
    val basicCard: BasicCard?,
    @Relation(
        parentColumn = "id", entityColumn = "cardId"
    )
    val hintCard: HintCard?,
    @Relation(
        parentColumn = "id", entityColumn = "cardId"
    )
    val threeFieldCard: ThreeFieldCard?,
    @Relation(
        parentColumn = "id", entityColumn = "cardId"
    )
    val multiChoiceCard: MultiChoiceCard?,
    @Relation(
        parentColumn = "id", entityColumn = "cardId"
    )
    val notationCard: NotationCard?,
    @Relation(
        parentColumn = "id", entityColumn = "cardId"
    )
    val nullableCustomCard: NullableCustomCard?

) : Parcelable {
    constructor(parcel: Parcel) : this(
        Card.create(parcel),
        parcel.toParcelableBasicCard(),
        parcel.toParcelableHintCard(),
        parcel.toParcelableThreeCard(),
        parcel.toParcelableMultiCard(),
        parcel.toParcelableNotationCard(),
        parcel.toParcelableNullableCustomCard()
    )

    companion object : Parceler<AllCardTypes> {

        override fun AllCardTypes.write(parcel: Parcel, flags: Int) {
            card.write(parcel, flags)
            basicCard?.write(parcel, flags)
            hintCard?.write(parcel, flags)
            threeFieldCard?.write(parcel, flags)
            multiChoiceCard?.write(parcel, flags)
            notationCard?.write(parcel, flags)
            nullableCustomCard?.write(parcel, flags)
        }

        override fun create(parcel: Parcel): AllCardTypes {
            return AllCardTypes(parcel)
        }
    }
}

/** A Sealed CT which represents a card with it's specialized card. */
@Serializable
@SerialName("$CARD_CRAFTER.CT")
@Parcelize
sealed class CT : Parcelable {
    /** Basic card type */
    @Serializable
    @SerialName("$CARD_CRAFTER.CT.Basic")
    @Parcelize
    data class Basic(var card: Card, val basicCard: BasicCard) : CT() {
        constructor(parcel: Parcel) : this(
            Card.create(parcel), BasicCard.create(parcel),
        )

        companion object : Parceler<Basic> {
            override fun Basic.write(parcel: Parcel, flags: Int) {
                card.write(parcel, flags)
                basicCard.write(parcel, flags)
            }

            override fun create(parcel: Parcel): Basic {
                return Basic(parcel)
            }
        }
    }

    @Serializable
    @SerialName("$CARD_CRAFTER.CT.Hint")
    @Parcelize
    data class Hint(var card: Card, val hintCard: HintCard) : CT() {
        constructor(parcel: Parcel) : this(
            Card.create(parcel), HintCard.create(parcel)
        )

        companion object : Parceler<Hint> {
            override fun Hint.write(parcel: Parcel, flags: Int) {
                card.write(parcel, flags)
                hintCard.write(parcel, flags)
            }

            override fun create(parcel: Parcel): Hint {
                return Hint(parcel)
            }
        }
    }

    @Serializable
    @SerialName("$CARD_CRAFTER.CT.ThreeField")
    @Parcelize
    data class ThreeField(var card: Card, val threeFieldCard: ThreeFieldCard) : CT() {
        constructor(parcel: Parcel) : this(
            Card.create(parcel), ThreeFieldCard.create(parcel)
        )

        companion object : Parceler<ThreeField> {
            override fun ThreeField.write(parcel: Parcel, flags: Int) {
                card.write(parcel, flags)
                threeFieldCard.write(parcel, flags)
            }

            override fun create(parcel: Parcel): ThreeField {
                return ThreeField(parcel)
            }
        }
    }

    @Serializable
    @SerialName("$CARD_CRAFTER.CT.MultiChoice")
    @Parcelize
    data class MultiChoice(var card: Card, val multiChoiceCard: MultiChoiceCard) : CT() {
        constructor(parcel: Parcel) : this(
            Card.create(parcel), MultiChoiceCard.create(parcel)
        )

        companion object : Parceler<MultiChoice> {
            override fun MultiChoice.write(parcel: Parcel, flags: Int) {
                card.write(parcel, flags)
                multiChoiceCard.write(parcel, flags)
            }

            override fun create(parcel: Parcel): MultiChoice {
                return MultiChoice(parcel)
            }
        }
    }

    @Serializable
    @SerialName("$CARD_CRAFTER.CT.Notation")
    @Parcelize
    data class Notation(var card: Card, val notationCard: NotationCard) : CT() {
        constructor(parcel: Parcel) : this(
            Card.create(parcel), NotationCard.create(parcel)
        )

        companion object : Parceler<Notation> {
            override fun create(parcel: Parcel): Notation {
                return Notation(parcel)
            }

            override fun Notation.write(parcel: Parcel, flags: Int) {
                card.write(parcel, flags)
                notationCard.write(parcel, flags)
            }
        }
    }

    @Serializable
    @SerialName("$CARD_CRAFTER.CT.Custom")
    @Parcelize
    data class Custom(var card: Card, val customCard: CustomCard) : CT() {
        constructor(parcel: Parcel) : this(
            Card.create(parcel), CustomCard.create(parcel)
        )

        companion object : Parceler<Custom> {
            override fun create(parcel: Parcel): Custom {
                return Custom(parcel)
            }

            override fun Custom.write(parcel: Parcel, flags: Int) {
                card.write(parcel, flags)
                customCard.write(parcel, flags)
            }
        }
    }
}