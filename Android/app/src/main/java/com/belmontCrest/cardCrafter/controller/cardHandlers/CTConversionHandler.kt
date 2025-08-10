package com.belmontCrest.cardCrafter.controller.cardHandlers

import com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels.CardDeckViewModel
import com.belmontCrest.cardCrafter.localDatabase.tables.AllCardTypes
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.AnswerParam
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.CardRemains
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.MiddleParam
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.Param
import com.belmontCrest.cardCrafter.localDatabase.tables.PartOfQorA
import com.belmontCrest.cardCrafter.localDatabase.tables.SavedCard
import com.belmontCrest.cardCrafter.localDatabase.tables.toCustomCard
import com.belmontCrest.cardCrafter.model.ui.states.CDetails


fun CT.toCard(): Card = when (this) {
    is CT.Basic -> card
    is CT.Hint -> card
    is CT.ThreeField -> card
    is CT.MultiChoice -> card
    is CT.Notation -> card
    is CT.Custom -> card
}

fun CT.getCardType(): String = when (this) {
    is CT.Basic -> card.type
    is CT.Hint -> card.type
    is CT.ThreeField -> card.type
    is CT.MultiChoice -> card.type
    is CT.Notation -> card.type
    is CT.Custom -> card.type
}

fun CT.getCardId(): Int = when (this) {
    is CT.Basic -> card.id
    is CT.Hint -> card.id
    is CT.ThreeField -> card.id
    is CT.MultiChoice -> card.id
    is CT.Notation -> card.id
    is CT.Custom -> card.id
}

fun List<CT>.toCardList(): List<Card> {
    return this.map {
        it.toCard()
    }
}

fun List<CT>.toBasicList() = filterIsInstance<CT.Basic>().map { it.basicCard }
fun List<CT>.toHintList() = filterIsInstance<CT.Hint>().map { it.hintCard }
fun List<CT>.toThreeFieldList() = filterIsInstance<CT.ThreeField>().map { it.threeFieldCard }
fun List<CT>.toMultiChoiceList() = filterIsInstance<CT.MultiChoice>().map { it.multiChoiceCard }
fun List<CT>.toNotationList() = filterIsInstance<CT.Notation>().map { it.notationCard }
fun List<CT>.toCustomList() = filterIsInstance<CT.Custom>().map { it.customCard }

fun CT.Basic.question(): String = this.basicCard.question
fun CT.Basic.answer(): String = this.basicCard.answer

fun CT.Hint.question(): String = this.hintCard.question
fun CT.Hint.hint(): String = this.hintCard.hint
fun CT.Hint.answer(): String = this.hintCard.answer

fun CT.ThreeField.question(): String = this.threeFieldCard.question
fun CT.ThreeField.middle(): String = this.threeFieldCard.middle
fun CT.ThreeField.field(): PartOfQorA = this.threeFieldCard.field
fun CT.ThreeField.answer(): String = this.threeFieldCard.answer

fun CT.MultiChoice.question(): String = this.multiChoiceCard.question
fun CT.MultiChoice.choices(): List<String> = listOf(
    this.multiChoiceCard.choiceA,
    this.multiChoiceCard.choiceB,
    this.multiChoiceCard.choiceC,
    this.multiChoiceCard.choiceD
)

fun CT.MultiChoice.correct(): Char = this.multiChoiceCard.correct

fun CT.Notation.question(): String = this.notationCard.question
fun CT.Notation.steps(): List<String> = this.notationCard.steps
fun CT.Notation.answer(): String = this.notationCard.answer

fun CT.Custom.question(): Param = this.customCard.question
fun CT.Custom.middle(): MiddleParam = this.customCard.middle
fun CT.Custom.answer(): AnswerParam = this.customCard.answer

fun CT.toCDetails(): CDetails = when (this) {
    is CT.Basic -> CDetails(question = this.question(), answer = this.answer())
    is CT.Hint -> CDetails(question = this.question(), middle = this.hint(), answer = this.answer())
    is CT.MultiChoice -> CDetails(
        question = this.question(), choices = this.choices(), correct = this.correct()
    )

    is CT.Notation -> CDetails(
        question = this.question(), steps = this.steps(), answer = this.answer()
    )

    is CT.ThreeField -> CDetails(
        question = this.question(), middle = this.middle(),
        answer = this.answer(), isQOrA = this.field()
    )

    is CT.Custom -> CDetails(
        customQuestion = this.question(), customMiddle = this.middle(), customAnswer = this.answer()
    )
}

