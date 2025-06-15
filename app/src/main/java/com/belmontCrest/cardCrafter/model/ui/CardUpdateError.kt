package com.belmontCrest.cardCrafter.model.ui

import android.util.Log

sealed class CardUpdateError(val exception: Exception) : Exception(exception) {
    class NetworkError(exception: Exception) : CardUpdateError(exception)
    {
        fun networkError(){
            Log.d("CardUpdateError", "Network Error: $exception")
        }
    }
    class DatabaseError(exception: Exception) : CardUpdateError(exception)
    {
        fun databaseError(){
            Log.d("CardUpdateError", "Database Error: $exception")
        }
    }
    class TimeoutError(exception: Exception) : CardUpdateError(exception)
    {
        fun timeoutError(){
            Log.d("CardUpdateError", "Timeout Error: $exception")
        }
    }
    class ConstraintError(exception: Exception) : CardUpdateError(exception){
        fun constraintError(){
            Log.d("CardUpdateError", "Constraint Error: $exception")
        }
    }
    class IllegalStateError(exception: Exception) : CardUpdateError(exception) {
        fun illegalStateError() {
            Log.d("CardUpdateError", "Illegal State Error: $exception")
        }
    }
    class UnknownError(exception: Exception) : CardUpdateError(exception)
    {
        fun unknownError(){
            Log.d("CardUpdateError", "Unknown Error: $exception")
        }
    }
}