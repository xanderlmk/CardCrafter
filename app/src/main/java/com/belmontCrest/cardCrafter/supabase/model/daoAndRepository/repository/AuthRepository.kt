package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repository

import android.content.Intent
import android.util.Log
import com.belmontCrest.cardCrafter.BuildConfig
import com.belmontCrest.cardCrafter.supabase.model.GoogleClientResponse
import com.belmontCrest.cardCrafter.supabase.model.GoogleCredentials
import com.belmontCrest.cardCrafter.supabase.model.tables.OwnerDto
import com.belmontCrest.cardCrafter.supabase.model.tables.UserProfile
import com.belmontCrest.cardCrafter.supabase.model.getSBKey
import com.belmontCrest.cardCrafter.supabase.model.getSBUrl
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.UnauthorizedRestException
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.exception.AuthWeakPasswordException
import io.github.jan.supabase.gotrue.handleDeeplinks
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
                supabase.from(sbOwnerTN)
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

    override suspend fun getOwner(): OwnerDto? {
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

    override suspend fun getGoogleCredentials(): GoogleCredentials {
        return withContext(Dispatchers.IO) {
            try {
                val response: HttpResponse =
                    supabase.httpClient.post("${getSBUrl()}/$POST_FUNCTION_STRING") {
                        header(HttpHeaders.Authorization, "Bearer ${getSBKey()}")
                        header(HttpHeaders.ContentType, ContentType.Application.Json)
                    }
                if (response.status == HttpStatusCode.OK) {
                    val googleResponse = response.body<GoogleClientResponse>()
                    GoogleCredentials.Success(googleResponse.google_id)
                } else {
                    Log.e("Error", "Unexpected response: ${response.status}")
                    GoogleCredentials.Failure("Unexpected response")
                }
            } catch (e: Exception) {
                Log.e("Error", "Network call failed", e)
                GoogleCredentials.Failure("Network Error: ${e.message ?: "Unknown Error"}")
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

    override suspend fun signUpWithEmail(inputEmail: String, inputPassword: String): String {
        return withContext(Dispatchers.IO) {
            try {
                supabase.auth.signUpWith(
                    provider = Email,
                    redirectUrl = "app://supabase.com"
                ) {
                    email = inputEmail
                    password = inputPassword
                }
                "yay"
            } catch (e: Exception) {
                when (e) {
                    is AuthWeakPasswordException -> {
                        e.message ?: "weak password"
                    }

                    is HttpRequestTimeoutException -> {
                        "timeout"
                    }

                    is HttpRequestException -> {
                        "network"
                    }

                    else -> {
                        "unknown"
                    }
                }
            }
        }
    }

    override suspend fun deepLinker(
        intent: Intent,
        callback: (String, String) -> Unit
    ): String {
        return withContext(Dispatchers.IO) {
            try {
                supabase.handleDeeplinks(
                    intent = intent,
                    onSessionSuccess = { session ->
                        Log.d("LOGIN", "Log in successfully with user info: ${session.user}")
                        session.user?.apply {
                            callback(email ?: "", createdAt.toString())
                        }
                    }
                )
                "yay"
            } catch (e: Exception) {
                when (e) {
                    is HttpRequestTimeoutException -> {
                        "timeout"
                    }

                    is HttpRequestException -> {
                        "network"
                    }

                    else -> {
                        "unknown"
                    }
                }
            }
        }
    }

    override suspend fun signInWithEmail(inputEmail: String, inputPassword: String): String {
        return withContext(Dispatchers.IO) {
            try {
                supabase.auth.signInWith(Email) {
                    email = inputEmail
                    password = inputPassword
                }
                "yay"
            } catch (e: Exception) {
                when (e) {
                    is UnauthorizedRestException -> {
                        "incorrect credentials"
                    }

                    is HttpRequestTimeoutException -> {
                        "timeout"
                    }

                    is HttpRequestException -> {
                        "network"
                    }

                    else -> {
                        "unknown"
                    }
                }
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
            } catch (e: Exception) {
                Log.e("AuthRepo", "Something went wrong: $e")
                null
            }
        }
    }

    override suspend fun signOut(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                supabase.auth.signOut()
                true
            } catch (e: Exception) {
                Log.e("SupabaseVM", "Couldn't sign out: $e")
                false
            }
        }
    }
}

private const val sbOwnerTN = BuildConfig.SB_OWNER_TN
private const val POST_FUNCTION_STRING = "functions/v1/getKeys"
