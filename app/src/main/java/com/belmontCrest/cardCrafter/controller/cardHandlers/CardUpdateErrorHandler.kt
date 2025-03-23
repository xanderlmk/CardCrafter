package com.belmontCrest.cardCrafter.controller.cardHandlers

import com.belmontCrest.cardCrafter.model.uiModels.CardUpdateError


fun callError(cardUE : CardUpdateError) {
    when (cardUE) {
        is CardUpdateError.NetworkError -> {
            cardUE.networkError()
        }
        is CardUpdateError.ConstraintError -> {
            cardUE.constraintError()
        }
        is CardUpdateError.DatabaseError -> {
            cardUE.databaseError()
        }
        is CardUpdateError.TimeoutError -> {
            cardUE.timeoutError()
        }
        is CardUpdateError.IllegalStateError -> {
            cardUE.illegalStateError()
        }
        is CardUpdateError.UnknownError -> {
            cardUE.unknownError()
        }
    }
}