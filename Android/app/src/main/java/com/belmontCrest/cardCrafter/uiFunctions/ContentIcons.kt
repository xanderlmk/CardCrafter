package com.belmontCrest.cardCrafter.uiFunctions

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle

@SuppressLint("ModifierParameter")
class ContentIcons(private val getUIStyle: GetUIStyle) {
    @Composable
    fun ContentIcon(icon: ImageVector, cd: String? = null, modifier: Modifier = Modifier) {
        Icon(
            imageVector = icon,
            modifier = modifier,
            contentDescription = cd,
            tint = if (getUIStyle.getIsCuteTheme()) {
                getUIStyle.themedColor()
            } else {
                getUIStyle.iconColor()
            }
        )
    }

    @Composable
    fun ContentIcon(icon: Painter, cd: String? = null, modifier: Modifier = Modifier) {
        Icon(
            painter = icon,
            modifier = modifier,
            contentDescription = cd,
            tint = if (getUIStyle.getIsCuteTheme()) {
                getUIStyle.themedColor()
            } else {
                getUIStyle.iconColor()
            }
        )
    }

    @Composable
    fun ContentIcon(icon: Painter, cd: String, modifier: Modifier, tint: Color) {
        Icon(
            painter = icon,
            modifier = modifier,
            contentDescription = cd,
            tint = tint
        )
    }

    @Composable
    fun DeleteIcon() {
        Icon(
            imageVector = Icons.Filled.Delete,
            modifier = Modifier
                .size(28.dp),
            contentDescription = "Delete icon",
            tint = getUIStyle.redColor()
        )
    }
}