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
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.flashcards.controller.navigation.AppNavHost
import com.example.flashcards.controller.AppViewModelProvider
import com.example.flashcards.controller.viewModels.cardViewsModels.EditingCardListViewModel
import com.example.flashcards.controller.viewModels.deckViewsModels.MainViewModel
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.model.uiModels.PreferencesManager
import com.example.flashcards.ui.theme.FlashcardsTheme
import kotlinx.coroutines.coroutineScope

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels {
        AppViewModelProvider.Factory
    }
    private val editingCardListViewModel: EditingCardListViewModel by viewModels {
        AppViewModelProvider.Factory
    }
    private lateinit var preferences: PreferencesManager


    private lateinit var fields: Fields


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            preferences = remember {
                PreferencesManager(
                    applicationContext
                )
            }
            /**
             * Making sure that if it's their first time,
             * there is no database update,
             * and if the application was removed from the tabs (killed)
             * this means that appStated.value will be null or false
             * so you should perform database update
             **/
            if ((mainViewModel.appStarted.value == null ||
                mainViewModel.appStarted.value == false ) &&
                !preferences.isFirstTime
            ) {
                LaunchedEffect(Unit) {
                    coroutineScope {
                        mainViewModel.performDatabaseUpdate()
                    }
                }
            }
            fields = remember { Fields() }


            val isSystemDark = isSystemInDarkTheme()

            if (preferences.isFirstTime) {
                preferences.darkTheme.value = isSystemDark
                preferences.isDarkThemeEnabled = isSystemDark
                preferences.isFirstTime = false
            }

            FlashcardsTheme(
                darkTheme = preferences.darkTheme.value,
                dynamicColor = preferences.customScheme.value
            ) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavHost(
                        navController = rememberNavController(),
                        mainViewModel = mainViewModel,
                        editingCardListVM = editingCardListViewModel,
                        preferences = preferences,
                        fields = fields,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }


    override fun onStop() {
        super.onStop()
        if (::preferences.isInitialized) {
        preferences.saveDarkTheme()
        preferences.saveCustomScheme()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::preferences.isInitialized) {
        preferences.saveDarkTheme()
        preferences.saveCustomScheme()
        }
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