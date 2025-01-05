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
import com.example.flashcards.controller.navigation.AppNavHost
import com.example.flashcards.controller.AppViewModelProvider
import com.example.flashcards.controller.navigation.AllViewModels
import com.example.flashcards.controller.viewModels.BasicCardViewModel
import com.example.flashcards.controller.viewModels.CardTypeViewModel
import com.example.flashcards.controller.viewModels.CardViewModel
import com.example.flashcards.controller.viewModels.deckViewsModels.DeckViewModel
import com.example.flashcards.controller.viewModels.CardDeckViewModel
import com.example.flashcards.controller.viewModels.HintCardViewModel
import com.example.flashcards.controller.viewModels.MultiChoiceCardViewModel
import com.example.flashcards.controller.viewModels.ThreeCardViewModel
import com.example.flashcards.model.uiModels.Preferences
import com.example.flashcards.model.uiModels.PreferencesManager
import com.example.flashcards.ui.theme.FlashcardsTheme

class MainActivity : ComponentActivity() {
    private val deckViewModel: DeckViewModel by viewModels {
        AppViewModelProvider.Factory
    }
    private val cardViewModel: CardViewModel by viewModels {
        AppViewModelProvider.Factory
    }
    private val dueCardsViewModel: CardDeckViewModel by viewModels{
        AppViewModelProvider.Factory
    }
    private val basicCardViewModel: BasicCardViewModel by viewModels {
        AppViewModelProvider.Factory
    }
    private val threeCardViewModel: ThreeCardViewModel by viewModels {
        AppViewModelProvider.Factory
    }
    private val hintCardViewModel: HintCardViewModel by viewModels {
        AppViewModelProvider.Factory
    }
    private val cardTypeViewModel: CardTypeViewModel by viewModels {
        AppViewModelProvider.Factory
    }
    private val multiChoiceCardViewModel: MultiChoiceCardViewModel by viewModels {
        AppViewModelProvider.Factory
    }
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var preferences: Preferences
    private lateinit var cardTypes : AllViewModels

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            cardTypes = AllViewModels(
                basicCardViewModel,
                hintCardViewModel,
                threeCardViewModel,
                multiChoiceCardViewModel
            )

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
                        cardViewModel = cardViewModel,
                        dueCardsViewModel = dueCardsViewModel,
                        cardTypeViewModel = cardTypeViewModel,
                        cardTypes = cardTypes,
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