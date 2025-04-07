package com.belmontCrest.cardCrafter.supabase.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.belmontCrest.cardCrafter.controller.AppViewModelProvider
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.UserProfileViewModel
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.scrollableBoxViewModifier

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MyProfile(getUIStyle: GetUIStyle) {
    val userProfileVM: UserProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val userProfile by userProfileVM.userProfile.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.scrollableBoxViewModifier(
            rememberScrollState(),
            getUIStyle.getColorScheme()
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(
                text = "Email:", textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                fontSize = 22.sp, color = getUIStyle.titleColor(),
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(.525f)
                    .align(
                        Alignment.Start
                    ), thickness = 2.5.dp
            )
            Text(
                text = userProfile?.user?.email ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp, vertical = 4.dp),
                fontSize = 20.sp, color = getUIStyle.titleColor()
            )
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(.525f)
                    .align(
                        Alignment.End
                    ), thickness = 2.5.dp
            )
        }
    }
}