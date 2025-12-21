package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo

import android.util.Log
import com.belmontCrest.cardCrafter.supabase.model.AuthRepoVals
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.GoogleClientResponse
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.GoogleCredentials
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.createSharedSupabase
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.getSBKey
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.getSBUrl
import com.belmontCrest.cardCrafter.supabase.model.tables.OwnerDto
import com.belmontCrest.cardCrafter.supabase.model.tables.UserProfile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.UnauthorizedRestException
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.exception.AuthWeakPasswordException
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(private var supabase: SupabaseClient) : AuthRepository {
    companion object {
        private val VS = AuthRepoVals
    }

    private var _googleClientId = MutableStateFlow("")
    override val googleClientId = _googleClientId.asStateFlow()

    private val _currentUser = MutableStateFlow(supabase.auth.currentUserOrNull())
    override val currentUser = _currentUser.asStateFlow()


    private val _ownerDto: MutableStateFlow<OwnerDto?> = MutableStateFlow(null)
    override val owner = _ownerDto.asStateFlow()

    override fun updateGoogleId(s: String) = _googleClientId.update { s }
    override fun getCurrentUser() = _currentUser.update { supabase.auth.currentUserOrNull() }

    override suspend fun closeSupabase(): Boolean {
        try {
            supabase.auth.close()
            return true
        } catch (e: Exception) {
            Log.d(VS.AUTH_REPO, "$e")
            return false
        }
    }

    override fun reCreateSupabase(): Boolean {
        try {
            supabase = createSharedSupabase(getSBUrl(), getSBKey())
            return true
        } catch (e: Exception) {
            Log.d(VS.AUTH_REPO, "$e")
            return false
        }
    }

    override suspend fun createOwner(username: String, fName: String, lName: String) =
        withContext(Dispatchers.IO) {
            val user = supabase.auth.currentUserOrNull() ?: return@withContext false
            try {
                supabase.from(VS.SB_OWNER_TN)
                    .insert(OwnerDto(user.id, username, fName, lName))
                return@withContext true
            } catch (e: Exception) {
                Log.d(VS.AUTH_REPO, "$e")
                return@withContext false
            }
        }

    override suspend fun getOwner() = withContext(Dispatchers.IO) {
        val user = supabase.auth.currentUserOrNull() ?: return@withContext
        val owner = supabase.from(VS.SB_OWNER_TN)
            .select(Columns.Companion.ALL) {
                filter {
                    eq("user_id", user.id)
                }
            }.decodeSingleOrNull<OwnerDto>()
        _ownerDto.update { owner }
    }

    override suspend fun getGoogleCredentials() = withContext(Dispatchers.IO) {
        try {
            val response: HttpResponse =
                supabase.httpClient.post("${getSBUrl()}/${VS.POST_FUNCTION_STRING}") {
                    header(HttpHeaders.Authorization, "Bearer ${getSBKey()}")
                    header(HttpHeaders.ContentType, ContentType.Application.Json)
                }
            if (response.status == HttpStatusCode.Companion.OK) {
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

    override suspend fun signUpWithEmail(inputEmail: String, inputPassword: String) =
        withContext(Dispatchers.IO) {
            try {
                supabase.auth.signUpWith(
                    provider = Email,
                    redirectUrl = "app://supabase.com/auth-callback"
                ) {
                    email = inputEmail
                    password = inputPassword
                }
                VS.SUCCESS
            } catch (e: Exception) {
                when (e) {
                    is AuthWeakPasswordException -> {
                        e.message ?: "weak password"
                    }

                    is HttpRequestTimeoutException -> {
                        VS.TIMEOUT
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

    override suspend fun signInWithEmail(inputEmail: String, inputPassword: String) =
        withContext(Dispatchers.IO) {
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
                        Log.e("Auth Repository", "$e")
                        "unknown"
                    }
                }
            }
        }


    override suspend fun getUserProfile(): UserProfile? = withContext(Dispatchers.IO) {
        try {
            val user = supabase.auth.currentUserOrNull() ?: return@withContext null

            val owner = supabase.from(VS.SB_OWNER_TN)
                .select(Columns.ALL) {
                    filter {
                        eq("user_id", user.id)
                    }
                }.decodeSingleOrNull<OwnerDto>()
            UserProfile(user, owner)
        } catch (e: Exception) {
            Log.e(VS.AUTH_REPO, "Something went wrong: $e")
            null
        }
    }

    override suspend fun signOut(): Boolean = withContext(Dispatchers.IO) {
        try {
            supabase.auth.signOut()
            true
        } catch (e: Exception) {
            Log.e(VS.AUTH_REPO, "Couldn't sign out: $e")
            false
        }
    }
}