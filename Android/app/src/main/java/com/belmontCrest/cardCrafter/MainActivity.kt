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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.belmontCrest.cardCrafter.model.application.AppViewModelProvider
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.navigation.navHosts.AppNavHost
import com.belmontCrest.cardCrafter.controller.viewModels.cardViewsModels.EditingCardListViewModel
import com.belmontCrest.cardCrafter.controller.viewModels.deckViewsModels.MainViewModel
import com.belmontCrest.cardCrafter.model.ui.Fields
import com.belmontCrest.cardCrafter.model.application.PreferencesManager
import com.belmontCrest.cardCrafter.model.application.setPreferenceValues
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

    private val navViewModel: NavViewModel by viewModels {
        AppViewModelProvider.Factory
    }
    private lateinit var fields: Fields

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = PreferencesManager(applicationContext, lifecycleScope)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val pv = setPreferenceValues(preferences)
            /**
             * Making sure that if it's their first time,
             * there is no database update,
             * and if the application was removed from the tabs (killed)
             * this means that appStated.value will be null or false
             * so you should perform database update
             **/
            if (mainViewModel.appStarted.value != true && !preferences.getIsFirstTime()) {
                LaunchedEffect(Unit) {
                    coroutineScope {
                        mainViewModel.performDatabaseUpdate()
                    }
                }
            }

            fields = rememberSaveable { Fields() }

            LaunchedEffect(Unit) { supabaseVM.connectSupabase() }

            if (preferences.getIsFirstTime()) {
                val isSystemDark = isSystemInDarkTheme()
                preferences.setDarkTheme(isSystemDark)
                preferences.setIsFirstTime()
            }

            FlashcardsTheme(
                darkTheme = pv.darkTheme,
                dynamicColor = pv.dynamicTheme,
                cuteTheme = pv.cuteTheme
            ) {
                AppNavHost(
                    mainNavController = navController,
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

    override fun recreate() {
        super.recreate()
        lifecycle.coroutineScope.launch {
            supabaseVM.getCurrentUserInfo()
        }
    }

    override fun onRestart() {
        super.onRestart()
        lifecycle.coroutineScope.launch {
            supabaseVM.getCurrentUserInfo()
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
        supabaseVM.disconnectSupabaseRT()
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