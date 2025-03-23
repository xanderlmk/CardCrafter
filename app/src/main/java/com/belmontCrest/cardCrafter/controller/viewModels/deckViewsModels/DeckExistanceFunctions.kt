package com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.belmontCrest.cardCrafter.model.repositories.FlashCardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


private const val SQLITE_CONSTRAINT_EXCEPTION = 20

suspend fun checkIfDeckExists(name: String, uuid: String, fCR : FlashCardRepository): Int {
    return withContext(Dispatchers.IO) {
        try {
            fCR.checkIfDeckExists(name, uuid)
        } catch (e: SQLiteConstraintException) {
            Log.d(
                "SupabaseViewModel",
                "Error checking deck existence: ${e.message}"
            )
            SQLITE_CONSTRAINT_EXCEPTION
        }
    }
}

suspend fun checkIfDeckExists(name: String, fCR : FlashCardRepository): Int {
    return withContext(Dispatchers.IO) {
        try {
            fCR.checkIfDeckExists(name)
        } catch (e: SQLiteConstraintException) {
            Log.d(
                "SupabaseViewModel",
                "Error checking deck existence: ${e.message}"
            )
            SQLITE_CONSTRAINT_EXCEPTION
        }
    }
}

suspend fun checkIfDeckUUIDExists(uuid: String, fCR : FlashCardRepository): Int {
    return withContext(Dispatchers.IO) {
        try {
            fCR.checkIfDeckUUIDExists(uuid)
        } catch (e: SQLiteConstraintException) {
            Log.d(
                "SupabaseViewModel",
                "Error checking deck existence: ${e.message}"
            )
            SQLITE_CONSTRAINT_EXCEPTION
        }
    }
}
