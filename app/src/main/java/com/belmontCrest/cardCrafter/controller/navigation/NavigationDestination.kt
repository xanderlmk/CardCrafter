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
object AddDeckDestination : NavigationDestination {
        override val route = "AddDeck"
}
@Serializable
object DeckViewDestination : NavigationDestination {
        override val route = "DeckView"
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
        override val route = "ViewCard"
}
@Serializable
object EditDeckDestination : NavigationDestination {
        override val route = "EditDeck/{currentName}"
        fun createRoute(name: String): String{
                return "EditDeck/$name"
        }
}
@Serializable
object ViewAllCardsDestination : NavigationDestination{
        override val route = "ViewFlashCards"
}
@Serializable
object EditingCardDestination : NavigationDestination {
        override val route = "EditingCard/{index}"
        fun createRoute(index : Int) : String {
                return "EditingCard/$index"
        }
}

/** Navigator destinations */
@Serializable
object SBNavDestination : NavigationDestination {
        override val route =  "sbNav"
}
@Serializable
object DeckNavDestination : NavigationDestination {
        override val route = "deckNav"
}

@Serializable
object MainNavDestination : NavigationDestination {
        override val route = "mainNav"
}