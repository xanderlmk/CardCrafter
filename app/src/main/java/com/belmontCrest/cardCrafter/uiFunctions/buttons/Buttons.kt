package com.belmontCrest.cardCrafter.uiFunctions.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.belmontCrest.cardCrafter.model.ui.Fields
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.uiFunctions.ContentIcons


@Composable
fun PullDeck(modifier: Modifier, getUIStyle: GetUIStyle, onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = {
            onClick()
        }, modifier = modifier
            .padding(8.dp),
        containerColor = getUIStyle.semiTransButtonColor(),
        contentColor = getUIStyle.titleColor(),
        elevation = FloatingActionButtonDefaults.elevation((Int.MAX_VALUE / 2).dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.merge),
            contentDescription = "Merge deck",
        )
        Text("Merge deck")
    }
}

@Composable
fun SmallAddButton(
    onClick: () -> Unit, iconSize: Int = 45,
    getUIStyle: GetUIStyle, modifier: Modifier
) {
    FloatingActionButton(
        onClick = {
            onClick()
        },
        modifier = modifier
            .padding(16.dp),
        containerColor = getUIStyle.buttonColor()
    ) {
        Icon(
            Icons.Outlined.Add,
            "Add Deck",
            modifier = Modifier.size(iconSize.dp),
            tint = getUIStyle.iconColor(),
        )
    }
}

@Composable
fun AddCardButton(
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        modifier = Modifier
            .padding(16.dp)
    ) {
        Icon(Icons.Outlined.Add, "Add Card")
        Text(text = stringResource(R.string.add_card))
    }

}

@Composable
fun ExportDeckButton(
    onClick: () -> Unit,
    modifier: Modifier
) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        modifier = modifier
            .padding(8.dp)
    ) {
        Icon(Icons.Default.AddCircle, "Export Deck")
        Text(text = "Export Deck")
    }
}

@Composable
fun BackButton(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    getUIStyle: GetUIStyle
) {
    IconButton(
        onClick = {
            onBackClick()
        },
        modifier = modifier
            .background(
                color = getUIStyle.buttonColor(),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(6.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
            modifier = Modifier
                .size(24.dp),
            contentDescription = "Back",
            tint = getUIStyle.iconColor()
        )
    }
}

@Composable
fun RedoCardButton(
    onRedoClick: () -> Unit,
    modifier: Modifier = Modifier,
    getUIStyle: GetUIStyle
) {
    IconButton(
        onClick = {
            onRedoClick()
        },
        modifier = modifier
    ) {
        val ci = ContentIcons(getUIStyle)
ci.ContentIcon()
        Icon(
            painter = painterResource(R.drawable.return_arrow),
            modifier = Modifier
                .size(22.dp),
            contentDescription = "Redo",
            tint = getUIStyle.iconColor()
        )
    }

}

@Composable
fun SettingsButton(
    onNavigateToEditDeck: () -> Unit,
    onNavigateToEditCards: () -> Unit,
    modifier: Modifier = Modifier,
    getUIStyle: GetUIStyle,
    fields: Fields
) {
    var expanded by remember { mutableStateOf(false) }
    val ci = ContentIcons(getUIStyle)
    IconButton(
        onClick = {
            if (!fields.inDeckClicked.value) {
                expanded = true
            }
        },
        modifier = modifier
            .background(
                color = getUIStyle.buttonColor(),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(6.dp)
    ) {
        ci.ContentIcon("Settings", Icons.Filled.Edit, Modifier.size(24.dp))
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    fields.mainClicked.value = true
                    onNavigateToEditDeck()
                },
                text = { Text(stringResource(R.string.edit_deck)) })
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    fields.mainClicked.value = true
                    onNavigateToEditCards()

                },
                text = { Text(stringResource(R.string.edit_flashcards)) })
        }
    }
}


@Composable
fun CancelButton(
    onClick: () -> Unit, enabled: Boolean, getUIStyle: GetUIStyle,
    modifier: Modifier = Modifier, fontSize: TextUnit = TextUnit.Unspecified
) {
    Button(
        onClick = {
            onClick()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = getUIStyle.secondaryButtonColor(),
            contentColor = getUIStyle.buttonTextColor()
        ),
        enabled = enabled, modifier = modifier
    ) { Text(text = stringResource(R.string.cancel), fontSize = fontSize) }
}

@Composable
fun SubmitButton(
    onClick: () -> Unit, enabled: Boolean,
    getUIStyle: GetUIStyle, string: String,
    modifier: Modifier = Modifier, fontSize: TextUnit = TextUnit.Unspecified,
    innerModifier: Modifier = Modifier
) {
    Button(
        onClick = {
            onClick()
        }, enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = getUIStyle.secondaryButtonColor(),
            contentColor = getUIStyle.buttonTextColor()
        ), modifier = modifier
    ) {
        Text(text = string, fontSize = fontSize, modifier = innerModifier)
    }
}

@Composable
fun MailButton(
    onClick: () -> Unit, getUIStyle: GetUIStyle
) {
    val ci = ContentIcons(getUIStyle)
    IconButton(onClick = { onClick() }) {
        ci.ContentIcon("mail", icon = Icons.Default.MailOutline, Modifier)
    }
}