package com.belmontCrest.cardCrafter.model


import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp


/** Font size prop */
enum class FSProp { Font22, Font20, Font18, Font16, Font14, Default }
/** Font weight prop */
enum class FWProp { SemiBold, Bold, Default }
/** Text align prop */
enum class TAProp  { Start, Center, End, Default }
/** Max lines prop */
enum class MLProp { One, Two, Three, Default }

/** Defining our text props */
data class TextProps (
    /** Font size */
    val fs : FSProp = FSProp.Default,
    /** Font weight */
    val fw: FWProp = FWProp.Default,
    /** Text align */
    val ta: TAProp = TAProp.Default,
    val ml: MLProp = MLProp.Default
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
    FSProp.Default -> TextProps(fs = FSProp.Default)
}

fun setFontSize(fsProp: FSProp): TextUnit {
    return when(fsProp) {
        FSProp.Font22  -> 22.sp
        FSProp.Font20  -> 20.sp
        FSProp.Font18 -> 18.sp
        FSProp.Font16  -> 16.sp
        FSProp.Font14  -> 14.sp
        FSProp.Default -> TextUnit.Unspecified
    }
}

fun setFontWeight(fwProp: FWProp): FontWeight {
    return when (fwProp) {
        FWProp.Bold -> FontWeight.Bold
        FWProp.SemiBold -> FontWeight.SemiBold
        FWProp.Default -> FontWeight.Normal
    }
}

fun setTextAlign(taProp: TAProp): TextAlign? {
    return when(taProp) {
        TAProp.Center -> TextAlign.Center
        TAProp.Start -> TextAlign.Start
        TAProp.End -> TextAlign.End
        TAProp.Default -> null
    }
}

fun setMaxLines(mlProp: MLProp): Int {
    return when(mlProp) {
        MLProp.One -> 1
        MLProp.Two -> 2
        MLProp.Three -> 3
        MLProp.Default -> Int.MAX_VALUE
    }
}