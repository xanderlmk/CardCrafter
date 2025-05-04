package com.belmontCrest.cardCrafter.model

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun returnFontSizeBasedOnDp(): TextUnit {
    val config = LocalConfiguration.current
    val widthDp = config.screenWidthDp
    return when (widthDp) {
        in 0..199 -> {
            10.sp
        }
        in 200..300 -> {
            12.sp
        }
        in 301..400 -> {
            14.sp
        }
        else -> {
            TextUnit.Unspecified
        }
    }
}

@Composable
fun Modifier.paddingForModal(): Modifier {
    val config = LocalConfiguration.current
    val widthDp = config.screenWidthDp
    return when (widthDp) {
        in 0..199 -> {
            this.padding(top = 12.dp, bottom = 6.dp, start = 4.dp, end = 4.dp)
        }
        in 200..300 -> {
            this.padding(top = 15.dp, bottom = 6.dp, start = 6.dp, end = 6.dp)
        }
        in 301..400 -> {
            this.padding(top = 15.dp, bottom = 6.dp, start = 8.dp, end = 8.dp)
        }
        else -> {
            this.padding(top = 15.dp, bottom = 6.dp, start = 15.dp, end = 15.dp)
        }
    }
}