package com.belmontCrest.cardCrafter.model.application

import android.app.Application
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.createSharedSupabase
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.getSBKey
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.getSBUrl
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


/** This creates the application that will have the application container
 * which gets the database, supabase client, and
 * all of its contents + the repositories */
class FlashCardApplication : Application() {
    lateinit var container: AppContainer
    /** Shared Supabase for community decks */
    lateinit var supabase: SupabaseClient
    /** Synced Supabase for personal/local decks */
    private val applicationScope = CoroutineScope(SupervisorJob())
    override fun onCreate() {
        super.onCreate()
        supabase = createSharedSupabase(getSBUrl(), getSBKey())
        container = AppDataContainer(
            this, applicationScope, supabase = supabase
        )
    }
}