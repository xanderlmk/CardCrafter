package com.belmontCrest.cardCrafter

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.belmontCrest.cardCrafter.model.application.AppViewModelProvider
import com.belmontCrest.cardCrafter.model.application.PreferencesManager
import com.belmontCrest.cardCrafter.model.application.setPreferenceValues
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.DeepLinksViewModel
import com.belmontCrest.cardCrafter.ui.theme.ColorSchemeClass
import com.belmontCrest.cardCrafter.ui.theme.FlashcardsTheme
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.uiFunctions.buttons.SubmitButton
import kotlinx.coroutines.launch
import kotlin.getValue

@RequiresApi(Build.VERSION_CODES.Q)
class DeepLinkerActivity : ComponentActivity() {
    private val deepLinksVM: DeepLinksViewModel by viewModels {
        AppViewModelProvider.Factory
    }

    private lateinit var callback: (String, String) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            deepLinksVM.deepLinker(
                intent, callback = { email, createdAt ->
                    callback(email, createdAt)
                })
        }
        val preferences = PreferencesManager(applicationContext, lifecycleScope)
        enableEdgeToEdge()
        setContent {
            val emailState = remember { mutableStateOf("") }
            val createdAtState = remember { mutableStateOf("") }
            LaunchedEffect(Unit) {
                callback = { email, created ->
                    emailState.value = email
                    createdAtState.value = created
                }
            }
            LaunchedEffect(Unit) {
                callback = { email, created ->
                    emailState.value = email
                    createdAtState.value = created
                }
            }
            val colorScheme = remember { ColorSchemeClass() }
            colorScheme.colorScheme = MaterialTheme.colorScheme

            val pc = setPreferenceValues(preferences)
            val getUIStyle = GetUIStyle(
                colorScheme, pc.darkTheme, pc.dynamicTheme, pc.cuteTheme
            )
            FlashcardsTheme(
                darkTheme = pc.darkTheme,
                dynamicColor = pc.dynamicTheme,
                cuteTheme = pc.cuteTheme
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
            Text("Successfully created your account!")
            Text(email)
            Text(createdAt)
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