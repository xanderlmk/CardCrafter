package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo

import com.belmontCrest.cardCrafter.supabase.model.createSupabase.GoogleCredentials
import com.belmontCrest.cardCrafter.supabase.model.tables.OwnerDto
import com.belmontCrest.cardCrafter.supabase.model.tables.UserProfile
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {

    val googleClientId: StateFlow<String>

    fun updateGoogleId(s: String)
    fun getCurrentUser()

    suspend fun closeSupabase() : Boolean

    fun reCreateSupabase(): Boolean

    suspend fun createOwner(username: String, fName: String, lName: String): Boolean

    suspend fun getOwner()

    suspend fun getGoogleCredentials(): GoogleCredentials

    suspend fun signInWithGoogle(
        googleIdToken: String, rawNonce: String
    ): Boolean

    suspend fun signUpWithEmail(inputEmail: String, inputPassword: String): String

    suspend fun signInWithEmail(inputEmail: String, inputPassword: String): String

    suspend fun getUserProfile(): UserProfile?

    suspend fun signOut(): Boolean

    val owner: StateFlow<OwnerDto?>
    val currentUser: StateFlow<UserInfo?>
}