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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.belmontCrest.cardCrafter.model.application.AppViewModelProvider
import com.belmontCrest.cardCrafter.model.ui.PreferencesManager
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.DeepLinksViewModel
import com.belmontCrest.cardCrafter.ui.theme.ColorSchemeClass
import com.belmontCrest.cardCrafter.ui.theme.FlashcardsTheme
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.scrollableBoxViewModifier
import com.belmontCrest.cardCrafter.uiFunctions.CustomText
import com.belmontCrest.cardCrafter.uiFunctions.PasswordTextField
import com.belmontCrest.cardCrafter.uiFunctions.buttons.SubmitButton
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage
import kotlinx.coroutines.launch
import kotlin.getValue

@RequiresApi(Build.VERSION_CODES.Q)
class ResetPasswordActivity : ComponentActivity() {

    private lateinit var preferences: PreferencesManager
    private lateinit var callback: (String, String) -> Unit
    private val deepLinksVM: DeepLinksViewModel by viewModels {
        AppViewModelProvider.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleDeepLink(intent)
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
            preferences = rememberUpdatedState(
                PreferencesManager(applicationContext)
            ).value
            val colorScheme = remember { ColorSchemeClass() }
            colorScheme.colorScheme = MaterialTheme.colorScheme

            val getUIStyle = GetUIStyle(
                colorScheme, preferences.darkTheme.value,
                preferences.customScheme.value, preferences.cuteTheme.value
            )
            var password by rememberSaveable { mutableStateOf("") }
            var confirmPass by rememberSaveable { mutableStateOf("") }
            var enabled by rememberSaveable { mutableStateOf(true) }
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current
            FlashcardsTheme(
                darkTheme = preferences.darkTheme.value,
                dynamicColor = preferences.customScheme.value,
                cuteTheme = preferences.cuteTheme.value
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .scrollableBoxViewModifier(
                                rememberScrollState(), getUIStyle.getColorScheme()
                            )
                            .padding(padding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CustomText("Reset password", getUIStyle)
                        PasswordTextField(
                            label = "New Password", password = password,
                            onPasswordChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            getUIStyle = getUIStyle
                        )
                        PasswordTextField(
                            label = "Confirm Password", password = confirmPass,
                            onPasswordChange = { confirmPass = it },
                            modifier = Modifier.fillMaxWidth(),
                            getUIStyle = getUIStyle
                        )
                        SubmitButton(onClick = {
                            coroutineScope.launch {
                                enabled = false
                                if (password != confirmPass) {
                                    showToastMessage(context, "Passwords don't match")
                                    enabled = true
                                    return@launch
                                } else if (password.length < 10) {
                                    showToastMessage(
                                        context,
                                        "You need 10 characters, you only have ${password.length}"
                                    )
                                    enabled = true
                                    return@launch
                                } else if (!isValidPassword(password)) {
                                    showToastMessage(context, "Not a valid password")
                                    enabled = true
                                    return@launch
                                }
                                deepLinksVM.resetPassword(password).let {
                                    if (it) {
                                        navigateToMainApp()
                                    } else {
                                        showToastMessage(context, "Failed to reset password.")
                                        enabled = true
                                    }
                                }
                            }
                        }, enabled, getUIStyle, "Reset", Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent) {
        lifecycleScope.launch {
            deepLinksVM.deepLinker(intent) { email, createdAt ->
                callback(email, createdAt.toString())
            }
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val hasLowercase = password.any { it.isLowerCase() }
        val hasUppercase = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSymbol = password.any { !it.isLetterOrDigit() }

        return hasLowercase && hasUppercase && hasDigit && hasSymbol
    }

    private fun navigateToMainApp() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
    }
}