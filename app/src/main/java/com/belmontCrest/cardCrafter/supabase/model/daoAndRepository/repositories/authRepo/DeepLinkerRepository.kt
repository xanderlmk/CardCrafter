package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.authRepo

import android.content.Intent
import android.util.Log
import com.belmontCrest.cardCrafter.supabase.model.AuthRepoVals
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.getSharedSBUrl
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.handleDeeplinks
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface DeepLinkerRepository {
    suspend fun deepLinker(intent: Intent, callback: (String, String) -> Unit): String

    suspend fun resetPassword(inputPassword: String): String
}

class DeepLinkerRepositoryImpl(
    val sharedSupabase: SupabaseClient,
) : DeepLinkerRepository {
    companion object {
        private val VS = AuthRepoVals
    }

    override suspend fun deepLinker(
        intent: Intent,
        callback: (String, String) -> Unit
    ): String {
        return withContext(Dispatchers.IO) {
            try {
                sharedSupabase.handleDeeplinks(
                    intent = intent,
                    onSessionSuccess = { session ->
                        Log.d("LOGIN", "Log in successfully with user info: ${session.user}")
                        session.user?.apply {
                            callback(email ?: "", createdAt.toString())
                        }
                    }
                )
                VS.SUCCESS
            } catch (e: Exception) {
                when (e) {
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

    override suspend fun resetPassword(inputPassword: String): String {
        return withContext(Dispatchers.IO) {
            try {
                sharedSupabase.auth.updateUser { password = inputPassword }
                // now get the jwt token
                val jwt = sharedSupabase.auth.currentSessionOrNull()?.accessToken
                val userId = sharedSupabase.auth.currentUserOrNull()?.id

                if (jwt == null) {
                    Log.e(VS.AUTH_REPO, "No JWT after sign-in")
                    return@withContext "No token"
                }
                val response =
                    sharedSupabase.httpClient.post("${getSharedSBUrl()}/${VS.POST_FUNCTION_RESET}") {
                        header(HttpHeaders.Authorization, "Bearer $jwt")
                        header(HttpHeaders.ContentType, "application/json")
                        setBody(
                            """{ "password": "$inputPassword", "user_id": "$userId" } """
                        )
                    }

                if (response.status !== HttpStatusCode.OK) {
                    return@withContext "Error"
                }
                VS.SUCCESS
            } catch (e: Exception) {
                Log.e(VS.AUTH_REPO, "$e")
                "Error"
            }
        }
    }
}