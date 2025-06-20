package com.belmontCrest.cardCrafter.views.mainViews

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle

@Composable
fun SystemThemeOptions(
    toggleDynamicTheme: () -> Unit,
    toggleDarkTheme: () -> Unit,
    toggleCuteTheme: () -> Unit,
    customToggled: Painter,
    darkToggled: Painter,
    cuteToggled: Painter,
    isDynamicTheme: Boolean,
    getUIStyle: GetUIStyle
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Box(
        Modifier
            .fillMaxWidth()
            .padding(12.dp)

            .border(
                width = 2.dp,
                shape = RoundedCornerShape(12.dp),
                color = if (getUIStyle.getIsDarkTheme() == true) {
                    Color.Gray
                } else {
                    Color.Black
                }
            )
            .clickable { expanded = !expanded }
            .wrapContentSize(Alignment.TopCenter)
    ) {
        Text(
            text = stringResource(R.string.system_theme),
            fontSize = 20.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Bold,
            color = getUIStyle.titleColor(),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 4.dp)
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                onClick = toggleDynamicTheme,
                text = { Text(stringResource(R.string.custom_theme)) },
                leadingIcon = {
                    Icon(
                        painter = customToggled,
                        contentDescription = "Toggle Custom Theme",
                        tint = getUIStyle.isThemeOn()
                    )
                })
            DropdownMenuItem(
                onClick = toggleDarkTheme,
                text = { Text(stringResource(R.string.dark_theme)) },
                leadingIcon = {
                    Icon(
                        painter = darkToggled,
                        contentDescription = "Toggle Dynamic Theme",
                        tint = getUIStyle.isThemeOn()
                    )
                })
            DropdownMenuItem(
                onClick = toggleCuteTheme,
                enabled = !isDynamicTheme,
                text = { Text(stringResource(R.string.cute_theme)) },
                leadingIcon = {
                    Icon(
                        painter = cuteToggled,
                        contentDescription = "Toggle Cute Theme",
                        tint = getUIStyle.isThemeOn()
                    )
                })
        }

    }
}
