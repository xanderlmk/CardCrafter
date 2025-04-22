package com.belmontCrest.cardCrafter.supabase.view.authViews

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.CancelButton
import com.belmontCrest.cardCrafter.uiFunctions.EditTextField
import com.belmontCrest.cardCrafter.uiFunctions.SubmitButton
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun EnterAccountDetails(
    inputUsername: String, inputFName: String, inputLName: String,
    getUIStyle: GetUIStyle, onExpanded: (Boolean) -> Unit,
    onUsername: (String) -> Unit, onFName: (String) -> Unit,
    onLName: (String) -> Unit, supabaseVM: SupabaseViewModel,
    onEnabled: (Boolean) -> Unit = {}
) {
    var enabled by rememberSaveable { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val fillOutfields = stringResource(R.string.fill_out_all_fields)
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Enter a username",
            color = getUIStyle.titleColor(),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        EditTextField(
            value = inputUsername,
            onValueChanged = {
                onUsername(it)
            },
            labelStr = "Username",
            Modifier.padding(horizontal = 8.dp)
        )
        Text(
            text = "Enter your first name",
            color = getUIStyle.titleColor(),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        EditTextField(
            value = inputFName,
            onValueChanged = {
                onFName(it)
            },
            labelStr = "First name",
            Modifier.padding(horizontal = 8.dp)
        )
        Text(
            text = "Enter a username",
            color = getUIStyle.titleColor(),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        EditTextField(
            value = inputLName,
            onValueChanged = {
                onLName(it)
            },
            labelStr = "Last name",
            Modifier.padding(horizontal = 8.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            CancelButton(
                onClick = {
                    onExpanded(false)
                }, enabled, getUIStyle
            )
            SubmitButton(
                onClick = {
                    if (inputUsername.isBlank() || inputFName.isBlank() ||
                        inputLName.isBlank()
                    ) {
                        showToastMessage(context, fillOutfields)
                        return@SubmitButton
                    }
                    // Validate username
                    if (!usernameRegex.matches(inputUsername)) {
                        showToastMessage(
                            context,
                            "Username can only contain letters, numbers, underscores and dots"
                        )
                        return@SubmitButton
                    }
                    // Validate names
                    if (!nameRegex.matches(inputFName)) {
                        showToastMessage(
                            context,
                            "First name can only contain letters and spaces"
                        )
                        return@SubmitButton
                    }
                    if (!nameRegex.matches(inputLName)) {
                        showToastMessage(
                            context,
                            "Last name can only contain letters and spaces"
                        )
                        return@SubmitButton
                    }
                    // Format names (capitalize words)
                    val formattedFName = inputFName.capitalizeWords()
                    val formattedLName = inputLName.capitalizeWords()
                    coroutineScope.launch {
                        enabled = false
                        onEnabled(false)
                        val result = supabaseVM.createOwner(
                            username = inputUsername.lowercase(),
                            fName = formattedFName,
                            lName = formattedLName
                        )
                        if (result) {
                            supabaseVM.getOwner()
                            onExpanded(false)
                            onEnabled(true)
                            enabled = true
                        } else {
                            showToastMessage(context, "Failed!")
                            onEnabled(true)
                            enabled = true
                        }
                    }

                }, enabled, getUIStyle, "Enter"
            )
        }
    }
}

private val usernameRegex = Regex("^[A-Za-z0-9_.]+$")
private val nameRegex = Regex("^[A-Za-z ]+$")

private fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") {
        it.lowercase().replaceFirstChar { ch ->
            if (ch.isLowerCase()) ch.titlecase() else ch.toString()
        }
    }