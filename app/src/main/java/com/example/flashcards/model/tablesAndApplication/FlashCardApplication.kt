package com.example.flashcards.model.tablesAndApplication
import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


/** This creates the application that will have the application container
 * which gets the database and all of its contents + the repositories */
class FlashCardApplication : Application() {
    lateinit var container: AppContainer
    private val applicationScope = CoroutineScope(SupervisorJob())
        override fun onCreate() {
            super.onCreate()
            container = AppDataContainer(this, applicationScope)
        }
}