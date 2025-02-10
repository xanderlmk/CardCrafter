package com.example.flashcards.controller.navigation

interface NavigationDestination {
        /**
         * Unique name to define the path for a composable
         */
        val route: String
}

object DeckListDestination : NavigationDestination {
        override val route = "DeckList"
}
object SettingsDestination : NavigationDestination {
        override val route = "Settings"
}
object DeckOptionsDestination : NavigationDestination {
        override val route = "DeckOptions/{deckId}"
        fun createRoute(deckId: Int): String {
                return "DeckOptions/$deckId"
        }
}
object AddDeckDestination : NavigationDestination {
        override val route = "AddDeck"
}
object DeckViewDestination : NavigationDestination {
        override val route = "DeckView/{deckId}"
        fun createRoute(deckId: Int): String {
                return "DeckView/$deckId"
        }
}
object AddCardDestination : NavigationDestination{
        override val route = "AddCard/{deckId}"
        fun createRoute(deckId: Int): String {
                return "AddCard/$deckId"
        }
}
object ViewCardDestination : NavigationDestination{
        override val route = "ViewCard/{deckId}"
        fun createRoute(deckId: Int): String {
                return "ViewCard/$deckId"
        }
}
object EditDeckDestination : NavigationDestination {
        override val route = "EditDeck/{deckId}/{currentName}"
        fun createRoute(deckId: Int, name: String): String{
                return "EditDeck/${deckId}/${name}"
        }
}
object ViewAllCardsDestination : NavigationDestination{
        override val route = "ViewFlashCards/{deckId}"
        fun createRoute(deckId: Int): String {
                return "ViewFlashCards/$deckId"
        }
}
object EditingCardDestination : NavigationDestination {
        override val route = "EditingCard/{deckId}"
        fun createRoute(deckId: Int) : String {
                return "EditingCard/${deckId}"
        }
}