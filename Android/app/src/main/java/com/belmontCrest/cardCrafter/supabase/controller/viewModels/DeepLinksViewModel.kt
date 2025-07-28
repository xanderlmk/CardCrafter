package com.belmontCrest.cardCrafter.supabase.controller.viewModels

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.belmontCrest.cardCrafter.supabase.model.AuthRepoVals
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo.DeepLinkerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeepLinksViewModel(
    private val deepLinkerRepo: DeepLinkerRepository
) : ViewModel() {
    suspend fun deepLinker(intent: Intent, callback: (String, String) -> Unit): String {
        return withContext(Dispatchers.IO) {
            deepLinkerRepo.deepLinker(intent) { email, createdAt ->
                callback(email, createdAt)
            }
        }
    }

    suspend fun resetPassword(password: String): Boolean {
        return withContext(Dispatchers.IO) {
            deepLinkerRepo.resetPassword(password) == AuthRepoVals.SUCCESS
        }
    }
}