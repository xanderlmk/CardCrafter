package com.belmontCrest.cardCrafter.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.belmontCrest.cardCrafter.localDatabase.tables.BasicCard
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.HintCard
import com.belmontCrest.cardCrafter.localDatabase.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.localDatabase.tables.NotationCard
import com.belmontCrest.cardCrafter.localDatabase.tables.ThreeFieldCard


@Dao
interface InsertAndReplaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: Card): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBasicCard(basicCard: BasicCard): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHintCard(hintCard: HintCard): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMultiChoiceCard(multiChoiceCard: MultiChoiceCard): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotationCard(notationCard: NotationCard): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThreeCard(threeFieldCard: ThreeFieldCard): Long
}

@Dao
interface InsertOrAbortDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCard(card: Card): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBasicCard(basicCard: BasicCard): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertHintCard(hintCard: HintCard): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMultiChoiceCard(multiChoiceCard: MultiChoiceCard): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertNotationCard(notationCard: NotationCard): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertThreeCard(threeFieldCard: ThreeFieldCard): Long
}