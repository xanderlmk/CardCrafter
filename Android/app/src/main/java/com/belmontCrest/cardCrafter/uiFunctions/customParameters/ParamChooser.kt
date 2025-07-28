package com.belmontCrest.cardCrafter.uiFunctions.customParameters

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.AnswerParam
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.MiddleParam
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.Param
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.ContentIcons

@Composable
fun ParamChooser(
    expanded: Boolean, string: String, onExpanded: (Boolean) -> Unit,
    onClick: (Param) -> Unit, getUIStyle: GetUIStyle
) {
    val ci = ContentIcons(getUIStyle)

    Box(
        Modifier.fillMaxWidth()
    ) {
        Text(
            text = string,
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
            color = getUIStyle.titleColor(),
            modifier = Modifier.align(Alignment.Center)
        )
        Box(
            Modifier
                .wrapContentSize(Alignment.TopEnd)
                .align(Alignment.CenterEnd)
        ) {
            IconButton(
                onClick = { onExpanded(!expanded) }) {
                ci.ContentIcon(
                    if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown
                )
            }
            ParamType(
                onDismiss = { onExpanded(false) },
                expanded = expanded,
                onClick = { onClick(it) }
            )
        }
    }
}

@Composable
fun PairedParamChooser(
    expanded: Boolean, string: String, onExpanded: (Boolean) -> Unit,
    onClick: (Param.Type) -> Unit, getUIStyle: GetUIStyle
) {
    val ci = ContentIcons(getUIStyle)

    Box(
        Modifier.fillMaxWidth()
    ) {
        Text(
            text = string,
            fontSize = 23.sp,
            textAlign = TextAlign.Center,
            lineHeight = 26.sp,
            color = getUIStyle.titleColor(),
            modifier = Modifier.align(Alignment.Center)
        )
        Box(
            Modifier
                .wrapContentSize(Alignment.TopStart)
                .align(Alignment.CenterStart)
        ) {
            IconButton(
                onClick = { onExpanded(!expanded) }) {
                ci.ContentIcon(
                    if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown
                )
            }
            PairedParamType(
                onDismiss = { onExpanded(false) },
                expanded = expanded,
                onClick = { onClick(it) }
            )
        }
    }
}

@Composable
fun MiddleParamChooser(
    expanded: Boolean, string: String, onExpanded: (Boolean) -> Unit,
    onClick: (MiddleParam) -> Unit, getUIStyle: GetUIStyle
) {
    val ci = ContentIcons(getUIStyle)

    Box(
        Modifier.fillMaxWidth()
    ) {
        Text(
            text = string,
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
            color = getUIStyle.titleColor(),
            modifier = Modifier.align(Alignment.Center)
        )
        Box(
            Modifier
                .wrapContentSize(Alignment.TopEnd)
                .align(Alignment.CenterEnd)
        ) {
            IconButton(
                onClick = { onExpanded(!expanded) }) {
                ci.ContentIcon(
                    if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown
                )
            }
            MiddleParamType(
                onDismiss = { onExpanded(false) },
                expanded = expanded,
                onClick = { onClick(it) }
            )
        }
    }
}

@Composable
fun AnswerParamChooser(
    expanded: Boolean, string: String, onExpanded: (Boolean) -> Unit,
    onClick: (AnswerParam) -> Unit, getUIStyle: GetUIStyle
) {
    val ci = ContentIcons(getUIStyle)

    Box(
        Modifier.fillMaxWidth()
    ) {
        Text(
            text = string,
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
            color = getUIStyle.titleColor(),
            modifier = Modifier.align(Alignment.Center)
        )
        Box(
            Modifier
                .wrapContentSize(Alignment.TopEnd)
                .align(Alignment.CenterEnd)
        ) {
            IconButton(
                onClick = { onExpanded(!expanded) }) {
                ci.ContentIcon(
                    if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown
                )
            }
            AnswerParamType(
                onDismiss = { onExpanded(false) },
                expanded = expanded,
                onClick = { onClick(it) }
            )
        }
    }
}