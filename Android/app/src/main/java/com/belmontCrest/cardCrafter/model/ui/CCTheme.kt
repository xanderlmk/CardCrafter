package com.belmontCrest.cardCrafter.model.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val THEME = "cc_theme"

@Serializable
@SerialName(THEME)
@Parcelize
sealed class CCTheme : Parcelable {
    @Serializable
    @SerialName("$THEME.default")
    @Parcelize
    data object Default : CCTheme()

    @Serializable
    @SerialName("$THEME.light")
    @Parcelize
    data class Light(@SerialName("$THEME.color") val type: ThemeColoring) : CCTheme()

    @Serializable
    @SerialName("$THEME.dark")
    @Parcelize
    data class Dark(@SerialName("$THEME.color") val type: ThemeColoring) : CCTheme()

    fun isDark() = this is Dark
    fun isDarkAndCute() = this is Dark && this.type is ThemeColoring.Cute
    fun isDarkAndOriginal() = this is Dark && this.type is ThemeColoring.Original
    fun isDarkAndDynamic() = this is Dark && this.type is ThemeColoring.Dynamic

    fun isLight() = this is Light
    fun isLightAndCute() = this is Light && this.type is ThemeColoring.Cute
    fun isLightAndOriginal() = this is Light && this.type is ThemeColoring.Original
    fun isLightAndDynamic() = this is Light && this.type is ThemeColoring.Dynamic

    fun isSystem() = this is Default
    fun isCute() = this.isLightAndCute() || this.isDarkAndCute()
    fun isDynamic() = this.isLightAndDynamic() || this.isDarkAndDynamic()
    fun isOriginal() = this.isLightAndOriginal() || this.isDarkAndOriginal()

    fun toDarkTheme() = when (this) {
        is Dark -> this
        is Default -> Dark(ThemeColoring.Dynamic)
        is Light -> Dark(this.type)
    }

    fun toLightTheme() = when (this) {
        is Dark -> Light(this.type)
        is Default -> Light(ThemeColoring.Dynamic)
        is Light -> this
    }

    fun toCuteTheme(systemIsDark: Boolean) = when (this) {
        is Dark -> Dark(ThemeColoring.Cute)
        is Default -> if (systemIsDark) Dark(ThemeColoring.Cute) else Light(ThemeColoring.Cute)
        is Light -> Light(ThemeColoring.Cute)
    }

    fun toOriginalTheme(systemIsDark: Boolean) = when (this) {
        is Dark -> Dark(ThemeColoring.Original)
        is Default -> if (systemIsDark) Dark(ThemeColoring.Original)
        else Light(ThemeColoring.Original)

        is Light -> Light(ThemeColoring.Original)
    }

    fun toDynamicTheme(systemIsDark: Boolean) = when (this) {
        is Dark -> Dark(ThemeColoring.Dynamic)
        is Default -> if (systemIsDark) Dark(ThemeColoring.Dynamic)
        else Light(ThemeColoring.Dynamic)

        is Light -> Light(ThemeColoring.Dynamic)
    }
}

@Serializable
@SerialName("$THEME.color")
@Parcelize
sealed class ThemeColoring : Parcelable {
    @Serializable
    @SerialName("$THEME.color.dynamic")
    @Parcelize
    data object Dynamic : ThemeColoring()

    @Serializable
    @SerialName("$THEME.color.cute")
    @Parcelize
    data object Cute : ThemeColoring()

    @Serializable
    @SerialName("$THEME.color.original")
    @Parcelize
    data object Original : ThemeColoring()
}