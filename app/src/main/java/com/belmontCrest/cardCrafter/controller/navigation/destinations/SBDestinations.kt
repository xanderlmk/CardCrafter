package com.belmontCrest.cardCrafter.controller.navigation.destinations

import kotlinx.serialization.Serializable

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
object UserProfileDestination : NavigationDestination {
    override val route = "UserProfile"
}