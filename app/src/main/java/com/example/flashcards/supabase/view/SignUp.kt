package com.example.flashcards.supabase.view

import android.credentials.GetCredentialException
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.flashcards.supabase.controller.SupabaseViewModel
import com.example.flashcards.ui.theme.GetModifier
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID
import com.example.flashcards.R
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun SignUp(
    supabase: SupabaseClient,
    supabaseVM: SupabaseViewModel,
    getModifier: GetModifier
) {

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = getModifier.boxViewsModifier()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(
                text = "You must use Google to Sign In/Sign Up",
                color = getModifier.titleColor(),
                modifier = Modifier.fillMaxWidth(),
                fontSize = 22.sp
            )
            GoogleSignInButton(supabase, supabaseVM)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun GoogleSignInButton(
    supabase: SupabaseClient,
    supabaseVM: SupabaseViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val googleClientId = stringResource(R.string.GOOGLE_CLIENTID).toString()

    val onClick: () -> Unit = {
        val credentialManager = CredentialManager.create(context)

        // Generate a nonce and hash it with sha-256
        // Providing a nonce is optional but recommended
        val rawNonce = UUID.randomUUID()
            .toString() // Generate a random String. UUID should be sufficient, but can also be any other random string.
        val bytes = rawNonce.toString().toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)

        /** Hashed nonce to be passed to Google sign-in */
        val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(googleClientId)
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
                    supabase = supabase,
                    googleIdToken = googleIdToken,
                    rawNonce = rawNonce
                )
                Toast.makeText(context, "You are Signed in", Toast.LENGTH_SHORT).show()
            } catch (e: GetCredentialException) {
                Log.i("GOOGLE SIGN IN", "$e")
            } catch (e: GoogleIdTokenParsingException) {
                Log.i("GOOGLE SIGN IN", "$e")
            } catch (e: RestException) {
                Log.i("GOOGLE SIGN IN", "$e")
            } catch (e: Exception) {
                Log.i("GOOGLE SIGN IN", "$e")
            }
        }
    }

    Button(
        onClick = onClick,
    ) {
        Text("Sign in with Google")
    }
}