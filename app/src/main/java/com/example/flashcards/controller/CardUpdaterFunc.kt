package com.example.flashcards.controller

import com.example.flashcards.controller.viewModels.CardViewModel
import com.example.flashcards.model.tablesAndApplication.Card
import java.util.Calendar
import java.util.Date

fun updateCard(card: Card, isSuccess: Boolean,
               deckGoodMultiplier: Double,
               deckBadMultiplier: Double) : Card {

    val temp = card
    if (isSuccess) {
        temp.passes += 1
        temp.prevSuccess = true
    } else { temp.prevSuccess = false}
    temp.nextReview = timeCalculator(temp.passes, isSuccess,
        deckGoodMultiplier,deckBadMultiplier)
    temp.totalPasses += 1

    if (!isSuccess && !temp.prevSuccess && temp.passes > 0){
        temp.passes -=1
    }
    return temp
}

fun timeCalculator (passes : Int, isSuccess: Boolean,
                    deckGoodMultiplier: Double,
                    deckBadMultiplier : Double) : Date {
    val calendar = Calendar.getInstance()
    // Determine the multiplier based on success or hard pass
    val multiplier =
        calculateReviewMultiplier(passes,isSuccess,
            deckGoodMultiplier, deckBadMultiplier)

    // Calculate days to add
    val daysToAdd = (passes * multiplier).toInt()

    // Add days to the current date
    calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)

    // Return the updated date
    return calendar.time
}

private fun calculateReviewMultiplier(passes: Int, isSuccess: Boolean,
                                      deckGoodMultiplier: Double,
                                      deckBadMultiplier: Double): Double {
    val baseMultiplier = when {
        passes == 1 -> 1.0
        passes >= 2 -> deckBadMultiplier
        else -> 0.0
    }
    return if (isSuccess) deckGoodMultiplier else baseMultiplier
}

// Helper to update the card state
suspend fun handleCardUpdate(card: Card, success: Boolean,
                     viewModel: CardViewModel,
                     deckGoodMultiplier: Double,
                     deckBadMultiplier: Double) : Boolean {
    val updatedCard = updateCard(card, success, deckGoodMultiplier,
        deckBadMultiplier)
    return viewModel.updateCard(updatedCard)
}