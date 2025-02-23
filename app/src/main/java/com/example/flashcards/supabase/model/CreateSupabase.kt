package com.example.flashcards.supabase.model

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets


/** Creating our supabase client for the user to use if they sign in/up. */
@OptIn(SupabaseInternal::class)
fun createSupabase(
    supabaseUrl : String,
    supabaseKey : String
): SupabaseClient {
    return createSupabaseClient(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseKey
    ) {
        install(Realtime)
        install(Postgrest)
        install(Auth)
        httpConfig {
            engine {
                CIO.create()
                install(WebSockets)
            }
        }
    }
}