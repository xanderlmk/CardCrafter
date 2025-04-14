package com.belmontCrest.cardCrafter.supabase.view.authViews.email

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.window.Dialog
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.supabase.view.showToastMessage
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.EditTextField
import com.belmontCrest.cardCrafter.uiFunctions.SubmitButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun EmailView(
    pressed: Boolean, onRefresh: (Boolean) -> Unit,
    supabaseVM: SupabaseViewModel, getUIStyle: GetUIStyle,
    onPress: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    if (pressed) {
        var inputEmail = rememberSaveable { mutableStateOf("") }
        var inputPassword = rememberSaveable { mutableStateOf("") }
        var inputConfirmPass = rememberSaveable { mutableStateOf("") }
        var enabled = rememberSaveable { mutableStateOf(true) }
        val coroutineScope = rememberCoroutineScope()
        var show = rememberSaveable { mutableStateOf(false) }
        var signIn = rememberSaveable { mutableStateOf(false) }
        Dialog(onDismissRequest = {
            if (enabled.value) {
                onPress(false)
                onRefresh(true)
            }
        }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.80f)
                    .background(
                        color = getUIStyle.altBackground(),
                        shape = RoundedCornerShape(20.dp)
                    )
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
                            getUIStyle = getUIStyle
                        ) {
                            if (!it) { onRefresh(true) }
                            onPress(it)
                        }
                    }
                }
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
    EditTextField(
        value = inputPassword.value,
        onValueChanged = {
            inputPassword.value = it
        },
        labelStr = "Password",
        modifier = Modifier.fillMaxWidth(),
    )
    EditTextField(
        value = inputConfirmPass.value,
        onValueChanged = {
            inputConfirmPass.value = it
        },
        labelStr = "Confirm Password",
        modifier = Modifier.fillMaxWidth(),
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
            }
            supabaseVM.signUpWithEmail(
                inputEmail.value, inputPassword.value
            ).let {
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
    onPress: (Boolean) -> Unit
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
    EditTextField(
        value = inputPassword.value,
        onValueChanged = {
            inputPassword.value = it
        },
        labelStr = "Password",
        modifier = Modifier.fillMaxWidth(),
    )
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
                        onPress(false)
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