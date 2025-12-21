package com.belmontCrest.cardCrafter.supabase.model.createSupabase

import com.belmontCrest.cardCrafter.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.FlowType
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.websocket.WebSockets

private object AuthConfig {
    val FLOW_TYPE = FlowType.PKCE
    const val APP_SCHEME = "app"
    const val HOST = "supabase.com"
}
/** Creating our supabase client for the user to use if they sign in/up. */
@OptIn(SupabaseInternal::class)
fun createSharedSupabase(
    supabaseUrl: String,
    supabaseKey: String
): SupabaseClient {
    return createSupabaseClient(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseKey,
    ) {
        install(Realtime)
        install(Postgrest)
        install(Auth) {
            flowType = AuthConfig.FLOW_TYPE
            scheme = AuthConfig.APP_SCHEME
            host = AuthConfig.HOST
        }
        httpConfig {
            install(WebSockets)
            engine {
                OkHttp.create()
            }
        }
    }
}
private const val supabaseUrl = BuildConfig.SUPABASE_URL
private const val supabaseKey = BuildConfig.SUPABASE_KEY

fun getSBUrl(): String {
    return supabaseUrl
}

fun getSBKey(): String {
    return supabaseKey
}

