package com.belmontCrest.cardCrafter.uiFunctions.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.model.ui.Fields
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.model.daoHelpers.Order
import com.belmontCrest.cardCrafter.model.TAProp
import com.belmontCrest.cardCrafter.model.TCProp
import com.belmontCrest.cardCrafter.model.TextProps
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.uiFunctions.ContentIcons
import com.belmontCrest.cardCrafter.uiFunctions.CustomText

@Composable
fun OrderByDropdown(getUIStyle: GetUIStyle, navVM: NavViewModel) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val ci = ContentIcons(getUIStyle)
    val direction by navVM.direction.collectAsStateWithLifecycle()
    Box(
        Modifier
            .wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier
                .padding(4.dp)
                .size(54.dp)
        ) {
            ci.ContentIcon(Icons.Default.MoreVert, "Card Type")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {

            CustomText(
                "Order By", getUIStyle, Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                props = TextProps(ta = TAProp.Center, tc = TCProp.Basic)
            )
            HorizontalDivider()
            DropdownMenuItem(
                onClick = { navVM.reverseOrder() },
                text = { Text(if (direction) "Ascending" else "Descending") })
            HorizontalDivider()
            DropdownMenuItem(
                onClick = { navVM.updateOrder(Order.Name) },
                text = { Text("Name") }
            )
            DropdownMenuItem(
                onClick = { navVM.updateOrder(Order.CreatedOn) },
                text = { Text("Created Date") }
            )
            DropdownMenuItem(
                onClick = { navVM.updateOrder(Order.CardsLeft) },
                text = { Text("Cards Left") }
            )
        }
    }
}

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
    val ci = ContentIcons(getUIStyle)
    IconButton(
        onClick = {
            onRedoClick()
        },
        modifier = modifier
    ) {
        ci.ContentIcon(painterResource(R.drawable.return_arrow), "Redo", Modifier.size(22.dp))
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
        ci.ContentIcon(Icons.Filled.Edit, "Settings", Modifier.size(24.dp))
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
    innerModifier: Modifier = Modifier, textColor: Color = Color.Unspecified,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = getUIStyle.secondaryButtonColor(),
        contentColor = getUIStyle.buttonTextColor()
    )
) {
    Button(
        onClick = {
            onClick()
        }, enabled = enabled,
        colors = colors, modifier = modifier
    ) {
        Text(text = string, fontSize = fontSize, modifier = innerModifier, color = textColor)
    }
}

@Composable
fun MailButton(
    onClick: () -> Unit, getUIStyle: GetUIStyle
) {
    val ci = ContentIcons(getUIStyle)
    IconButton(onClick = { onClick() }) {
        ci.ContentIcon(Icons.Default.MailOutline, "mail", Modifier)
    }
}