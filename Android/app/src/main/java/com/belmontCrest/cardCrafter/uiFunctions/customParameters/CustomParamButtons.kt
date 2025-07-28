package com.belmontCrest.cardCrafter.uiFunctions.customParameters

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.AnswerParam
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.MiddleParam
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.Param

@Composable
fun MiddleParamType(onDismiss: () -> Unit, onClick: (MiddleParam) -> Unit, expanded: Boolean) {
    DropdownMenu(expanded = expanded, onDismissRequest = { onDismiss() }) {
        ParamTypeMenuItems { onClick(MiddleParam.WithParam(it)) }
        DropdownMenuItem(
            onClick = { onClick(MiddleParam.Hint("")) },
            text = { Text(stringResource(R.string.hint)) }
        )
        DropdownMenuItem(
            onClick = { onClick(MiddleParam.Choice(listOf("", ""), correct = '-')) },
            text = { Text("Choices") }
        )
        DropdownMenuItem(
            onClick = {
                onClick(
                    MiddleParam.WithParam(Param.Pair(Param.Type.String(""), Param.Type.String("")))
                )
            },
            text = { Text("Pair") }
        )
        DropdownMenuItem(
            onClick = { onClick(MiddleParam.Empty) },
            text = { Text("Empty") }
        )
    }
}

@Composable
fun AnswerParamType(onDismiss: () -> Unit, onClick: (AnswerParam) -> Unit, expanded: Boolean) {
    DropdownMenu(expanded = expanded, onDismissRequest = { onDismiss() }) {
        ParamTypeMenuItems { onClick(AnswerParam.WithParam(it)) }
        DropdownMenuItem(
            onClick = { onClick(AnswerParam.NotationList(listOf(""), "")) },
            text = { Text("Notation List") }
        )
        DropdownMenuItem(
            onClick = {
                onClick(
                    AnswerParam.WithParam(Param.Pair(Param.Type.String(""), Param.Type.String("")))
                )
            },
            text = { Text("Pair") }
        )
    }
}

@Composable
fun ParamType(onDismiss: () -> Unit, onClick: (Param) -> Unit, expanded: Boolean) {
    DropdownMenu(expanded = expanded, onDismissRequest = { onDismiss() }) {
        ParamTypeMenuItems(onClick)
        DropdownMenuItem(
            onClick = { onClick(Param.Pair(Param.Type.String(""), Param.Type.String(""))) },
            text = { Text("Pair") }
        )
    }
}

@Composable
fun PairedParamType(onDismiss: () -> Unit, onClick: (Param.Type) -> Unit, expanded: Boolean) {
    DropdownMenu(expanded = expanded, onDismissRequest = { onDismiss() }) {
        ParamTypeMenuItems(onClick)
    }
}

@Composable
private fun ParamTypeMenuItems(onClick: (Param.Type) -> Unit) {
    DropdownMenuItem(
        onClick = { onClick(Param.Type.String("")) },
        text = { Text("Default") }
    )
    DropdownMenuItem(
        onClick = { onClick(Param.Type.StringList(listOf(""))) },
        text = { Text("String list") }
    )
    DropdownMenuItem(
        onClick = { onClick(Param.Type.Notation("")) },
        text = { Text("Notation") }
    )
    DropdownMenuItem(
        onClick = { onClick(Param.Type.Image("")) },
        text = { Text("Image") }
    )
    DropdownMenuItem(
        onClick = { onClick(Param.Type.Audio("")) },
        text = { Text("Audio") }
    )
}