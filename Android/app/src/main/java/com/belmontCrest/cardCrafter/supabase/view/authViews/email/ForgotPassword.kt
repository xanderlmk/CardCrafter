package com.belmontCrest.cardCrafter.supabase.view.authViews.email

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.belmontCrest.cardCrafter.model.application.AppVMProvider
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.ForgotPasswordViewModel
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.uiFunctions.CustomText
import com.belmontCrest.cardCrafter.uiFunctions.EditTextField
import com.belmontCrest.cardCrafter.uiFunctions.buttons.SubmitButton
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ForgotPassword(getUIStyle: GetUIStyle) {
    var inputEmail by rememberSaveable { mutableStateOf("") }
    var enabled by rememberSaveable { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val fpVM: ForgotPasswordViewModel = viewModel(factory = AppVMProvider.Factory)
    val context = LocalContext.current
    Column(
        modifier = Modifier.boxViewsModifier(getUIStyle.getColorScheme()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomText(
            "Please enter you email", getUIStyle, Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )
        EditTextField(
            value = inputEmail, onValueChanged = { inputEmail = it },
            labelStr = "Email", Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )
        SubmitButton(onClick = {
            coroutineScope.launch {
                enabled = false
                val result = fpVM.forgotPassword(inputEmail)
                if (!result) {
                    showToastMessage(context, "Failed to send email.")
                } else {
                    showToastMessage(context, "Sent, please check your email.")
                }
                enabled = true
            }
        }, enabled, getUIStyle, "Send email")
    }
}