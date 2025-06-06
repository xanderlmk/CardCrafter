package com.belmontCrest.cardCrafter.model.application

import android.app.Application
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.createSharedSupabase
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.createSyncedSupabase
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.getSharedSBKey
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.getSharedSBUrl
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.getSyncedSBKey
import com.belmontCrest.cardCrafter.supabase.model.createSupabase.getSyncedSBUrl
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


/** This creates the application that will have the application container
 * which gets the database, supabase client, and
 * all of its contents + the repositories */
class FlashCardApplication : Application() {
    lateinit var container: AppContainer
    /** Shared Supabase for community decks */
    lateinit var sharedSupabase: SupabaseClient
    /** Synced Supabase for personal/local decks */
    lateinit var syncedSupabase: SupabaseClient
    private val applicationScope = CoroutineScope(SupervisorJob())
    override fun onCreate() {
        super.onCreate()
        sharedSupabase = createSharedSupabase(getSharedSBUrl(), getSharedSBKey())
        syncedSupabase = createSyncedSupabase(getSyncedSBUrl(), getSyncedSBKey())
        container = AppDataContainer(
            this, applicationScope,
            sharedSupabase = sharedSupabase, syncedSupabase = syncedSupabase
        )
    }
}