package com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.repositories.ownerRepos

import android.util.Log
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCTList
import com.belmontCrest.cardCrafter.controller.cardHandlers.toCard
import com.belmontCrest.cardCrafter.local.db.tables.CT
import com.belmontCrest.cardCrafter.local.db.tables.Deck
import com.belmontCrest.cardCrafter.local.db.tables.ImportedDeckInfo
import com.belmontCrest.cardCrafter.model.ui.states.SealedAllCTs
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.daos.ExportToSBDao
import com.belmontCrest.cardCrafter.supabase.model.tables.CardsToDisplay
import com.belmontCrest.cardCrafter.supabase.model.tables.FourSelectedCards
import com.belmontCrest.cardCrafter.supabase.model.tables.add
import com.belmontCrest.cardCrafter.views.misc.CARD_CRAFTER
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

interface ExportRepository {
    fun updateNewInfo(importedDeckInfo: ImportedDeckInfo, deckId: Int)
    fun addCardsToDisplay(cardIdentifier: String)
    fun updateCardToDisplay(ctd: CardsToDisplay)
    val pickedDeck: Flow<Deck?>
    val sealedAllCTs: Flow<SealedAllCTs>
    val selectedCards: Flow<FourSelectedCards>
    val importedDeckInfo: Flow<ImportedDeckInfo?>
    fun getCTD(): CardsToDisplay?
    fun updateDeckId(id: Int)
}

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineExportRepository(private val exportToSBDao: ExportToSBDao) : ExportRepository {
    private val _cardsToDisplay: MutableStateFlow<CardsToDisplay?> = MutableStateFlow(null)
    private val deckId = MutableStateFlow(0)
    override val pickedDeck = deckId.flatMapLatest { id ->
        if (id == 0) {
            flowOf(null)
        } else {
            exportToSBDao.getDeckFlow(id)
        }
    }
    override val sealedAllCTs = deckId.flatMapLatest { id ->
        if (id == 0)
            flowOf(SealedAllCTs())
        else exportToSBDao.getAllCardTypes(id).map {
            try {
                SealedAllCTs(it.toCTList())
            } catch (e: Exception) {
                Log.e(CARD_CRAFTER, "$e")
                SealedAllCTs(emptyList())
            }
        }
    }

    override val selectedCards: Flow<FourSelectedCards> = combine(
        sealedAllCTs, _cardsToDisplay
    ) { sealedAll, cardsTD ->
        if (cardsTD == null) {
            FourSelectedCards()
        } else {
            // Lookup a CT by cardIdentifier
            fun findCT(cardIdentifier: String?): CT? =
                cardIdentifier?.let { lookup ->
                    sealedAll.allCTs.firstOrNull { it.toCard().cardIdentifier == lookup }
                }
            // Build the FourSelectedCards
            FourSelectedCards(
                first = findCT(cardsTD.cardOne),
                second = findCT(cardsTD.cardTwo),
                third = findCT(cardsTD.cardThree),
                fourth = findCT(cardsTD.cardFour)
            )
        }
    }

    override val importedDeckInfo = deckId.flatMapLatest { id ->
        if (id == 0) flowOf(null)
        else getImportedDeckInfo(id).map { it }
    }

    override fun addCardsToDisplay(cardIdentifier: String) =
        _cardsToDisplay.update { it?.add(cardIdentifier) }

    override fun getCTD() = _cardsToDisplay.value
    override fun updateCardToDisplay(ctd: CardsToDisplay) = _cardsToDisplay.update { ctd }

    override fun updateNewInfo(importedDeckInfo: ImportedDeckInfo, deckId: Int) =
        exportToSBDao.updateNewInfo(importedDeckInfo, deckId)

    override fun updateDeckId(id: Int) = deckId.update { id }

    private fun getImportedDeckInfo(id: Int) = exportToSBDao.getImportedDeckInfo(id)

}