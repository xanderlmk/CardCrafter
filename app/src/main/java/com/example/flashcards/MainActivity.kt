package com.example.flashcards

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.coroutineScope
import androidx.navigation.compose.rememberNavController
import com.example.flashcards.controller.navigation.AppNavHost
import com.example.flashcards.controller.AppViewModelProvider
import com.example.flashcards.controller.viewModels.cardViewsModels.EditingCardListViewModel
import com.example.flashcards.controller.viewModels.deckViewsModels.MainViewModel
import com.example.flashcards.supabase.model.createSupabase
import com.example.flashcards.model.uiModels.Fields
import com.example.flashcards.model.uiModels.PreferencesManager
import com.example.flashcards.supabase.model.getSBKey
import com.example.flashcards.supabase.model.getSBUrl
import com.example.flashcards.ui.theme.FlashcardsTheme
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.net.SocketException

@OptIn(SupabaseInternal::class)
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels {
        AppViewModelProvider.Factory
    }
    private val editingCardListViewModel: EditingCardListViewModel by viewModels {
        AppViewModelProvider.Factory
    }
    private lateinit var preferences: PreferencesManager


    private lateinit var fields: Fields

    val supabaseUrl = getSBUrl()
    val supabaseKey = getSBKey()
    private var supabase = createSupabase(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseKey
    )

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /** Getting our supabase credentials */

        enableEdgeToEdge()
        setContent {
            preferences = rememberUpdatedState(
                PreferencesManager(
                    applicationContext
                )
            ).value
            /**
             * Making sure that if it's their first time,
             * there is no database update,
             * and if the application was removed from the tabs (killed)
             * this means that appStated.value will be null or false
             * so you should perform database update
             **/
            if ((mainViewModel.appStarted.value == null ||
                        mainViewModel.appStarted.value == false) &&
                !preferences.getIsFirstTime()
            ) {
                LaunchedEffect(Unit) {
                    coroutineScope {
                        mainViewModel.performDatabaseUpdate()
                    }
                }
            }
            fields = rememberSaveable { Fields() }

            LaunchedEffect(Unit) {
                supabase.useHTTPS
                supabase.realtime.connect()
            }
            val isSystemDark = isSystemInDarkTheme()


            if (preferences.getIsFirstTime()) {
                preferences.darkTheme.value = isSystemDark
                preferences.setDarkTheme(isSystemDark)
                preferences.setIsFirstTime()
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
                        supabase = supabase,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycle.coroutineScope.launch {
            try {
                if (supabase.realtime.status.value != Realtime.Status.CONNECTED) {
                    supabase.realtime.connect()
                }

            } catch (e: SocketException) {
                Log.d("Socket Issue", "SocketException: $e")
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (::preferences.isInitialized) {
            preferences.savePreferences()
        }
        supabase.realtime.disconnect()
    }

    override fun onPause() {
        super.onPause()
        if (::preferences.isInitialized) {
            preferences.savePreferences()
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