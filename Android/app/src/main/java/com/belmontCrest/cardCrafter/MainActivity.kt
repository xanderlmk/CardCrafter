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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.coroutineScope
import androidx.navigation.compose.rememberNavController
import com.belmontCrest.cardCrafter.model.application.AppVMProvider
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.navigation.navHosts.AppNavHost
import com.belmontCrest.cardCrafter.model.ui.Fields
import com.belmontCrest.cardCrafter.model.application.preferences.PreferencesManager
import com.belmontCrest.cardCrafter.model.application.preferences.PrefRepository
import com.belmontCrest.cardCrafter.model.application.preferences.PrefRepositoryImpl
import com.belmontCrest.cardCrafter.ui.theme.FlashcardsTheme
import io.github.jan.supabase.annotations.SupabaseInternal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(SupabaseInternal::class)
class MainActivity : ComponentActivity() {
    private val navViewModel: NavViewModel by viewModels { AppVMProvider.Factory }
    private lateinit var fields: Fields
    private val applicationScope = CoroutineScope(SupervisorJob())

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        //val sharedPrefs = applicationContext.getSharedPreferences("app_preferences", MODE_PRIVATE)
        val prefRepository: PrefRepository = PrefRepositoryImpl(applicationContext)
        val preferences = PreferencesManager(prefRepository, applicationScope)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val pv by preferences.pv.collectAsStateWithLifecycle()
            val navController = rememberNavController()
            fields = rememberSaveable { Fields() }
            FlashcardsTheme(
                darkTheme = pv.theme.isDark() || (pv.theme.isSystem() && isSystemInDarkTheme()),
                dynamicColor = pv.theme.isDynamic() || pv.theme.isSystem(),
                cuteTheme = pv.theme.isCute()
            ) {
                AppNavHost(
                    mainNavController = navController,
                    preferences = preferences, fields = fields, navViewModel = navViewModel
                )
            }
        }
    }

    override fun recreate() {
        super.recreate()
        lifecycle.coroutineScope.launch {
            navViewModel.getCurrentUserInfo()
        }
    }

    override fun onRestart() {
        super.onRestart()
        lifecycle.coroutineScope.launch {
            navViewModel.getCurrentUserInfo()
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycle.coroutineScope.launch {
            navViewModel.connectSupabase()
        }
    }

    override fun onStop() {
        super.onStop()
        navViewModel.disconnectSupabaseRT()
    }

}
