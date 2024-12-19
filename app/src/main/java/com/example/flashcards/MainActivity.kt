package com.example.flashcards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.flashcards.controller.AppNavHost
import com.example.flashcards.controller.AppViewModelProvider
import com.example.flashcards.controller.viewModels.DeckViewModel
import com.example.flashcards.model.Preferences
import com.example.flashcards.model.PreferencesManager
import com.example.flashcards.ui.theme.FlashcardsTheme

class MainActivity : ComponentActivity() {
    private val deckViewModel : DeckViewModel by viewModels {
        AppViewModelProvider.Factory
    }
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isSystemDark = isSystemInDarkTheme()

            preferencesManager = remember { PreferencesManager(applicationContext) }

            if (preferencesManager.isFirstTime) {
                preferencesManager.isDarkThemeEnabled = isSystemDark
                preferencesManager.isFirstTime = false
            }
            preferences = remember {
                Preferences(
                    darkTheme = mutableStateOf(isSystemDark),
                    preferencesManager = preferencesManager
                )
            }
                FlashcardsTheme(
                    darkTheme = preferences.darkTheme.value,
                    dynamicColor = preferences.customScheme.value
                ) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        AppNavHost(
                            navController = rememberNavController(),
                            deckViewModel = deckViewModel,
                            preferences = preferences,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
        }
    }
    override fun onStop() {
        super.onStop()
        preferences.saveDarkTheme()
        preferences.saveCustomScheme()
    }
    override fun onPause() {
        super.onPause()
        preferences.saveDarkTheme()
        preferences.saveCustomScheme()
    }
    override fun onDestroy() {
        super.onDestroy()
        preferences.saveDarkTheme()
        preferences.saveCustomScheme()
    }
}

/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FlashcardsTheme {

        var view = MainView()
        view.DeckList(viewModel = MainViewModel(/* Pass a mock or test repository here */))
    }
}*/