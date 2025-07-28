package com.belmontCrest.cardCrafter.supabase.view.authViews.email

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.uiFunctions.EditTextField
import com.belmontCrest.cardCrafter.uiFunctions.PasswordTextField
import com.belmontCrest.cardCrafter.uiFunctions.buttons.SubmitButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun EmailView(
    supabaseVM: SupabaseViewModel, getUIStyle: GetUIStyle,
    onNavigate: () -> Unit, onForgotPassword: () -> Unit
) {
    val context = LocalContext.current
    var inputEmail = rememberSaveable { mutableStateOf("") }
    var inputPassword = rememberSaveable { mutableStateOf("") }
    var inputConfirmPass = rememberSaveable { mutableStateOf("") }
    var enabled = rememberSaveable { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    var show = rememberSaveable { mutableStateOf(false) }
    var signIn = rememberSaveable { mutableStateOf(false) }
    Box(
        modifier = Modifier.boxViewsModifier(getUIStyle.getColorScheme())
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            if (!signIn.value) {
                SignUpWithEmail(
                    inputEmail = inputEmail, inputPassword = inputPassword,
                    inputConfirmPass = inputConfirmPass, enabled = enabled,
                    show = show, coroutineScope = coroutineScope,
                    supabaseVM = supabaseVM, context = context, signIn = signIn,
                    getUIStyle = getUIStyle
                )
            } else {
                SignInWithEmail(
                    inputEmail = inputEmail, inputPassword = inputPassword,
                    enabled = enabled, coroutineScope = coroutineScope,
                    supabaseVM = supabaseVM, context = context, signIn = signIn,
                    getUIStyle = getUIStyle, onNavigate = onNavigate,
                    onForgotPassword = onForgotPassword
                )
            }
        }
    }


}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
private fun SignUpWithEmail(
    inputEmail: MutableState<String>, inputPassword: MutableState<String>,
    inputConfirmPass: MutableState<String>, enabled: MutableState<Boolean>,
    show: MutableState<Boolean>, coroutineScope: CoroutineScope, getUIStyle: GetUIStyle,
    supabaseVM: SupabaseViewModel, context: Context, signIn: MutableState<Boolean>,
) {
    EditTextField(
        value = inputEmail.value,
        onValueChanged = {
            inputEmail.value = it
        },
        labelStr = "Email",
        modifier = Modifier.fillMaxWidth(),
    )
    PasswordTextField(
        password = inputPassword.value,
        onPasswordChange = {
            inputPassword.value = it
        },
        label = "Password",
        modifier = Modifier.fillMaxWidth(),
        getUIStyle = getUIStyle
    )
    PasswordTextField(
        password = inputConfirmPass.value,
        onPasswordChange = {
            inputConfirmPass.value = it
        },
        label = "Confirm Password",
        modifier = Modifier.fillMaxWidth(),
        getUIStyle = getUIStyle
    )
    SubmitButton(onClick = {
        coroutineScope.launch {
            enabled.value = false
            if (inputPassword.value.length < 10) {
                showToastMessage(
                    context,
                    "You need 10 characters, you only have ${inputPassword.value.length}"
                )
                enabled.value = true
                return@launch
            } else if (inputPassword.value != inputConfirmPass.value) {
                showToastMessage(context, "Passwords don't match")
                enabled.value = true
                return@launch
            } else if (!isValidPassword(inputPassword.value)) {
                showToastMessage(context, "Not a valid password")
                enabled.value = true
                return@launch
            }
            supabaseVM.signUpWithEmail(inputEmail.value, inputPassword.value).let {
                if (it != "yay") {
                    showToastMessage(context, it)
                } else {
                    show.value = true
                }
            }
            enabled.value = true
        }
    }, enabled.value, getUIStyle, "Sign up with email")
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Already have an account?",
            fontSize = 13.sp,
        )
        Text(
            text = "Sign in",
            color = Color.Red,
            modifier = Modifier
                .clickable {
                    if (enabled.value) {
                        signIn.value = true
                    }
                },
            fontSize = 13.sp
        )
    }
    if (show.value) {
        Text(
            "A confirmation was sent to ${inputEmail.value}\n " +
                    "Please confirm your email"
        )
    }

}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
private fun SignInWithEmail(
    inputEmail: MutableState<String>, inputPassword: MutableState<String>,
    enabled: MutableState<Boolean>, coroutineScope: CoroutineScope, getUIStyle: GetUIStyle,
    supabaseVM: SupabaseViewModel, context: Context, signIn: MutableState<Boolean>,
    onNavigate: () -> Unit, onForgotPassword: () -> Unit
) {
    var errorMessage by rememberSaveable { mutableStateOf("") }
    val success = stringResource(R.string.signed_in)
    EditTextField(
        value = inputEmail.value,
        onValueChanged = {
            inputEmail.value = it
        },
        labelStr = "Email",
        modifier = Modifier.fillMaxWidth(),
    )
    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PasswordTextField(
            password = inputPassword.value,
            onPasswordChange = {
                inputPassword.value = it
            },
            label = "Password",
            modifier = Modifier.fillMaxWidth(),
            getUIStyle = getUIStyle
        )
        Text(
            text = "Forgot password",
            color = Color.Red,
            modifier = Modifier
                .padding(4.dp)
                .clickable {
                    if (enabled.value) {
                        onForgotPassword()
                    }
                }
                .align(Alignment.End),
            fontSize = 13.sp
        )
    }
    Spacer(Modifier.padding(vertical = 20.dp))
    SubmitButton(
        onClick = {
            coroutineScope.launch {
                enabled.value = false
                supabaseVM.signInWithEmail(
                    inputEmail.value, inputPassword.value
                ).let {
                    if (it == "yay") {
                        showToastMessage(context, success)
                        onNavigate()
                    } else {
                        showToastMessage(context, it)
                        errorMessage = it
                    }
                }
                enabled.value = true
            }
        }, enabled.value, getUIStyle, "Sign in with email"
    )
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Don't have have an account?",
            fontSize = 13.sp
        )
        Text(
            text = "Sign up",
            color = Color.Red,
            modifier = Modifier
                .clickable {
                    if (enabled.value) {
                        signIn.value = false
                    }
                },
            fontSize = 13.sp
        )
    }
    if (errorMessage.isNotBlank()) {
        Text(errorMessage)
    }

}

private fun isValidPassword(password: String): Boolean {
    val hasLowercase = password.any { it.isLowerCase() }
    val hasUppercase = password.any { it.isUpperCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSymbol = password.any { !it.isLetterOrDigit() }

    return hasLowercase && hasUppercase && hasDigit && hasSymbol
}