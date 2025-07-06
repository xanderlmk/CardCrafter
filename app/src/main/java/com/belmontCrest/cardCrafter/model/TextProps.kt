@file:Suppress("unused")

package com.belmontCrest.cardCrafter.model


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle


/** Font size prop */
enum class FSProp { Font22, Font20, Font18, Font16, Font14, Font12, Font10, Default }

/** Font weight prop */
enum class FWProp { SemiBold, Bold, Default }

/** Text align prop */
enum class TAProp { Start, Center, End, Default }

/** Max lines prop */
enum class MLProp { One, Two, Three, Default }

enum class TCProp { Button, Default, Disabled, Basic }

enum class TSProp { MediumTitle, LargeTitle, Body, Footer }

/** Defining our text props */
data class TextProps(
    /** Font size */
    val fs: FSProp = FSProp.Default,
    /** Font weight */
    val fw: FWProp = FWProp.Default,
    /** Text align */
    val ta: TAProp = TAProp.Default,
    /** Max lines */
    val ml: MLProp = MLProp.Default,
    /** Text Colors */
    val tc: TCProp = TCProp.Default,
    /** Text Style */
    val ts: TSProp = TSProp.Body
)

fun TAProp.toTextProp(): TextProps = when (this) {
    TAProp.Center -> TextProps(ta = TAProp.Center)
    TAProp.Start -> TextProps(ta = TAProp.Start)
    TAProp.End -> TextProps(ta = TAProp.End)
    TAProp.Default -> TextProps()
}

fun MLProp.toTextProp(): TextProps = when (this) {
    MLProp.One -> TextProps(ml = MLProp.One)
    MLProp.Two -> TextProps(ml = MLProp.Two)
    MLProp.Three -> TextProps(ml = MLProp.Three)
    MLProp.Default -> TextProps(ml = MLProp.Default)
}

fun FSProp.toTextProp(): TextProps = when (this) {
    FSProp.Font22 -> TextProps(fs = FSProp.Font22)
    FSProp.Font20 -> TextProps(fs = FSProp.Font20)
    FSProp.Font18 -> TextProps(fs = FSProp.Font18)
    FSProp.Font16 -> TextProps(fs = FSProp.Font16)
    FSProp.Font14 -> TextProps(fs = FSProp.Font14)
    FSProp.Font12 -> TextProps(fs = FSProp.Font12)
    FSProp.Font10 -> TextProps(fs = FSProp.Font10)
    FSProp.Default -> TextProps(fs = FSProp.Default)
}

fun TextProps.setFontSize(): TextUnit = when (this.fs) {
    FSProp.Font22 -> 22.sp
    FSProp.Font20 -> 20.sp
    FSProp.Font18 -> 18.sp
    FSProp.Font16 -> 16.sp
    FSProp.Font14 -> 14.sp
    FSProp.Font12 -> 12.sp
    FSProp.Font10 -> 10.sp
    FSProp.Default -> TextUnit.Unspecified
}

fun TextUnit.toTextProp(): TextProps = when(this) {
    10.sp -> TextProps(fs = FSProp.Font10)
    12.sp -> TextProps(fs = FSProp.Font12)
    14.sp -> TextProps(fs = FSProp.Font14)
    else -> TextProps(fs = FSProp.Default)
}

fun TextProps.setFontWeight(): FontWeight = when (this.fw) {
    FWProp.Bold -> FontWeight.Bold
    FWProp.SemiBold -> FontWeight.SemiBold
    FWProp.Default -> FontWeight.Normal
}


fun TextProps.setTextAlign(): TextAlign? = when (this.ta) {
    TAProp.Center -> TextAlign.Center
    TAProp.Start -> TextAlign.Start
    TAProp.End -> TextAlign.End
    TAProp.Default -> null
}


fun TextProps.setMaxLines(): Int = when (this.ml) {
    MLProp.One -> 1
    MLProp.Two -> 2
    MLProp.Three -> 3
    MLProp.Default -> Int.MAX_VALUE
}

fun TextProps.setTextColor(getUIStyle: GetUIStyle): Color = when (this.tc) {
    TCProp.Button -> getUIStyle.buttonTextColor()
    TCProp.Default -> getUIStyle.titleColor()
    TCProp.Disabled -> getUIStyle.disabledTextColor()
    TCProp.Basic -> getUIStyle.themedColor()
}

@Composable
fun TextProps.setTextStyle(): TextStyle = when (this.ts) {
    TSProp.LargeTitle -> MaterialTheme.typography.titleLarge
    TSProp.MediumTitle -> MaterialTheme.typography.titleMedium
    TSProp.Body -> MaterialTheme.typography.bodyLarge
    TSProp.Footer -> MaterialTheme.typography.labelMedium
}

fun titledTextProp() = TextProps(FSProp.Font22, FWProp.Default, TAProp.Center)

fun cardListTextProp() = TextProps(ml = MLProp.Two, tc = TCProp.Button)

fun FWProp.toTextProp(): TextProps = when (this) {
    FWProp.SemiBold -> TextProps(fw = FWProp.SemiBold)
    FWProp.Bold -> TextProps(fw = FWProp.Bold)
    FWProp.Default -> TextProps(fw = FWProp.Default)
}

fun TSProp.toTextProp(): TextProps = when (this) {
    TSProp.MediumTitle -> TextProps(ts = TSProp.MediumTitle)
    TSProp.LargeTitle -> TextProps(ts = TSProp.LargeTitle)
    TSProp.Body -> TextProps(ts = TSProp.Body)
    TSProp.Footer -> TextProps(ts = TSProp.Footer)
}