suspend fun updateCTCard(
    ct: CT, deck: Deck, vm: CardDeckViewModel, success: Boolean, again: Boolean
) = handleCardUpdate(
    ct.toCard(), success = success, vm, deck, again = again
)


fun showReviewsLeft(ct: CT): String {
    return when (ct) {
        is CT.Basic -> ct.card.reviewsLeft.toString()
        is CT.Hint -> ct.card.reviewsLeft.toString()
        is CT.ThreeField -> ct.card.reviewsLeft.toString()
        is CT.MultiChoice -> ct.card.reviewsLeft.toString()
        is CT.Notation -> ct.card.reviewsLeft.toString()
        is CT.Custom -> ct.card.reviewsLeft.toString()
    }
}

fun returnReviewsLeft(ct: CT): Int {
    return when (ct) {
        is CT.Basic -> ct.card.reviewsLeft
        is CT.Hint -> ct.card.reviewsLeft
        is CT.ThreeField -> ct.card.reviewsLeft
        is CT.MultiChoice -> ct.card.reviewsLeft
        is CT.Notation -> ct.card.reviewsLeft
        is CT.Custom -> ct.card.reviewsLeft
    }
}

fun List<AllCardTypes>.toCTList() = this.map { allCardTypes ->
    when {
        allCardTypes.basicCard != null -> CT.Basic(
            allCardTypes.card, allCardTypes.basicCard
        )

        allCardTypes.hintCard != null -> CT.Hint(
            allCardTypes.card, allCardTypes.hintCard
        )

        allCardTypes.threeFieldCard != null -> CT.ThreeField(
            allCardTypes.card, allCardTypes.threeFieldCard
        )

        allCardTypes.multiChoiceCard != null -> CT.MultiChoice(
            allCardTypes.card, allCardTypes.multiChoiceCard
        )

        allCardTypes.notationCard != null -> CT.Notation(
            allCardTypes.card, allCardTypes.notationCard
        )

        allCardTypes.nullableCustomCard != null -> CT.Custom(
            allCardTypes.card, allCardTypes.nullableCustomCard.toCustomCard()
        )
        /** This error will probably only happen when you add a new card. */
        else -> throw IllegalStateException(
            """Mapping error for AllCardTypes element: 
                card=${allCardTypes.card}, 
                basicCard=${allCardTypes.basicCard}, 
                hintCard=${allCardTypes.hintCard},
                threeFieldCard=${allCardTypes.threeFieldCard},
                multiChoiceCard=${allCardTypes.multiChoiceCard},
                notationCard=${allCardTypes.notationCard},
                nullableCustomCard=${allCardTypes.nullableCustomCard}"""
        )
    }
}

fun AllCardTypes.toCT(): CT = when {
    this.basicCard != null -> CT.Basic(this.card, this.basicCard)
    this.hintCard != null -> CT.Hint(this.card, this.hintCard)
    this.threeFieldCard != null -> CT.ThreeField(this.card, this.threeFieldCard)
    this.multiChoiceCard != null -> CT.MultiChoice(this.card, this.multiChoiceCard)
    this.notationCard != null -> CT.Notation(this.card, this.notationCard)
    this.nullableCustomCard != null -> CT.Custom(this.card, this.nullableCustomCard.toCustomCard())
    /** This error will probably only happen when you add a new card. */
    else -> throw IllegalStateException("Invalid AllCardTypes: all card types are null")
}


fun SavedCard.toCard(cr: CardRemains): Card =
    Card(
        id = this.cardId, deckId = cr.deckId, deckUUID = cr.deckUUID,
        reviewsLeft = this.reviewsLeft, nextReview = this.nextReview, passes = this.passes,
        totalPasses = this.totalPasses, partOfList = partOfList, createdOn = cr.createdOn,
        prevSuccess = this.prevSuccess, type = cr.type, deckCardNumber = cr.deckCardNumber,
        cardIdentifier = cr.cardIdentifier
    )