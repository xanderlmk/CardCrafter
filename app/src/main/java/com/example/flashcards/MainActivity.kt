package com.example.flashcards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.flashcards.controller.MainController
import com.example.flashcards.ui.theme.FlashcardsTheme
import com.example.flashcards.views.MainView

class MainActivity : ComponentActivity() {
    private var controller = MainController()
    private var view = MainView(controller)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashcardsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    view.DeckList("Android", Modifier.padding(innerPadding))
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FlashcardsTheme {
        var controller = MainController()
        var view = MainView(controller)
        view.DeckList("Android")
    }
}