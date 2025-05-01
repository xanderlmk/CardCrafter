package com.belmontCrest.cardCrafter.navigation.destinations

import kotlinx.serialization.Serializable

@Serializable
object SupabaseDestination : NavigationDestination {
    override val route = "Supabase"
}
@Serializable
object ImportSBDestination : NavigationDestination {
    override val route = "ImportDeck/{uuid}"
    fun createRoute(uuid: String): String {
        return "ImportDeck/$uuid"
    }
}

@Serializable
object ExportSBDestination : NavigationDestination {
    override val route = "ExportDeck"
}

@Serializable
object UserProfileDestination : NavigationDestination {
    override val route = "UserProfile"
}

@Serializable
object UserEDDestination : NavigationDestination {
    override val route = "UserExportedDecks"
}

@Serializable
object UseEmailDestination : NavigationDestination{
    override val route = "UseEmail"
}

@Serializable
object SBCardListDestination : NavigationDestination {
    override val route = "SBCardList"
}

@Serializable
object CoOwnerRequestsDestination : NavigationDestination {
    override val route = "CoOwnerRequests"
}