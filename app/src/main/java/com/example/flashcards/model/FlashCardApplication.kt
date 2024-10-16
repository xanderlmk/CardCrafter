package com.example.flashcards.model
import android.app.Application

class FlashCardApplication {


    class FlashCardApplication : Application() {

        /**
         * AppContainer instance used by the rest of classes to obtain dependencies
         */
        lateinit var container: AppContainer

        override fun onCreate() {
            super.onCreate()
            container = AppDataContainer(this)
        }
    }
}