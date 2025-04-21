package com.belmontCrest.cardCrafter.supabase.view.authViews

import android.credentials.GetCredentialException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.uiFunctions.SubmitButton

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun SignUp(
    supabaseVM: SupabaseViewModel,
    getUIStyle: GetUIStyle,
    onUseEmail: () -> Unit,
    onRefresh: (Boolean) -> Unit
) {
    val clientId by supabaseVM.clientId.collectAsStateWithLifecycle()
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                Text(
                    text = "Use Google to Sign In/Sign Up",
                    color = getUIStyle.titleColor(),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp
                )
                GoogleSignInButton(supabaseVM, clientId, getUIStyle) {
                    onRefresh(it)
                }
            }
            Text(
                text = "Sign in with email",
                color = getUIStyle.titleColor(),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 22.sp
            )
            SubmitButton(onClick = {
                onUseEmail()
            }, enabled = true, getUIStyle, "Sign up/Sign up with email")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun GoogleSignInButton(
    supabaseVM: SupabaseViewModel,
    googleClientId: String,
    getUIStyle: GetUIStyle,
    onRefresh: (Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var enabled by rememberSaveable { mutableStateOf(true) }
    val signedIn = stringResource(R.string.signed_in)
    val couldNotSignIn = stringResource(R.string.could_not_sign_in)
    val onClick: () -> Unit = {
        if (googleClientId.isEmpty()) {
            showToastMessage(context, "No credentials available.")
        } else {
            enabled = false
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
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(googleClientId)
                .setAutoSelectEnabled(false)
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
                    val success = supabaseVM.signUpWithGoogle(
                        googleIdToken = googleIdToken,
                        rawNonce = rawNonce
                    )
                    enabled = true
                    if (success) {
                        showToastMessage(context, signedIn)
                        onRefresh(true)
                    } else {
                        showToastMessage(context, couldNotSignIn)
                    }
                } catch (e: GetCredentialException) {
                    showToastMessage(context, "$e")
                    Log.d("GOOGLE SIGN IN", "$e")
                    enabled = true
                } catch (e: GoogleIdTokenParsingException) {
                    showToastMessage(context, "$e")
                    Log.d("GOOGLE SIGN IN", "$e")
                    enabled = true
                } catch (e: RestException) {
                    showToastMessage(context, "$e")
                    Log.d("GOOGLE SIGN IN", "$e")
                    enabled = true
                } catch (e: Exception) {
                    showToastMessage(context, "$e")
                    Log.d("GOOGLE SIGN IN", "$e")
                    enabled = true
                }
            }
        }
    }
    SubmitButton(
        onClick = { onClick() }, enabled,
        getUIStyle, "Sign in with Google"
    )
}