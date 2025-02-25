@file:Suppress("PropertyName")

package com.example.flashcards.supabase.model

import com.example.flashcards.BuildConfig
import retrofit2.Call
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

@Serializable
data class GoogleClientResponse(
    @SerializedName("google_id") val GOOGLE_CLIENT_ID: String
)
object RetrofitClient {
    private const val BASE_URL: String = BuildConfig.SUPABASE_URL.toString()
    val instance: SupabaseApi by lazy {
        val client = OkHttpClient.Builder()
            .followRedirects(true)
            .build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseApi::class.java)
    }
}
interface SupabaseApi {
    @Headers("Content-Type: application/json")
    @POST("functions/v1/getKeys")
    fun getGoogleClientId(
        @Header("Authorization") authHeader: String
    ): Call<GoogleClientResponse>
}