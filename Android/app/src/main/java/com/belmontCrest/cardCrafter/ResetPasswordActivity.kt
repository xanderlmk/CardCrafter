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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.belmontCrest.cardCrafter.ui.theme.scrollableBoxViewModifier
import com.belmontCrest.cardCrafter.ui.functions.CustomText
import com.belmontCrest.cardCrafter.ui.functions.PasswordTextField
import com.belmontCrest.cardCrafter.ui.functions.buttons.SubmitButton
import com.belmontCrest.cardCrafter.ui.functions.showToastMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.getValue

@RequiresApi(Build.VERSION_CODES.Q)
class ResetPasswordActivity : ComponentActivity() {
    private lateinit var callback: (String, String) -> Unit
    private val deepLinksVM: DeepLinksViewModel by viewModels { AppVMProvider.Factory }
    private val applicationScope = CoroutineScope(SupervisorJob())


    override fun onCreate(savedInstanceState: Bundle?) {
        //val sharedPrefs = applicationContext.getSharedPreferences("app_preferences", MODE_PRIVATE)
        val prefRepository: PrefRepository = PrefRepositoryImpl(applicationContext)
        val preferences = PreferencesManager(prefRepository, applicationScope)
        super.onCreate(savedInstanceState)
        handleDeepLink(intent)
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
            var password by rememberSaveable { mutableStateOf("") }
            var confirmPass by rememberSaveable { mutableStateOf("") }
            var enabled by rememberSaveable { mutableStateOf(true) }
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current
            FlashcardsTheme(
                darkTheme = pv.theme.isDark() || (pv.theme.isSystem() && isSystemInDarkTheme()),
                dynamicColor = pv.theme.isDynamic() || pv.theme.isSystem(),
                cuteTheme = pv.theme.isCute()
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
                callback(email, createdAt)
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