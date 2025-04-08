package com.belmontCrest.cardCrafter.model.application
import android.app.Application
import com.belmontCrest.cardCrafter.supabase.model.createSupabase
import com.belmontCrest.cardCrafter.supabase.model.getSBKey
import com.belmontCrest.cardCrafter.supabase.model.getSBUrl
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


/** This creates the application that will have the application container
 * which gets the database, supabase client, and
 * all of its contents + the repositories */
class FlashCardApplication : Application() {
    lateinit var container: AppContainer
    lateinit var supabase : SupabaseClient
    private val applicationScope = CoroutineScope(SupervisorJob())
        override fun onCreate() {
            super.onCreate()
            supabase = createSupabase(getSBUrl(), getSBKey())
            container = AppDataContainer(this, applicationScope, supabase)
        }
}