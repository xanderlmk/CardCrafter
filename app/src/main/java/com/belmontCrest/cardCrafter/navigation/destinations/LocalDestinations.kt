package com.belmontCrest.cardCrafter.navigation.destinations

import kotlinx.serialization.Serializable

@Serializable
object DeckListDestination : NavigationDestination {
    override val route = "DeckList"
}
@Serializable
object SettingsDestination : NavigationDestination {
    override val route = "Settings"
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
    override val route = "EditingCard"
}