package com.belmontCrest.cardCrafter.controller.cardHandlers

import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.CardDeckViewModel
import com.belmontCrest.cardCrafter.model.tablesAndApplication.AllCardTypes
import com.belmontCrest.cardCrafter.model.tablesAndApplication.CT
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Card
import com.belmontCrest.cardCrafter.model.tablesAndApplication.Deck


fun returnCard(ct: CT) : Card{
    return when (ct) {
        is CT.Basic -> {
            ct.card
        }
        is CT.Hint -> {
            ct.card
        }
        is CT.ThreeField -> {
            ct.card
        }
        is CT.MultiChoice->{
            ct.card
        }
        is CT.Notation -> {
            ct.card
        }
    }
}
fun returnCardId(ct : CT) : Int {
    return when (ct) {
        is CT.Basic -> {
            ct.card.id
        }
        is CT.Hint -> {
            ct.card.id
        }
        is CT.ThreeField -> {
            ct.card.id
        }
        is CT.MultiChoice->{
            ct.card.id
        }
        is CT.Notation -> {
            ct.card.id
        }
    }
}
fun updateCTCard(ct: CT, dueCT: CT,
                 deck: Deck, vm: CardDeckViewModel,
                 success : Boolean, again : Boolean) : CT {
    return when (ct) {
        is CT.Basic -> {
            ct.copy(
                card = handleCardUpdate(
                    returnCard(dueCT),
                    success = success,
                    vm,
                    deck.goodMultiplier,
                    deck.badMultiplier,
                    deck.reviewAmount,
                    again = again
                ),
                basicCard = ct.basicCard
            )
        }
        is CT.Hint -> {
            ct.copy(
                card = handleCardUpdate(
                    returnCard(dueCT),
                    success = success,
                    vm,
                    deck.goodMultiplier,
                    deck.badMultiplier,
                    deck.reviewAmount,
                    again = again
                ),
                hintCard = ct.hintCard
            )
        }
        is CT.ThreeField -> {
            ct.copy(
                card = handleCardUpdate(
                    returnCard(dueCT),
                    success = success,
                    vm,
                    deck.goodMultiplier,
                    deck.badMultiplier,
                    deck.reviewAmount,
                    again = again
                ),
                threeFieldCard = ct.threeFieldCard
            )
        }
        is CT.MultiChoice->{
            ct.copy(
                card = handleCardUpdate(
                    returnCard(dueCT),
                    success = success,
                    vm,
                    deck.goodMultiplier,
                    deck.badMultiplier,
                    deck.reviewAmount,
                    again = again
                ),
                multiChoiceCard = ct.multiChoiceCard
            )
        }
        is CT.Notation -> {
            ct.copy(
                card = handleCardUpdate(
                    returnCard(dueCT),
                    success = success,
                    vm,
                    deck.goodMultiplier,
                    deck.badMultiplier,
                    deck.reviewAmount,
                    again = again
                )
            )
        }
    }
}

suspend fun redoACard(ct : CT, cardDeckVM : CardDeckViewModel, index : Int,
                      dueCTs : MutableList<CT>){
    when (ct) {
        /** Even though it seems like getting the new card isn't necessary,
         * it is because we want to replace the savedCard with a new savedCard
         * (the redo card) hence we call this getRedoCard.
         */
        is CT.Basic -> {
            ct.card = cardDeckVM.getRedoCard(ct.card.id)
            cardDeckVM.getRedoCardType(
                ct.card.id,
                index
            ).also {
                dueCTs[index] = ct
            }
        }
        is CT.Hint -> {
            ct.card = cardDeckVM.getRedoCard(ct.card.id)
            cardDeckVM.getRedoCardType(
                ct.card.id,
                index
            ).also {
                dueCTs[index] = ct
            }
        }
        is CT.ThreeField -> {
            ct.card = cardDeckVM.getRedoCard(ct.card.id)
            cardDeckVM.getRedoCardType(
                ct.card.id,
                index
            ).also {
                dueCTs[index] = ct
            }
        }
        is CT.MultiChoice->{
            ct.card = cardDeckVM.getRedoCard(ct.card.id)
            cardDeckVM.getRedoCardType(
                ct.card.id,
                index
            ).also {
                dueCTs[index] = ct
            }
        }
        is CT.Notation -> {
            ct.card = cardDeckVM.getRedoCard(ct.card.id)
            cardDeckVM.getRedoCardType(
                ct.card.id,
                index
            ).also {
                dueCTs[index] = ct
            }
        }
    }
}

fun showReviewsLeft(ct : CT): String{
    return when (ct) {
        is CT.Basic -> {
            ct.card.reviewsLeft.toString()
        }
        is CT.Hint -> {
            ct.card.reviewsLeft.toString()
        }
        is CT.ThreeField -> {
            ct.card.reviewsLeft.toString()
        }
        is CT.MultiChoice->{
            ct.card.reviewsLeft.toString()
        }
        is CT.Notation -> {
            ct.card.reviewsLeft.toString()
        }
    }
}

fun returnReviewsLeft(ct: CT): Int{
    return when (ct) {
        is CT.Basic -> {
            ct.card.reviewsLeft
        }
        is CT.Hint -> {
            ct.card.reviewsLeft
        }
        is CT.ThreeField -> {
            ct.card.reviewsLeft
        }
        is CT.MultiChoice->{
            ct.card.reviewsLeft
        }
        is CT.Notation -> {
            ct.card.reviewsLeft
        }
    }
}

// The mapping function
fun mapAllCardTypesToCTs(allCardTypesList: List<AllCardTypes>): List<CT> {
    return allCardTypesList.map { allCardTypes ->
        when {
            allCardTypes.basicCard != null -> CT.Basic(
                allCardTypes.card,
                allCardTypes.basicCard
            )

            allCardTypes.hintCard != null -> CT.Hint(
                allCardTypes.card,
                allCardTypes.hintCard
            )

            allCardTypes.threeFieldCard != null -> CT.ThreeField(
                allCardTypes.card,
                allCardTypes.threeFieldCard
            )

            allCardTypes.multiChoiceCard != null -> CT.MultiChoice(
                allCardTypes.card,
                allCardTypes.multiChoiceCard
            )
            allCardTypes.notationCard != null -> CT.Notation(
                allCardTypes.card,
                allCardTypes.notationCard
            )
            /** This error will probably only happen when you add a new card. */
            else -> throw IllegalStateException(
                """Mapping error for AllCardTypes element: 
                card=${allCardTypes.card}, 
                basicCard=${allCardTypes.basicCard}, 
                hintCard=${allCardTypes.hintCard},
                threeFieldCard=${allCardTypes.threeFieldCard},
                multiChoiceCard=${allCardTypes.multiChoiceCard},
                notationCard=${allCardTypes.notationCard}""")
        }
    }
}

fun mapACardTypeToCT(cardTypes: AllCardTypes): CT {
    return cardTypes.let {
        return when {
            it.basicCard != null -> CT.Basic(it.card, it.basicCard)
            it.hintCard != null -> CT.Hint(it.card, it.hintCard)
            it.threeFieldCard != null -> CT.ThreeField(it.card, it.threeFieldCard)
            it.multiChoiceCard != null -> CT.MultiChoice(it.card, it.multiChoiceCard)
            it.notationCard != null -> CT.Notation(it.card, it.notationCard)
            /** This error will probably only happen when you add a new card. */
            else -> throw IllegalStateException("Invalid AllCardTypes: all card types are null")
        }
    }
}