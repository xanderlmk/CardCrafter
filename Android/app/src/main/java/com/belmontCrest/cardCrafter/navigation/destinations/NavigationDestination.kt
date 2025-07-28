package com.belmontCrest.cardCrafter.navigation.destinations

import kotlinx.serialization.Serializable

interface NavigationDestination {
        /**
         * Unique name to define the path for a composable
         */
        val route: String
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