package com.belmontCrest.cardCrafter.model

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("ConfigurationScreenWidthHeight")
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

@SuppressLint("ConfigurationScreenWidthHeight")
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

@Composable
fun getMaxWidth(): Dp {
    val config = LocalWindowInfo.current.containerSize
    return config.width.dp
}

@Composable
fun getMaxHeight(): Dp {
    val config = LocalWindowInfo.current.containerSize
    return config.height.dp
}

@Composable
fun getKatexMenuWidth(): Dp {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        val config = LocalWindowInfo.current.containerSize
        val width = config.width
        return when (width) {
            in 200..250 -> {
                100.dp
            }

            in 251..350 -> {
                150.dp
            }

            in 351..500 -> {
                250.dp
            }

            in 501..700 -> {
                300.dp
            }

            in 701..900 -> {
                400.dp
            }

            else -> {
                450.dp
            }
        }
    } else {
        val config = LocalWindowInfo.current.containerSize
        val width = config.width
        return when (width) {
            in 200..250 -> {
                200.dp
            }

            in 251..350 -> {
                250.dp
            }

            in 351..500 -> {
                350.dp
            }

            in 501..700 -> {
                500.dp
            }

            in 701..900 -> {
                700.dp
            }

            else -> {
                900.dp
            }
        }
    }
}

@Composable
fun getIsLandScape(): Boolean {
    return LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
}