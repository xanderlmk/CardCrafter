package com.belmontCrest.cardCrafter.uiFunctions

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle

@Composable
fun IsPartOfQOrA(getUIStyle: GetUIStyle, isQOrA: Boolean, onClick: () -> Unit) {
    val ci = ContentIcons(getUIStyle)
    Row(
        Modifier
            .padding(6.dp)
            .border(1.5.dp, getUIStyle.secondaryButtonColor(), RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isQOrA) {
            ci.ContentIcon(
                painterResource(R.drawable.toggle_on), "Toggle Q or A",
                Modifier.size(30.dp), getUIStyle.themedColor()
            )
            Text("Part of Question")
        } else {
            ci.ContentIcon(
                painterResource(R.drawable.toggle_off), "Toggle Q or A",
                Modifier.size(30.dp), getUIStyle.themedColor()
            )
            Text("Part of Answer")
        }
    }
}