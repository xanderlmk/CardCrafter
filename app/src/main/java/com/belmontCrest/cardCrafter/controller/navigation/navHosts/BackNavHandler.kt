package com.belmontCrest.cardCrafter.controller.navigation.navHosts

import androidx.navigation.NavHostController
import com.belmontCrest.cardCrafter.controller.navigation.DeckListDestination
import com.belmontCrest.cardCrafter.controller.navigation.MainNavDestination
import com.belmontCrest.cardCrafter.model.uiModels.Fields

object BackNavHandler {
    fun returnToDeckListFromSB(
        navController: NavHostController,
        updateCurrentTime: Unit,
        fields : Fields
    ) {
        fields.mainClicked.value = false
        updateCurrentTime
        navController.navigate(MainNavDestination.route)
    }
    fun returnToDeckListFromDeck(
        navController: NavHostController,
        updateCurrentTime: Unit,
        updateIndex: Unit,
        fields: Fields
    ){
        fields.scrollPosition.value = 0
        fields.inDeckClicked.value = true
        fields.mainClicked.value = false
        updateCurrentTime
        updateIndex
        navController.navigate(MainNavDestination.route)
    }

}