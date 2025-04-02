package com.belmontCrest.cardCrafter.supabase.view

import android.credentials.GetCredentialException
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.belmontCrest.cardCrafter.supabase.controller.SupabaseViewModel
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID
import com.belmontCrest.cardCrafter.controller.AppViewModelProvider
import com.belmontCrest.cardCrafter.supabase.controller.APIViewModel
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun SignUp(
    supabaseVM: SupabaseViewModel,
    getUIStyle: GetUIStyle
) {
    val apiVM : APIViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val clientId by apiVM.clientId.collectAsStateWithLifecycle()
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .boxViewsModifier(getUIStyle.getColorScheme())
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Text(
                text = "You must use Google to Sign In/Sign Up",
                color = getUIStyle.titleColor(),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 22.sp
            )
            GoogleSignInButton(supabaseVM, clientId)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun GoogleSignInButton(
    supabaseVM: SupabaseViewModel,
    googleClientId : String
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val onClick: () -> Unit = {
        val credentialManager = CredentialManager.create(context)
        // Generate a nonce and hash it with sha-256
        // Providing a nonce is optional but recommended
        val rawNonce = UUID.randomUUID()
            .toString()
        // Generate a random String. UUID should be sufficient,
        // but can also be any other random string.
        val bytes = rawNonce.toString().toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)

        /** Hashed nonce to be passed to Google sign-in */
        val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(googleClientId)
            .setAutoSelectEnabled(true)
            .setNonce(hashedNonce) // Provide the nonce if you have one
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context,
                )
                val googleIdTokenCredential = GoogleIdTokenCredential
                    .createFrom(result.credential.data)
                val googleIdToken = googleIdTokenCredential.idToken
                supabaseVM.signUpWithGoogle(
                    googleIdToken = googleIdToken,
                    rawNonce = rawNonce
                )
                Toast.makeText(context, "You are Signed in", Toast.LENGTH_SHORT).show()
            } catch (e: GetCredentialException) {
                Log.d("GOOGLE SIGN IN", "$e")
            } catch (e: GoogleIdTokenParsingException) {
                Log.d("GOOGLE SIGN IN", "$e")
            } catch (e: RestException) {
                Log.d("GOOGLE SIGN IN", "$e")
            } catch (e: Exception) {
                Log.d("GOOGLE SIGN IN", "$e")
            }
        }
    }

    Button(
        onClick = {onClick()},
    ) {
        Text("Sign in with Google")
    }
}