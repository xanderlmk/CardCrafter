package com.belmontCrest.cardCrafter.supabase.view.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.belmontCrest.cardCrafter.controller.AppViewModelProvider
import com.belmontCrest.cardCrafter.navigation.destinations.SupabaseDestination
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.UserProfileViewModel
import com.belmontCrest.cardCrafter.supabase.view.authViews.EnterAccountDetails
import com.belmontCrest.cardCrafter.supabase.view.authViews.SignUp
import com.belmontCrest.cardCrafter.supabase.view.showToastMessage
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.boxViewsModifier
import com.belmontCrest.cardCrafter.ui.theme.scrollableBoxViewModifier
import com.belmontCrest.cardCrafter.uiFunctions.SubmitButton
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun MyProfile(
    getUIStyle: GetUIStyle, supabaseVM: SupabaseViewModel,
    startingRoute: String, onSignOut: () -> Unit
) {
    val userProfileVM: UserProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val userProfile by userProfileVM.userProfile.collectAsStateWithLifecycle()
    val isLoading by userProfileVM.isLoading.collectAsStateWithLifecycle()
    var enabled by rememberSaveable { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val currentUser by supabaseVM.currentUser.collectAsStateWithLifecycle()
    LaunchedEffect(currentUser) {
        if (currentUser != null && userProfile == null) {
            userProfileVM.getUserInfo()
        }
    }
    if (isLoading) {
        Box(
            modifier = Modifier.boxViewsModifier(getUIStyle.getColorScheme()),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = getUIStyle.titleColor())
        }
    } else {
        if (userProfile != null) {
            Box(
                modifier = Modifier.scrollableBoxViewModifier(
                    rememberScrollState(),
                    getUIStyle.getColorScheme()
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    ProfileText("Email:", userProfile?.user?.email ?: "", getUIStyle)
                    userProfile?.owner.let { owner ->
                        if (owner != null) {
                            ProfileText("Name:", owner.f_name + " " + owner.l_name, getUIStyle)
                            ProfileText("Username:", owner.username, getUIStyle)
                        } else {
                            var inputUsername by rememberSaveable { mutableStateOf("") }
                            var inputFName by rememberSaveable { mutableStateOf("") }
                            var inputLName by rememberSaveable { mutableStateOf("") }
                            var expanded by rememberSaveable { mutableStateOf(false) }
                            Text(
                                text = "Create account to export decks",
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 2.dp, vertical = 4.dp)
                                    .clickable {
                                        expanded = !expanded
                                    },
                                fontSize = 20.sp, color = getUIStyle.titleColor()
                            )
                            if (expanded) {
                                EnterAccountDetails(
                                    inputUsername = inputUsername, inputFName = inputFName,
                                    inputLName = inputLName, getUIStyle = getUIStyle,
                                    onExpanded = { expanded = it },
                                    onUsername = { inputUsername = it },
                                    onFName = { inputFName = it },
                                    onLName = { inputLName = it },
                                    supabaseVM = supabaseVM
                                )
                            }
                        }
                    }
                    Spacer(Modifier.padding(10.dp))
                    SubmitButton(
                        onClick = {
                            coroutineScope.launch {
                                enabled = false
                                userProfileVM.signOut().let {
                                    if (!it) {
                                        showToastMessage(context, "Could not sign out.")
                                    } else {
                                        supabaseVM.updateStatus()
                                        userProfileVM.getUserInfo()
                                        supabaseVM.getOwner()
                                        if (startingRoute == SupabaseDestination.route) {
                                            onSignOut()
                                        }
                                    }
                                    enabled = true
                                }
                            }
                        }, enabled, getUIStyle, "Sign Out"
                    )
                }
            }
        } else {
            SignUp(supabaseVM, getUIStyle) {

            }
        }
    }
}