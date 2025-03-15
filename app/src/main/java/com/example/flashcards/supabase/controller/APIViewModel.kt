package com.example.flashcards.supabase.controller

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.BuildConfig
import com.example.flashcards.supabase.model.GoogleClientResponse
import com.example.flashcards.supabase.model.RetrofitClient
import com.example.flashcards.supabase.model.SBDecks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/** Our API caller. */
class APIViewModel : ViewModel() {
    private var googleClientId = MutableStateFlow("")
    val clientId = googleClientId.asStateFlow()
    init {
        returnGoogleClientId()
    }
    private fun returnGoogleClientId() {
        viewModelScope.launch {
            val api = RetrofitClient.instance
            api.getGoogleClientId("Bearer ${BuildConfig.SUPABASE_KEY}")
                .enqueue(object : Callback<GoogleClientResponse> {
                    override fun onResponse(
                        call: Call<GoogleClientResponse>,
                        response: Response<GoogleClientResponse>
                    ) {
                        if (response.isSuccessful) {
                            googleClientId.value = response.body()?.GOOGLE_CLIENT_ID ?: ""
                        } else {
                            Log.e(
                                "GOOGLE_CLIENT_ID",
                                "Response failed: ${response.errorBody()?.string()}"
                            )
                        }
                    }

                    override fun onFailure(call: Call<GoogleClientResponse>, t: Throwable) {
                        Log.e("Error", "Network call failed", t)
                    }
                })
        }
    }
}