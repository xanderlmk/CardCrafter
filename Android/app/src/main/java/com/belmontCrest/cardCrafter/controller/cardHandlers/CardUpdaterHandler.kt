package com.belmontCrest.cardCrafter.controller.cardHandlers

import android.util.Log
import com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels.CardDeckViewModel
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.SavedCard
import java.util.Calendar
import java.util.Date


@Suppress("KotlinConstantConditions")
fun updateCard(
    card: Card, isSuccess: Boolean,
    deckGoodMultiplier: Double,
    deckBadMultiplier: Double, deckReviewAmount: Int,
    again: Boolean
): Card {
    var passes = card.passes
    var totalPasses = card.totalPasses
    var prevSuccess = card.prevSuccess
    var reviewsLeft = card.reviewsLeft
    var nextReview = card.nextReview
    if (card.reviewsLeft <= 1) {
        if (isSuccess) {
            passes += 1
            prevSuccess = true
            reviewsLeft = deckReviewAmount
        } else {
            if (!again) {
                reviewsLeft = deckReviewAmount
            }
            prevSuccess = false
        }
        nextReview = timeCalculator(passes, isSuccess, deckGoodMultiplier, deckBadMultiplier)

        if (!isSuccess && !prevSuccess && passes > 0) {
            passes -= 1
        }
    } else {
        /** When the user reviews a card x amount of times
         *  Default value is 1
         */
        if (isSuccess) reviewsLeft -= 1
    }
    totalPasses += 1
    return Card(
        id = card.id, deckCardNumber = card.deckCardNumber, createdOn = card.createdOn,
        deckId = card.deckId, deckUUID = card.deckUUID, cardIdentifier = card.cardIdentifier,
        passes = passes, totalPasses = totalPasses, partOfList = true, type = card.type,
        reviewsLeft = reviewsLeft, prevSuccess = prevSuccess, nextReview = nextReview,
    )
}

fun timeCalculator(
    passes: Int, isSuccess: Boolean,
    deckGoodMultiplier: Double,
    deckBadMultiplier: Double
): Date {
    val calendar = Calendar.getInstance()
    // Determine the multiplier based on success or hard pass
    val multiplier =
        calculateReviewMultiplier(
            passes, isSuccess,
            deckGoodMultiplier, deckBadMultiplier
        )
    // Calculate days to add
    val daysToAdd = (passes * multiplier).toInt()
    Log.d("CardCrafter","days to add: $daysToAdd")
    // Add days to the current date
    calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)

    // Return the updated date
    return calendar.time
}

private fun calculateReviewMultiplier(
    passes: Int, isSuccess: Boolean,
    deckGoodMultiplier: Double,
    deckBadMultiplier: Double
): Double {
    val baseMultiplier = when {
        passes == 1 -> 1.0
        passes >= 2 -> deckBadMultiplier
        else -> 0.0
    }
    return if (isSuccess) deckGoodMultiplier else baseMultiplier
}

suspend fun handleCardUpdate(
    card: Card, success: Boolean,
    viewModel: CardDeckViewModel,
    deck: Deck, again: Boolean
): Boolean {
    try {
        val new = updateCard(
            card, success, deck.goodMultiplier, deck.badMultiplier, deck.reviewAmount, again
        )
        viewModel.addCardToUpdate(new, card.toSavedCard(), deck)
        return true
    } catch (e: Exception) {
        Log.e("CardCrafter", "Failed to update card: $e")
        return false
    }
}
fun Card.toSavedCard(): SavedCard = SavedCard(
    cardId = this.id,
    createdOn = Date(),
    reviewsLeft = this.reviewsLeft,
    nextReview = this.nextReview,
    prevSuccess = this.prevSuccess,
    passes = this.passes,
    totalPasses = this.totalPasses,
    partOfList = this.partOfList
)
