package com.belmontCrest.cardCrafter.model.application

import android.app.Application
import com.belmontCrest.cardCrafter.supabase.model.createSharedSupabase
import com.belmontCrest.cardCrafter.supabase.model.createSyncedSupabase
import com.belmontCrest.cardCrafter.supabase.model.getSharedSBKey
import com.belmontCrest.cardCrafter.supabase.model.getSharedSBUrl
import com.belmontCrest.cardCrafter.supabase.model.getSyncedSBKey
import com.belmontCrest.cardCrafter.supabase.model.getSyncedSBUrl
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


/** This creates the application that will have the application container
 * which gets the database, supabase client, and
 * all of its contents + the repositories */
class FlashCardApplication : Application() {
    lateinit var container: AppContainer
    lateinit var sharedSupabase: SupabaseClient
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