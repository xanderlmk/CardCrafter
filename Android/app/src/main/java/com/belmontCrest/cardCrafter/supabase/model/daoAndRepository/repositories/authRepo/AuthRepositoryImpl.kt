package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo

import android.util.Log
import com.belmontCrest.cardCrafter.localDatabase.tables.Encryption
import com.belmontCrest.cardCrafter.localDatabase.tables.Pwd
import com.belmontCrest.cardCrafter.supabase.model.AuthRepoVals
import com.belmontCrest.cardCrafter.supabase.model.MergedUserInfo
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.GoogleClientResponse
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.GoogleCredentials
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.createSharedSupabase
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.createSyncedSupabase
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.getSharedSBKey
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.getSharedSBUrl
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.getSyncedSBUrl
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.daos.PwdDao
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
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class AuthRepositoryImpl(
    private var sharedSupabase: SupabaseClient,
    private var syncedSupabase: SupabaseClient,
    private val pwdDao: PwdDao
) : AuthRepository {
    companion object {
        private val VS = AuthRepoVals
    }

    override fun getCurrentUser(): MergedUserInfo? {
        val syncedInfo = syncedSupabase.auth.currentUserOrNull()
        val sharedInfo = sharedSupabase.auth.currentUserOrNull()

        return if (syncedInfo == null || sharedInfo == null) {
            null
        } else {
            MergedUserInfo(syncedInfo = syncedInfo, sharedInfo = sharedInfo)
        }
    }

    override suspend fun closeSupabase(): Boolean {
        try {
            sharedSupabase.auth.close()
            syncedSupabase.auth.close()
            return true
        } catch (e: Exception) {
            Log.d(VS.AUTH_REPO, "$e")
            return false
        }
    }

    override fun reCreateSupabase(): Boolean {
        try {
            sharedSupabase = createSharedSupabase(getSharedSBUrl(), getSharedSBKey())
            syncedSupabase = createSyncedSupabase(getSyncedSBUrl(), getSharedSBKey())
            return true
        } catch (e: Exception) {
            Log.d(VS.AUTH_REPO, "$e")
            return false
        }
    }

    override suspend fun createOwner(username: String, fName: String, lName: String): Boolean {
        return withContext(Dispatchers.IO) {
            val user = sharedSupabase.auth.currentUserOrNull() ?: return@withContext false
            try {
                sharedSupabase.from(VS.SB_OWNER_TN)
                    .insert(OwnerDto(user.id, username, fName, lName))
                return@withContext true
            } catch (e: Exception) {
                Log.d(VS.AUTH_REPO, "$e")
                return@withContext false
            }
        }
    }

    override suspend fun getOwner(): OwnerDto? {
        return withContext(Dispatchers.IO) {
            val user = sharedSupabase.auth.currentUserOrNull() ?: return@withContext null
            val owner = sharedSupabase.from(VS.SB_OWNER_TN)
                .select(Columns.Companion.ALL) {
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
                    sharedSupabase.httpClient.post("${getSharedSBUrl()}/${VS.POST_FUNCTION_STRING}") {
                        header(HttpHeaders.Authorization, "Bearer ${getSharedSBKey()}")
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
    }

    override suspend fun signInWithGoogle(googleIdToken: String, rawNonce: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                sharedSupabase.auth.signInWith(IDToken) {
                    idToken = googleIdToken
                    provider = Google
                    nonce = rawNonce
                }
                syncedSupabase.auth.signInWith(IDToken) {
                    idToken = googleIdToken
                    provider = Google
                    nonce = rawNonce
                }
                true
            } catch (e: Exception) {
                signOutIssue()
                Log.e("AuthRepo", "Failed to sign in with google: $e")
                false
            }
        }
    }

    override suspend fun signUpWithEmail(inputEmail: String, inputPassword: String): String {
        return withContext(Dispatchers.IO) {
            try {
                sharedSupabase.auth.signUpWith(
                    provider = Email,
                    redirectUrl = "app://supabase.com/auth-callback"
                ) {
                    email = inputEmail
                    password = inputPassword
                }
                pwdDao.insertPwd(Pwd(password = Encryption(inputPassword)))
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
    }

    override suspend fun signInWithEmail(inputEmail: String, inputPassword: String): String {
        return withContext(Dispatchers.IO) {
            try {
                sharedSupabase.auth.signInWith(Email) {
                    email = inputEmail
                    password = inputPassword
                }
                syncedSupabase.auth.signInWith(Email)
                {
                    email = inputEmail
                    password = inputPassword
                }
                "yay"
            } catch (e: Exception) {
                signOutIssue()
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
    }


    override suspend fun getUserProfile(): UserProfile? {
        return withContext(Dispatchers.IO) {
            try {
                val user = sharedSupabase.auth.currentUserOrNull() ?: return@withContext null

                val owner = sharedSupabase.from(VS.SB_OWNER_TN)
                    .select(Columns.Companion.ALL) {
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
    }

    override suspend fun signOut(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                sharedSupabase.auth.signOut()
                syncedSupabase.auth.signOut()
                true
            } catch (e: Exception) {
                Log.e(VS.AUTH_REPO, "Couldn't sign out: $e")
                false
            }
        }
    }

    override suspend fun signInSyncedDBUser(): String {
        return withContext(Dispatchers.IO) {
            try {
                val pwd = pwdDao.getPwd()
                // Get the current session after login
                val user = sharedSupabase.auth.currentUserOrNull()
                val userId = syncedSupabase.auth.currentUserOrNull()?.id
                // if the user already exist no need to do anything.
                if (syncedSupabase.auth.currentUserOrNull() != null) {
                    Log.d(VS.AUTH_REPO, "Synced is fine.")
                    deletePwd(pwd)
                    return@withContext "Sign in to synced DB."
                }
                val issuer = Json.Default.decodeFromJsonElement(
                    String.Companion.serializer(),
                    user?.userMetadata?.get("iss") ?: Json.Default.encodeToJsonElement(
                        String.serializer(),
                        "empty"
                    )
                )
                // if the user already exist with google it's okay, but don't sign in.
                if (issuer == VS.GOOGLE_ISSUER) {
                    Log.d(VS.AUTH_REPO, "Google provider checked")
                    return@withContext "Already signed in with google"
                }

                // now get the jwt token
                val jwt = sharedSupabase.auth.currentSessionOrNull()?.accessToken
                if (jwt == null) {
                    Log.e(VS.AUTH_REPO, "No JWT after sign-in")
                    return@withContext "No token"
                }
                val response =
                    sharedSupabase.httpClient.post("${getSharedSBUrl()}/${VS.POST_FUNCTION_JTW}") {
                        header(HttpHeaders.Authorization, "Bearer $jwt")
                        header(HttpHeaders.ContentType, "application/json")
                        setBody(
                            """{ "password": "${pwd?.password?.pd}", "user_id": "$userId" } """
                        )
                    }
                if (response.status == HttpStatusCode.Companion.OK) {
                    val statusBody = response.bodyAsText()
                    Log.d(VS.AUTH_REPO, "User synced: ${response.status} - $statusBody")
                    // delete the password if it exist
                    deletePwd(pwd)
                    return@withContext "Sign in to synced DB."
                } else {
                    val errorBody = response.bodyAsText()
                    Log.e(VS.AUTH_REPO, "Failed: ${response.status} - $errorBody")
                    if (errorBody.contains(VS.USER_EXISTS)) {
                        deletePwd(pwd)
                    }
                    return@withContext "Couldn't sign in to the synced DB."
                }
            } catch (e: Exception) {
                Log.e(VS.AUTH_REPO, "$e")
                return@withContext "Something went wrong"
            }
        }
    }

    private fun deletePwd(pwd: Pwd?) {
        if (pwd != null) {
            pwdDao.deletePwd(pwd)
        }
    }

    /**
     *  In case there's an issue signing in with either account
     *  sign out of the one that did successfully sign in.
     */
    private suspend fun signOutIssue() {
        try {
            val syncedUser = syncedSupabase.auth.currentUserOrNull()
            val sharedUser = sharedSupabase.auth.currentUserOrNull()
            if (syncedUser == null && sharedUser != null
            ) {
                sharedSupabase.auth.signOut()
            } else if (syncedUser != null && sharedUser == null) {
                syncedSupabase.auth.signOut()
            }
        } catch (e: Exception) {
            Log.e(VS.AUTH_REPO, "$e")
        }
    }
}