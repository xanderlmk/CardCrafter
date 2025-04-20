package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories

import android.content.Intent
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.GoogleCredentials
import com.belmontCrest.cardCrafter.supabase.model.tables.OwnerDto
import com.belmontCrest.cardCrafter.supabase.model.tables.UserProfile

interface AuthRepository {
    suspend fun createOwner(username: String, fName: String, lName: String): Boolean

    suspend fun getOwner(): OwnerDto?

    suspend fun getGoogleCredentials(): GoogleCredentials

    suspend fun signInWithGoogle(
        googleIdToken: String, rawNonce: String
    ): Boolean

    suspend fun signUpWithEmail(inputEmail: String, inputPassword: String): String

    suspend fun deepLinker(intent: Intent, callback: (String, String) -> Unit): String

    suspend fun signInWithEmail(inputEmail: String, inputPassword: String): String

    suspend fun getUserProfile(): UserProfile?

    suspend fun signOut(): Boolean

    suspend fun signInSyncedDBUser(): String
}
