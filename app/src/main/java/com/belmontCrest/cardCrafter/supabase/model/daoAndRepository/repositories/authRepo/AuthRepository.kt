package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo

import com.belmontCrest.cardCrafter.supabase.model.MergedUserInfo
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.GoogleCredentials
import com.belmontCrest.cardCrafter.supabase.model.tables.OwnerDto
import com.belmontCrest.cardCrafter.supabase.model.tables.UserProfile

interface AuthRepository {

    fun getCurrentUser() : MergedUserInfo?

    suspend fun closeSupabase() : Boolean

    fun reCreateSupabase(): Boolean

    suspend fun createOwner(username: String, fName: String, lName: String): Boolean

    suspend fun getOwner(): OwnerDto?

    suspend fun getGoogleCredentials(): GoogleCredentials

    suspend fun signInWithGoogle(
        googleIdToken: String, rawNonce: String
    ): Boolean

    suspend fun signUpWithEmail(inputEmail: String, inputPassword: String): String

    suspend fun signInWithEmail(inputEmail: String, inputPassword: String): String

    suspend fun getUserProfile(): UserProfile?

    suspend fun signOut(): Boolean

    suspend fun signInSyncedDBUser(): String

    suspend fun forgotPassword(inputEmail: String): Boolean
}