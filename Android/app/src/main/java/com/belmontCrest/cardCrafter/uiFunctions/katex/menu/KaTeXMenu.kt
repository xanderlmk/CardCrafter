package com.belmontCrest.cardCrafter.uiFunctions.katex.menu

import android.annotation.SuppressLint
import android.os.Parcelable
import android.webkit.WebView
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.belmontCrest.cardCrafter.model.getIsLandScape
import com.belmontCrest.cardCrafter.model.getKatexMenuWidth
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt


@Parcelize
@Serializable
data class KaTeXMenu(val notation: String?, val sa: SelectedAnnotation) : Parcelable

@Parcelize
@Serializable
sealed class SelectedAnnotation : Parcelable {
    @Parcelize
    @Serializable
    data object Letter : SelectedAnnotation()

    @Parcelize
    @Serializable
    data object Accent : SelectedAnnotation()

    @Parcelize
    @Serializable
    data object EQ : SelectedAnnotation()

    @Parcelize
    @Serializable
    data object OP : SelectedAnnotation()

    @Parcelize
    @Serializable
    data object NORM : SelectedAnnotation()

    @Parcelize
    @Serializable
    sealed class CursorChange : SelectedAnnotation() {
        data object Forward : CursorChange()
        data object Backward : CursorChange()
        data object Inline : CursorChange()
        data object NewLine : CursorChange()
        data object Delete : CursorChange()
    }

    @Parcelize
    @Serializable
    data object Idle : SelectedAnnotation()
}


class IsInsideException :
    Exception("You cannot put a notation equation inside a notation equation.")

//private const val KATEX_MENU = "KatexMenu"

@OptIn(ExperimentalStdlibApi::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun KaTeXMenu(
    modifier: Modifier, offsetProvider: () -> Offset, onDismiss: () -> Unit,
    onOffset: (Offset) -> Unit, getUIStyle: GetUIStyle,// initialPos: Offset?,
    webView: WebView, scrollState: ScrollState, height: Int, width: Int,
    onCursorChange: (SelectedAnnotation.CursorChange) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val isLandscape = getIsLandScape()
    val expandedWidth = 150
    val offsetMod =
        if (!isLandscape) Modifier.offset(y = -(126.dp))
        else Modifier.offset(x = -(expandedWidth.dp))

    Box(
        modifier = modifier
            .offset { IntOffset(offsetProvider().x.roundToInt(), offsetProvider().y.roundToInt()) }
    ) {
        if (expanded) {
            Column(
                modifier = offsetMod
                    .width(expandedWidth.dp)
                    .height(126.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onOffset(dragAmount)
                        }
                    }
                    .background(getUIStyle.katexMenuHeaderColor(), RoundedCornerShape(12.dp))
                    .border(1.5.dp, getUIStyle.themedColor(), RoundedCornerShape(12.dp)),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier.size(30.dp),
                        onClick = { onCursorChange(SelectedAnnotation.CursorChange.Backward) },
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                    IconButton(
                        modifier = Modifier.size(30.dp),
                        onClick = { onCursorChange(SelectedAnnotation.CursorChange.Forward) },
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                    Text(
                        text = "delete",
                        modifier = Modifier
                            .clickable {
                                onCursorChange(SelectedAnnotation.CursorChange.Delete)
                            },
                        textAlign = TextAlign.End,
                        fontSize = 14.sp,
                        color = getUIStyle.titleColor()
                    )

                }
                HorizontalDivider(color = getUIStyle.themedColor())
                Text(
                    text = "Start Inline",
                    modifier = Modifier
                        .clickable {
                            onCursorChange(SelectedAnnotation.CursorChange.Inline)
                        }
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    color = getUIStyle.titleColor()
                )
                HorizontalDivider(color = getUIStyle.themedColor())
                Text(
                    text = "Start New Line",
                    modifier = Modifier
                        .clickable {
                            onCursorChange(SelectedAnnotation.CursorChange.NewLine)
                        }
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    color = getUIStyle.titleColor()
                )
            }
        }
        Box(
            modifier = Modifier
                .width(getKatexMenuWidth(width))
                .height(26.dp)
                .background(getUIStyle.katexMenuHeaderColor())
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        onOffset(dragAmount)
                    }
                }
                .border(1.5.dp, getUIStyle.themedColor())
        ) {
            Text(
                text = if (!expanded) "Expand" else "Minimize",
                modifier = Modifier
                    .padding(start = 4.dp)
                    .align(Alignment.TopStart)
                    .clickable { expanded = !expanded },
                textAlign = TextAlign.Start,
                fontSize = 16.sp,
                color = getUIStyle.titleColor()
            )
            Text(
                text = "Drag here",
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = getUIStyle.titleColor()
            )
            IconButton(
                onClick = { onDismiss() },
                modifier = Modifier.align(Alignment.TopEnd)
            ) { Icon(Icons.Filled.Close, contentDescription = null) }

        }
        AndroidView(
            factory = { webView },
            modifier = Modifier
                .width(getKatexMenuWidth(width))
                .padding(top = 26.dp)
                .height(height.dp)
                .background(getUIStyle.katexMenuBGColor())
                .border(1.5.dp, getUIStyle.themedColor())
                .verticalScroll(scrollState),
        )
    }

}

/**
 * Check whether the box is out of bounds
 * @param isLandScape Whether or not the device is in portrait or landscape mode
 * @param pos The initial global position offset relative to window
 * @param maxX The max X value of the screen
 * @param maxY The max Y value of the screen
 * @param currentY Current Y value of the offset in the Katex Menu.
 * @param currentX Current X value of the offset in the Katex Menu
 * @param midXPoint Midpoint of the width in the Katex Menu
 */
@Suppress("unused")
private fun isOutsideOfBounds(
    isLandScape: Boolean,
    pos: Offset, maxY: Float, maxX: Float,
    currentY: Float, currentX: Float,
    midXPoint: Float
): Boolean {
    return if (!isLandScape) {
        val bottom = -pos.y - 26.dp.value - (pos.y / 3)
        val top = maxY - (200.dp.value)
        val right = maxX - midXPoint
        val left = -(pos.x * 2) - 6.dp.value
        /*Log.i(
            KATEX_MENU,
            "PositionY: $currentY :: LowerBoundY: $bottom :: HigherBoundY: $top"
        )*/
        currentY >= top || // ✅
                currentY <= bottom || // ✅
                currentX >= right || // ✅
                currentX <= left // ✅

    } else {
        val bottom = -pos.y - (pos.y / 2)
        val top = maxY - (200.dp.value) - pos.y
        val right = maxX - pos.x
        val left = -(pos.x * 2) - 6.dp.value
        /*Log.i(
            KATEX_MENU,
            "PositionY: $currentY :: LowerBoundY: $bottom :: HigherBoundY: $top"
        )*/
        currentY >= top || // ✅
                currentY <= bottom || // ✅
                currentX >= right || // ✅
                currentX <= left // ✅
    }
}