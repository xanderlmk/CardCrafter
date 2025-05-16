package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ForgotPasswordRepository {
    suspend fun forgotPassword(inputEmail: String): Boolean
}

class ForgotPasswordRepoImpl(
    private val sharedSupabase: SupabaseClient
) : ForgotPasswordRepository {

    companion object {
        private const val FPR = "ForgotPasswordRepository"
    }

    override suspend fun forgotPassword(inputEmail: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                sharedSupabase.auth.resetPasswordForEmail(
                    email = inputEmail, redirectUrl = "app://supabase.com/reset-callback"
                )
                true
            } catch (e: Exception) {
                Log.e(FPR, "$e")
                false
            }
        }
    }
}