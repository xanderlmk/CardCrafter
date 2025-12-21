package com.belmontCrest.cardCrafter.navigation

import androidx.navigation.NavHostController
import com.belmontCrest.cardCrafter.model.ui.states.StringVar
import com.belmontCrest.cardCrafter.navigation.destinations.DeckViewDestination
import com.belmontCrest.cardCrafter.navigation.destinations.MainNavDestination
import com.belmontCrest.cardCrafter.navigation.destinations.SupabaseDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface NavHostRepo {
    /** Deck NavHostController */
    val deckNav: StateFlow<NavHostController?>
    fun updateDeckNav(navHostController: NavHostController)
    /** Supabase NavHostController */
    val sbNav: StateFlow<NavHostController?>
    fun updateSBNav(navHostController: NavHostController)
    /** Current route/Destination */
    val route: StateFlow<StringVar>
    fun updateRoute(newRoute: String)
    val startingDeckRoute: StateFlow<StringVar>
    fun updateStartingDeckRoute(newRoute: String)
    val startingSBRoute: StateFlow<StringVar>
    fun updateStartingSBRoute(newRoute: String)
    /** Retrieve saved state handle keys */
    fun onInit(initRoute: String?, startDeck: String?, startSB: String?)
}

class NavHostRepoImpl() : NavHostRepo {
    private val _deckNav: MutableStateFlow<NavHostController?> = MutableStateFlow(null)
    override val deckNav = _deckNav.asStateFlow()
    override fun updateDeckNav(navHostController: NavHostController) =
        _deckNav.update { navHostController }

    private val _sbNav: MutableStateFlow<NavHostController?> = MutableStateFlow(null)
    override val sbNav = _sbNav.asStateFlow()
    override fun updateSBNav(navHostController: NavHostController) =
        _sbNav.update { navHostController }

    private val _route = MutableStateFlow(StringVar(MainNavDestination.route))
    override val route = _route.asStateFlow()

    override fun updateRoute(newRoute: String) = _route.update { StringVar(newRoute) }

    private val _startingDeckRoute = MutableStateFlow(StringVar(DeckViewDestination.route))
    override val startingDeckRoute = _startingDeckRoute.asStateFlow()

    override fun updateStartingDeckRoute(newRoute: String) =
        _startingDeckRoute.update { StringVar(newRoute) }


    private val _startingSBRoute = MutableStateFlow(StringVar(SupabaseDestination.route))
    override val startingSBRoute = _startingSBRoute.asStateFlow()

    override fun updateStartingSBRoute(newRoute: String) =
        _startingSBRoute.update { StringVar(newRoute) }

    override fun onInit(initRoute: String?, startDeck: String?, startSB: String?) {
        updateRoute(initRoute ?: MainNavDestination.route)
        updateStartingDeckRoute(startDeck ?: DeckViewDestination.route)
        updateStartingSBRoute(startSB ?: SupabaseDestination.route)
    }
}