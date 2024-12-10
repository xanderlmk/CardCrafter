package com.example.flashcards.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.flashcards.R
import com.example.flashcards.ui.theme.borderColor
import com.example.flashcards.ui.theme.iconColor
import com.example.flashcards.ui.theme.titleColor
import kotlinx.coroutines.launch

@Composable
fun SmallAddButton(
    onClick:() -> Unit,
    iconSize: Int = 45
) {
    FloatingActionButton(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .padding(16.dp)

    ) {
        Icon(
            Icons.Outlined.Add,
            "Add Deck",
            modifier = Modifier.size(iconSize.dp),
            tint = borderColor,
        )
    }
}

@Composable
fun AddCardButton (
    onClick:() -> Unit
){
    ExtendedFloatingActionButton(
        onClick = { onClick()},
        modifier = Modifier
            .padding(16.dp)
    ) {
        Icon(Icons.Filled.Add, "Add Card")
        Text(text = stringResource(R.string.add_card))
    }

}


@Composable
fun BackButton(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = titleColor
) {
    val coroutineScope = rememberCoroutineScope()
    IconButton(
        onClick = {
            coroutineScope.launch {
                delayNavigate()
                onBackClick()
            }
        },
        modifier = modifier
            .background(color= backgroundColor, shape = RoundedCornerShape(16.dp))
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            modifier = Modifier
                .size(36.dp),
            contentDescription = "Back",
            tint = iconColor

        )
    }
}