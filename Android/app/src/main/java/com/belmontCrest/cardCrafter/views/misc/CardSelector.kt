package com.belmontCrest.cardCrafter.views.misc

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.views.misc.details.toQuestion

@Composable
fun CardSelector(
    allCTs: List<CT>, index: Int, getUIStyle: GetUIStyle, isSelecting: Boolean, selected: Boolean
) {
    if (isSelecting) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selected) {
                Icon(painterResource(R.drawable.filled_square), contentDescription = null)
                Text(
                    text = allCTs[index].toQuestion(),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = getUIStyle.buttonTextColor()
                )
            } else {
                Icon(painterResource(R.drawable.outlined_square), contentDescription = null)
                Text(
                    text = allCTs[index].toQuestion(),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = getUIStyle.buttonTextColor()
                )
            }
        }
    } else {
        Text(
            text = allCTs[index].toQuestion(),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            color = getUIStyle.buttonTextColor()
        )
    }
}
