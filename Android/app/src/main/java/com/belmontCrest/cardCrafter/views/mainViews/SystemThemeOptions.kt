package com.belmontCrest.cardCrafter.views.mainViews

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.model.FSProp
import com.belmontCrest.cardCrafter.model.TSProp
import com.belmontCrest.cardCrafter.model.TextProps
import com.belmontCrest.cardCrafter.model.ui.CCTheme
import com.belmontCrest.cardCrafter.ui.GetUIStyle
import com.belmontCrest.cardCrafter.ui.functions.ColumnContentWithBorder
import com.belmontCrest.cardCrafter.ui.functions.CustomText

@Composable
fun SystemThemeOptions(theme: CCTheme, onChangeTheme: (CCTheme) -> Unit, getUIStyle: GetUIStyle) {
    val isDark = isSystemInDarkTheme()
    ColumnContentWithBorder(getUIStyle, stringResource(R.string.system_theme)) {
        CustomText(
            text = stringResource(R.string.theme), getUIStyle,
            props = TextProps(ts = TSProp.MediumTitle, fs = FSProp.Font18)
        )
        CenteredAndEvenedRow(Modifier.fillMaxWidth()) {
            FilterChip(
                selected = theme.isDark(),
                label = {
                    Text(stringResource(R.string.dark_theme), color = getUIStyle.themedColor())
                },
                onClick = { onChangeTheme(theme.toDarkTheme()) }
            )
            FilterChip(
                selected = theme.isLight(),
                label = {
                    Text(stringResource(R.string.light_theme), color = getUIStyle.themedColor())
                },
                onClick = { onChangeTheme(theme.toLightTheme()) }
            )
            FilterChip(
                selected = theme.isSystem(),
                label = {
                    Text(stringResource(R.string.system_theme), color = getUIStyle.themedColor())
                },
                onClick = { onChangeTheme(CCTheme.Default) }
            )
        }
        CustomText(
            text = stringResource(R.string.coloring), getUIStyle,
            props = TextProps(ts = TSProp.MediumTitle, fs = FSProp.Font18)
        )
        CenteredAndEvenedRow(Modifier.fillMaxWidth()) {
            FilterChip(
                selected = theme.isCute(),
                label = {
                    Text(stringResource(R.string.cute), color = getUIStyle.themedColor())
                },
                onClick = { onChangeTheme(theme.toCuteTheme(isDark)) }
            )
            FilterChip(
                selected = theme.isDynamic(),
                label = {
                    Text(stringResource(R.string.dynamic), color = getUIStyle.themedColor())
                },
                onClick = { onChangeTheme(theme.toDynamicTheme(isDark)) }
            )
            FilterChip(
                selected = theme.isOriginal(),
                label = {
                    Text(stringResource(R.string.original), color = getUIStyle.themedColor())
                },
                onClick = { onChangeTheme(theme.toOriginalTheme(isDark)) }
            )
        }
    }
}

@Composable
private fun CenteredAndEvenedRow(modifier: Modifier, content: @Composable () -> Unit) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) { content() }
}
