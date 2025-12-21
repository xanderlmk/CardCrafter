package com.belmontCrest.cardCrafter.supabase.controller.view.models

import androidx.lifecycle.ViewModel
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo.ForgotPasswordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ForgotPasswordViewModel(
    private val fpRepository: ForgotPasswordRepository
) : ViewModel() {
    suspend fun forgotPassword(email: String): Boolean {
        return withContext(Dispatchers.IO) { fpRepository.forgotPassword(email) }
    }
}