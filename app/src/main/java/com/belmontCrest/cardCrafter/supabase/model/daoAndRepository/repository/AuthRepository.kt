package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repository

import android.util.Log
import com.belmontCrest.cardCrafter.BuildConfig
import com.belmontCrest.cardCrafter.supabase.model.GoogleClientResponse
import com.belmontCrest.cardCrafter.supabase.model.OwnerDto
import com.belmontCrest.cardCrafter.supabase.model.UserProfile
import com.belmontCrest.cardCrafter.supabase.model.getSBKey
import com.belmontCrest.cardCrafter.supabase.model.getSBUrl
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface AuthRepository {
    suspend fun createOwner(username: String, fName: String, lName: String): Boolean

    suspend fun getOwner() : OwnerDto?

    suspend fun getGoogleCredentials() : String

    suspend fun signInWithGoogle(
        googleIdToken: String, rawNonce: String
    ): Boolean

    suspend fun getUserProfile() : UserProfile?
}

class AuthRepositoryImpl(
    private val supabase: SupabaseClient
) : AuthRepository {
    override suspend fun createOwner(username: String, fName: String, lName: String): Boolean {
        return withContext(Dispatchers.IO) {
            val user = supabase.auth.currentUserOrNull()
            if (user == null) {
                return@withContext false
            }
            try {
                supabase.from("owner")
                    .insert(
                        OwnerDto(user.id, username, fName, lName)
                    )
                return@withContext true
            } catch (e: Exception) {
                Log.d("SupabaseViewModel", "$e")
                return@withContext false
            }
        }
    }

    override suspend fun getOwner() : OwnerDto? {
        return withContext(Dispatchers.IO) {
            val user = supabase.auth.currentUserOrNull()
            if (user == null) {
                return@withContext null
            }
            val owner = supabase.from(sbOwnerTN)
                .select(Columns.ALL) {
                    filter {
                        eq("user_id", user.id)
                    }
                }.decodeSingleOrNull<OwnerDto>()
            owner
        }
    }

    override suspend fun getGoogleCredentials(): String {
        return withContext(Dispatchers.IO) {
            try {
                val response: HttpResponse =
                    supabase.httpClient.post("${getSBUrl()}/$POST_FUNCTION_STRING") {
                        header(HttpHeaders.Authorization, "Bearer ${getSBKey()}")
                        header(HttpHeaders.ContentType, ContentType.Application.Json)
                    }
                if (response.status == HttpStatusCode.OK) {
                    val googleResponse = response.body<GoogleClientResponse>()
                        googleResponse.google_id
                } else {
                    Log.e("Error", "Unexpected response: ${response.status}")
                    ""
                }
            } catch (e: Exception) {
                Log.e("Error", "Network call failed", e)
                ""
            }
        }
    }

    override suspend fun signInWithGoogle(googleIdToken: String, rawNonce: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                supabase.auth.signInWith(IDToken) {
                    idToken = googleIdToken
                    provider = Google
                    nonce = rawNonce
                }
                true
            } catch (e: Exception) {
                Log.e("AuthRepo", "Failed to sign in with google: $e")
                false
            }
        }
    }

    override suspend fun getUserProfile(): UserProfile? {
        return withContext(Dispatchers.IO) {
            try {
                val user = supabase.auth.currentUserOrNull()

                if (user == null) {
                    return@withContext null
                }

                val owner = supabase.from(sbOwnerTN)
                    .select(Columns.ALL) {
                        filter {
                            eq("user_id", user.id)
                        }
                    }.decodeSingleOrNull<OwnerDto>()
                UserProfile(user, owner)
            } catch (e : Exception) {
                Log.e("AuthRepo", "Something went wrong: $e")
                null
            }
        }
    }
}

private const val sbOwnerTN = BuildConfig.SB_OWNER_TN
private const val POST_FUNCTION_STRING = "functions/v1/getKeys"
