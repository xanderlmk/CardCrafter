package com.belmontCrest.cardCrafter.uiFunctions

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle

class ContentIcons(private val getUIStyle: GetUIStyle) {
    @Composable
    fun ContentIcon(cd: String, icon: ImageVector, modifier: Modifier) {
        Icon(
            imageVector = icon,
            modifier = modifier,
            contentDescription = cd,
            tint = if (getUIStyle.getIsCuteTheme()) {
                getUIStyle.defaultIconColor()
            } else {
                getUIStyle.iconColor()
            }
        )
    }

    @Composable
    fun ContentIcon(cd: String, icon: Painter, modifier: Modifier) {
        Icon(
            painter = icon,
            modifier = modifier,
            contentDescription = cd,
            tint = if (getUIStyle.getIsCuteTheme()) {
                getUIStyle.defaultIconColor()
            } else {
                getUIStyle.iconColor()
            }
        )
    }
}