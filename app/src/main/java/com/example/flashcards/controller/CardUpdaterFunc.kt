package com.example.flashcards.controller

import com.example.flashcards.controller.viewModels.CardViewModel
import com.example.flashcards.model.tablesAndApplication.Card
import java.util.Calendar
import java.util.Date

fun updateCard(card: Card, isSuccess: Boolean) : Card {

    val temp = card
    if (isSuccess) {
        temp.passes += 1
        temp.prevSuccess = true
    } else { temp.prevSuccess = false}
    temp.nextReview = timeCalculator(temp.passes, isSuccess)
    temp.totalPasses += 1

    if (!isSuccess && !temp.prevSuccess && temp.passes > 0){
        temp.passes -=1
    }
    return temp
}

fun timeCalculator (passes : Int, isSuccess: Boolean) : Date {
    val calendar = Calendar.getInstance()
    // Determine the multiplier based on success or hard pass
    val multiplier = when {
        passes == 1 -> if (isSuccess) 1.5 else 1.0 // passes == 1
        passes >= 2 -> if (isSuccess) 1.5 else 0.5
        else -> if (isSuccess) 1.5 else 0.0
    }

    // Calculate days to add
    val daysToAdd = (passes * multiplier).toInt()

    // Add days to the current date
    calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)

    // Return the updated date
    return calendar.time
}

// Helper to update the card state
fun handleCardUpdate(card: Card, success: Boolean, viewModel: CardViewModel) {
    val updatedCard = updateCard(card, success)
    viewModel.updateCard(updatedCard)
}