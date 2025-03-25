package com.belmontCrest.cardCrafter.controller.navigation

import kotlinx.serialization.Serializable

interface NavigationDestination {
        /**
         * Unique name to define the path for a composable
         */
        val route: String
}
@Serializable
object DeckListDestination : NavigationDestination {
        override val route = "DeckList"
}
@Serializable
object SettingsDestination : NavigationDestination {
        override val route = "Settings"
}
@Serializable
object SupabaseDestination : NavigationDestination {
        override val route = "Supabase"
}
@Serializable
object ImportSBDestination : NavigationDestination {
        override val route = "ImportDeck"
}

@Serializable
object ExportSBDestination : NavigationDestination {
        override val route = "ExportDeck"
}
@Serializable
object DeckOptionsDestination : NavigationDestination {
        override val route = "DeckOptions/{deckId}"
        fun createRoute(deckId: Int): String {
                return "DeckOptions/$deckId"
        }
}
@Serializable
object AddDeckDestination : NavigationDestination {
        override val route = "AddDeck"
}
@Serializable
object DeckViewDestination : NavigationDestination {
        override val route = "DeckView/{deckId}"
        fun createRoute(deckId: Int): String {
                return "DeckView/$deckId"
        }
}
@Serializable
object AddCardDestination : NavigationDestination{
        override val route = "AddCard/{deckId}"
        fun createRoute(deckId: Int): String {
                return "AddCard/$deckId"
        }
}
@Serializable
object ViewDueCardsDestination : NavigationDestination{
        override val route = "ViewCard/{deckId}"
        fun createRoute(deckId: Int): String {
                return "ViewCard/$deckId"
        }
}
@Serializable
object EditDeckDestination : NavigationDestination {
        override val route = "EditDeck/{deckId}/{currentName}"
        fun createRoute(deckId: Int, name: String): String{
                return "EditDeck/${deckId}/${name}"
        }
}
@Serializable
object ViewAllCardsDestination : NavigationDestination{
        override val route = "ViewFlashCards/{deckId}"
        fun createRoute(deckId: Int): String {
                return "ViewFlashCards/$deckId"
        }
}
@Serializable
object EditingCardDestination : NavigationDestination {
        override val route = "EditingCard/{deckId}/{index}"
        fun createRoute(deckId: Int, index : Int) : String {
                return "EditingCard/${deckId}/${index}"
        }
}