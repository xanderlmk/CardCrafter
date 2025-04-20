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
    const val SCHEME = "app"
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
            scheme = AuthConfig.SCHEME
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
@OptIn(SupabaseInternal::class)
fun createSyncedSupabase(
    supabaseUrl: String,
    supabaseKey: String
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
private const val sharedSupabaseUrl = BuildConfig.SUPABASE_URL
private const val sharedSupabaseKey = BuildConfig.SUPABASE_KEY
private const val syncedSupabaseUrl = BuildConfig.SYNCED_SB_URL
private const val syncedSupabaseKey = BuildConfig.SYNCED_SB_KEY

fun getSharedSBUrl(): String {
    return sharedSupabaseUrl
}

fun getSharedSBKey(): String {
    return sharedSupabaseKey
}

fun getSyncedSBUrl(): String {
    return syncedSupabaseUrl
}

fun getSyncedSBKey(): String {
    return syncedSupabaseKey
}

