package com.belmontCrest.cardCrafter.controller.cardHandlers

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import com.belmontCrest.cardCrafter.model.uiModels.CardUpdateError
import kotlinx.coroutines.TimeoutCancellationException
import java.io.IOException


fun returnError(e : Exception) : CardUpdateError {
    return when (e) {
        is IOException -> CardUpdateError.NetworkError(e)
        is SQLiteConstraintException -> CardUpdateError.ConstraintError(e)
        is SQLiteException -> CardUpdateError.DatabaseError(e)
        is TimeoutCancellationException -> CardUpdateError.TimeoutError(e)
        else -> CardUpdateError.UnknownError(e)
    }
}
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