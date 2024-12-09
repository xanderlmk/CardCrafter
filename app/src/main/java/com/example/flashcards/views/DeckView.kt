package com.example.flashcards.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcards.controller.MainViewModel
import com.example.flashcards.model.Deck
import com.example.flashcards.ui.theme.titleColor
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.InspectableModifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.flashcards.controller.AppViewModelProvider
import com.example.flashcards.controller.CardViewModel
import com.example.flashcards.ui.theme.backgroundColor
import com.example.flashcards.ui.theme.buttonColor
import com.example.flashcards.ui.theme.textColor
import kotlinx.coroutines.launch


class DeckView(private var mainViewModel: MainViewModel,
    var navController: NavController) {


    @Composable
    fun ViewEditDeck(deck: Deck, onNavigate: () -> Unit, whichView : View) {
        val cardViewModel: CardViewModel = viewModel(factory = AppViewModelProvider.Factory)
        val cardUiState by cardViewModel.cardUiState.collectAsState()
        val view = remember {whichView}
        val choosingView = ChoosingView(navController)

        val coroutineScope = rememberCoroutineScope()
        val presetModifier = Modifier
            .padding(top = 16.dp,start = 16.dp, end = 16.dp)
            .size(54.dp)

        when (view.whichView.intValue) {
            0 -> {
                view.onView.value = false
            }
            1 -> {
                choosingView.WhichScreen(deck,view)
            }
            2 -> {
                choosingView.WhichScreen(deck,view)
            }
            3 -> {
                choosingView.WhichScreen(deck,view)
            }
            4 -> {
                choosingView.WhichScreen(deck,view)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .background(backgroundColor)
        ) {
            BackButton(
                onBackClick = { onNavigate() },
                modifier = presetModifier
            )
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = deck.name,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 50.dp)
                    )
                }
                    Column(
                        modifier = Modifier
                        .weight(2f)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp, start = 20.dp, end = 20.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier.weight(1f)
                            ) {
                                Button(
                                    onClick = {
                                        mainViewModel.deleteDeck(deck)
                                        onNavigate()
                                    },
                                    modifier = Modifier.width(300.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = buttonColor,
                                        contentColor = textColor
                                    )
                                ) {
                                    Text("Delete Deck")
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                            Box(
                                modifier = Modifier.weight(1f)
                            ) {
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            cardViewModel.getDueCards(deck.id)
                                        }
                                        view.whichView.intValue = 2
                                    },
                                    modifier = Modifier.width(300.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = buttonColor,
                                        contentColor = textColor
                                    )
                                ) {
                                    Text("Start Deck")
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp, start = 20.dp, end = 20.dp),
                            horizontalArrangement = Arrangement.Center // Adjust the arrangement as needed
                        ) {
                            Box(
                                modifier = Modifier.weight(1f) // Make the button take equal space
                            ) {
                                Button(
                                    onClick = {
                                        view.whichView.intValue = 3
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = buttonColor,
                                        contentColor = textColor
                                    ),
                                    modifier = Modifier.width(300.dp) // Fill the width of the Box
                                ) {
                                    Text("Edit Deck")
                                }
                            }

                            Box(
                                modifier = Modifier.weight(1f) // Make the button take equal space
                            ) {
                                Button(
                                    onClick = {
                                        view.whichView.intValue = 4
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = buttonColor,
                                        contentColor = textColor
                                    ),
                                    modifier = Modifier.width(300.dp) // Fill the width of the Box
                                ) {
                                    Text("Edit Flashcards")
                                }
                            }
                        }
                    }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                ) {
                    val bottomLeftModifier = Modifier
                        .padding(bottom = 12.dp)
                        .align(Alignment.End)
                    Box(
                        modifier = bottomLeftModifier,
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        AddCardButton(
                            onClick = {
                                view.whichView.intValue = 1
                            }
                        )
                    }
                }
            }
        }
    }
}