package com.belmontCrest.cardCrafter

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.belmontCrest.cardCrafter.model.application.AppVMProvider
import com.belmontCrest.cardCrafter.model.application.preferences.PreferencesManager
import com.belmontCrest.cardCrafter.model.application.preferences.PrefRepository
import com.belmontCrest.cardCrafter.model.application.preferences.PrefRepositoryImpl
import com.belmontCrest.cardCrafter.supabase.controller.view.models.DeepLinksViewModel
import com.belmontCrest.cardCrafter.ui.theme.ColorSchemeClass
import com.belmontCrest.cardCrafter.ui.theme.FlashcardsTheme
import com.belmontCrest.cardCrafter.ui.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.ui.functions.CustomText
import com.belmontCrest.cardCrafter.ui.functions.buttons.SubmitButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.getValue

@RequiresApi(Build.VERSION_CODES.Q)
class DeepLinkerActivity : ComponentActivity() {
    private val deepLinksVM: DeepLinksViewModel by viewModels { AppVMProvider.Factory }
    private val applicationScope = CoroutineScope(SupervisorJob())

    private lateinit var callback: (String, String) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        //val sharedPrefs = applicationContext.getSharedPreferences("app_preferences", MODE_PRIVATE)
        val prefRepository: PrefRepository = PrefRepositoryImpl(applicationContext)
        val preferences = PreferencesManager(prefRepository, applicationScope)
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            deepLinksVM.deepLinker(
                intent, callback = { email, createdAt ->
                    callback(email, createdAt)
                })
        }
        enableEdgeToEdge()
        setContent {
            val emailState = rememberSaveable { mutableStateOf("") }
            val createdAtState = rememberSaveable { mutableStateOf("") }
            LaunchedEffect(Unit) {
                callback = { email, created ->
                    emailState.value = email
                    createdAtState.value = created
                }
            }
            val colorScheme = remember { ColorSchemeClass() }
            colorScheme.colorScheme = MaterialTheme.colorScheme

            val pv by preferences.pv.collectAsStateWithLifecycle()
            val getUIStyle = GetUIStyle(colorScheme, pv.theme)
            FlashcardsTheme(
                darkTheme = pv.theme.isDark() || (pv.theme.isSystem() && isSystemInDarkTheme()),
                dynamicColor = pv.theme.isDynamic() || pv.theme.isSystem(),
                cuteTheme = pv.theme.isCute()
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { padding ->
                    SignInSuccessScreen(
                        modifier = Modifier
                            .boxViewsModifier(getUIStyle.getColorScheme())
                            .padding(padding),
                        getUIStyle = getUIStyle,
                        email = emailState.value,
                        createdAt = createdAtState.value,
                        onClick = { navigateToMainApp() }
                    )
                }
            }
        }
    }

    @Composable
    fun SignInSuccessScreen(
        modifier: Modifier, getUIStyle: GetUIStyle,
        email: String, createdAt: String, onClick: () -> Unit
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CustomText("Successfully created your account!", getUIStyle)
            CustomText(email, getUIStyle)
            CustomText(createdAt, getUIStyle)
            SubmitButton(
                onClick = { onClick() }, enabled = true,
                getUIStyle, "Return"
            )
        }
    }

    private fun navigateToMainApp() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
    }
}