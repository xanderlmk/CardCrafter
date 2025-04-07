@file:OptIn(ExperimentalMaterial3Api::class)

package com.belmontCrest.cardCrafter

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.coroutineScope
import androidx.navigation.compose.rememberNavController
import com.belmontCrest.cardCrafter.controller.AppViewModelProvider
import com.belmontCrest.cardCrafter.controller.navigation.NavViewModel
import com.belmontCrest.cardCrafter.controller.navigation.navHosts.AppNavHost
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.EditingCardListViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.MainViewModel
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.model.uiModels.PreferencesManager
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.ui.theme.FlashcardsTheme
import io.github.jan.supabase.annotations.SupabaseInternal
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(SupabaseInternal::class)
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels {
        AppViewModelProvider.Factory
    }
    private val editingCardListViewModel: EditingCardListViewModel by viewModels {
        AppViewModelProvider.Factory
    }
    private val supabaseVM: SupabaseViewModel by viewModels {
        AppViewModelProvider.Factory
    }

    private val navViewModel : NavViewModel by viewModels{
        AppViewModelProvider.Factory
    }

    private lateinit var preferences: PreferencesManager
    private lateinit var fields: Fields

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /** Getting our supabase credentials */
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
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
                supabaseVM.connectSupabase()
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
                AppNavHost(
                    navController = navController,
                    mainViewModel = mainViewModel,
                    editingCardListVM = editingCardListViewModel,
                    preferences = preferences,
                    fields = fields,
                    supabaseVM = supabaseVM,
                    navViewModel = navViewModel
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycle.coroutineScope.launch {
            supabaseVM.connectSupabase()
        }
    }

    override fun onStop() {
        super.onStop()
        if (::preferences.isInitialized) {
            preferences.savePreferences()
        }
        supabaseVM.disconnectSupabaseRT()
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