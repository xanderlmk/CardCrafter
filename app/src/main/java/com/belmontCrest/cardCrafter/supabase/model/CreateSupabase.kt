package com.belmontCrest.cardCrafter.supabase.model

import com.belmontCrest.cardCrafter.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.supabaseJson
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.websocket.WebSockets
import kotlinx.serialization.json.Json

/** Creating our supabase client for the user to use if they sign in/up. */
@OptIn(SupabaseInternal::class)
fun createSupabase(
    supabaseUrl : String,
    supabaseKey : String
): SupabaseClient {
    return createSupabaseClient(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseKey,
    ) {
        install(Realtime)
        install(Postgrest)
        install(Auth)
        httpConfig {
            install(WebSockets)
            engine {
                OkHttp.create()
            }
        }
    }
}
private const val  supabaseUrl = BuildConfig.SUPABASE_URL
private const val supabaseKey = BuildConfig.SUPABASE_KEY

fun getSBUrl() : String {
    return supabaseUrl
}

fun getSBKey() : String {
    return supabaseKey
}

