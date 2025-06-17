package com.belmontCrest.cardCrafter.controller.viewModels

import com.belmontCrest.cardCrafter.localDatabase.dbInterface.repositories.FlashCardRepository
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.Date

class ReusedFunc(private val flashCardRepository: FlashCardRepository) {
    companion object {
        private const val TIMEOUT_MILLIS = 4_000L
    }
    suspend fun updateCardsLeft(deck: Deck, cardsToAdd: Int) {
        return withContext(Dispatchers.IO) {
            /** Only add the cards if the deck's review is due */
            if (deck.nextReview <= Date()) {
                withTimeout(TIMEOUT_MILLIS) {
                    /** This keeps a record of the amount of cards left vs done and compares
                     *  it to the card amount. Here's three examples
                     *  cardsLeft = CL, cardsDone = CD, cardAmount = CA
                     *  1 represents how many cardsToAdd
                     *  CA = 20
                     *  1. CL = 19, CD = 1
                     *      (CL + 1 = 20) < CA && (CD + 1 = 2) < CA -> False,
                     *      (CD + 1 = 2) >= CA -> False; just update to CA
                     *
                     *  2. CL = 1, CD = 19
                     *      (CL + 1 = 2) && (CD + 1 = 20) < CA -> F; move down
                     *      (CD + 1 = 20) >= CA -> True, Don't update just return.
                     *
                     *  3. CL = 15, CD = 5
                     *      (CL + 1 = 16) ** (CD + 1 = 6) < CA -> True; Update accordingly.
                     */
                    if (((deck.cardsLeft + cardsToAdd) < deck.cardAmount) &&
                        ((deck.cardsDone + cardsToAdd) < deck.cardAmount)
                    ) {
                        flashCardRepository.updateCardsLeft(
                            deckId = deck.id, cardsDone = deck.cardsDone,
                            cardsLeft = (deck.cardsLeft + cardsToAdd),
                        )
                    } else if ((deck.cardsDone + cardsToAdd) >= deck.cardAmount) {
                        return@withTimeout
                    } else {
                        flashCardRepository.updateCardsLeft(
                            deck.id, deck.cardAmount, deck.cardsDone
                        )
                    }
                }
            }
        }
    }
}