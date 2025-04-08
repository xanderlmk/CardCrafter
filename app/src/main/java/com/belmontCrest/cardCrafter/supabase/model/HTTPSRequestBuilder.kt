@file:Suppress("PropertyName")

package com.belmontCrest.cardCrafter.supabase.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleClientResponse(
    @SerializedName(GOOGLE_ID) val google_id: String
)

private const val GOOGLE_ID = "google_id"
@Serializable
sealed class GoogleCredentials {
    @Serializable
    data class Success(val credentials : String) : GoogleCredentials()
    data class Failure(val errorMessage: String) : GoogleCredentials()
}