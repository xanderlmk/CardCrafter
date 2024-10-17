package com.example.flashcards.model
import android.app.Application


class FlashCardApplication : Application() {

    lateinit var container: AppContainer

        override fun onCreate() {
            super.onCreate()
            container = AppDataContainer(this)
        }
}