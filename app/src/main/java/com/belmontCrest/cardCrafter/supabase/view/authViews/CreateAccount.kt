package com.belmontCrest.cardCrafter.supabase.view.authViews

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.supabase.view.EnterAccountDetails
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun CreateAccount(
    supabaseVM: SupabaseViewModel,
    dismiss: MutableState<Boolean>,
    getUIStyle: GetUIStyle
) {
    var inputUsername by rememberSaveable { mutableStateOf("") }
    var inputFName by rememberSaveable { mutableStateOf("") }
    var inputLName by rememberSaveable { mutableStateOf("") }
    var enabled by rememberSaveable { mutableStateOf(true) }

    if (dismiss.value) {
        Dialog(
            onDismissRequest = {
                if (enabled) {
                    dismiss.value = false
                }
            }
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize(0.9f)
                    .background(
                        color = getUIStyle.altBackground(),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Please create an account to export decks.",
                    color = getUIStyle.titleColor(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                EnterAccountDetails(
                    inputUsername = inputUsername, inputFName = inputFName,
                    inputLName = inputLName, getUIStyle = getUIStyle,
                    onExpanded = { dismiss.value = it },
                    onUsername = { inputUsername = it },
                    onFName = { inputFName = it },
                    onLName = { inputLName = it },
                    supabaseVM = supabaseVM,
                    onEnabled = { enabled = it }
                )
            }
        }
    }
}