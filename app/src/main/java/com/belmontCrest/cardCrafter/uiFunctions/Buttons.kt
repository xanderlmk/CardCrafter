package com.belmontCrest.cardCrafter.uiFunctions

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.controller.onClickActions.DeleteCard
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.model.uiModels.Fields
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import kotlinx.coroutines.launch
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.navigation.NavViewModel
import com.belmontCrest.cardCrafter.navigation.destinations.UserEDDestination
import com.belmontCrest.cardCrafter.navigation.destinations.UserProfileDestination
import com.belmontCrest.cardCrafter.supabase.controller.viewModels.SupabaseViewModel
import com.belmontCrest.cardCrafter.views.miscFunctions.delayNavigate

@Composable
fun SmallAddButton(
    onClick: () -> Unit,
    iconSize: Int = 45,
    getUIStyle: GetUIStyle,
    modifier: Modifier
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
    val coroutineScope = rememberCoroutineScope()
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
        Icon(
            imageVector = Icons.Filled.Edit,
            modifier = Modifier
                .size(24.dp),
            contentDescription = "Settings",
            tint = getUIStyle.iconColor()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    coroutineScope.launch {
                        fields.mainClicked.value = true
                        delayNavigate()
                        onNavigateToEditDeck()
                    }
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
fun CardOptionsButton(
    navVM: NavViewModel,
    getUIStyle: GetUIStyle, card: Card,
    fields: Fields,
    expanded: MutableState<Boolean>,
    modifier: Modifier,
    onDelete: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val showDialog = remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(
            onClick = { expanded.value = true },
            modifier = Modifier
                .size(54.dp)
                .align(Alignment.TopEnd)
        ) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = "Card Type",
                tint = getUIStyle.titleColor()
            )
        }
        DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
            DropdownMenuItem(
                onClick = { fields.newType.value = "basic" },
                text = { Text(stringResource(R.string.basic_card)) })
            DropdownMenuItem(
                onClick = { fields.newType.value = "three" },
                text = { Text(stringResource(R.string.three_field_card)) })
            DropdownMenuItem(
                onClick = { fields.newType.value = "hint" },
                text = { Text(stringResource(R.string.hint_card)) })
            DropdownMenuItem(
                onClick = { fields.newType.value = "multi" },
                text = { Text(stringResource(R.string.multi_choice_card)) })
            DropdownMenuItem(
                onClick = { fields.newType.value = "notation" },
                text = { Text("Notation") }
            )
            HorizontalDivider()
            DropdownMenuItem(
                onClick = {
                    showDialog.value = true
                },
                text = { Text("Delete Card") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        modifier = Modifier
                            .size(28.dp),
                        contentDescription = "Delete Card",
                        tint = getUIStyle.iconColor()
                    )
                }
            )
        }
        DeleteCard(
            navVM, coroutineScope,
            card, fields, showDialog,
            onDelete, getUIStyle
        )
    }
}

@Composable
fun CardTypesButton(getUIStyle: GetUIStyle, navViewModel: NavViewModel) {
    var expanded by rememberSaveable { mutableStateOf(false) }
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
            Icon(
                Icons.Default.MoreVert,
                contentDescription = "Card Type",
                tint = getUIStyle.titleColor()
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                onClick = { navViewModel.updateType("basic") },
                text = { Text(stringResource(R.string.basic_card)) }
            )
            DropdownMenuItem(
                onClick = { navViewModel.updateType("three") },
                text = { Text(stringResource(R.string.three_field_card)) }
            )
            DropdownMenuItem(
                onClick = { navViewModel.updateType("hint") },
                text = { Text(stringResource(R.string.hint_card)) }
            )
            DropdownMenuItem(
                onClick = { navViewModel.updateType("multi") },
                text = { Text(stringResource(R.string.multi_choice_card)) }
            )
            DropdownMenuItem(
                onClick = { navViewModel.updateType("notation") },
                text = { Text("Notation") }
            )
        }
    }
}

@Composable
fun CancelButton(
    onClick: () -> Unit, enabled: Boolean, getUIStyle: GetUIStyle
) {
    Button(
        onClick = {
            onClick()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = getUIStyle.secondaryButtonColor(),
            contentColor = getUIStyle.buttonTextColor()
        ),
        enabled = enabled
    ) { Text(stringResource(R.string.cancel)) }
}

@Composable
fun SubmitButton(
    onClick: () -> Unit, enabled: Boolean,
    getUIStyle: GetUIStyle, string: String
) {
    Button(
        onClick = {
            onClick()
        },
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = getUIStyle.secondaryButtonColor(),
            contentColor = getUIStyle.buttonTextColor()
        )
    ) {
        Text(string)
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun EditProfileButton(
    getUIStyle: GetUIStyle,
    navViewModel: NavViewModel,
    supabaseVM: SupabaseViewModel
) {
    val sbNavController by navViewModel.sbNav.collectAsStateWithLifecycle()
    var expanded by rememberSaveable { mutableStateOf(false) }
    val owner by supabaseVM.owner.collectAsStateWithLifecycle()
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
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = "Profile",
                tint = getUIStyle.titleColor()
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile Settings",
                        tint = getUIStyle.titleColor()
                    )
                },
                onClick = {
                    navViewModel.updateRoute(UserProfileDestination.route)
                    sbNavController?.navigate(UserProfileDestination.route)
                },
                text = { Text("My profile") }
            )
            owner?.let {
                HorizontalDivider()
                DropdownMenuItem(
                    onClick = {
                        navViewModel.updateRoute(UserEDDestination.route)
                        sbNavController?.navigate(UserEDDestination.route)
                    },
                    text = { Text("Exported decks") }
                )
            }
        }
    }
}