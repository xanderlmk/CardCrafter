@file:Suppress("RedundantOverride", "unused")

package com.belmontCrest.cardCrafter.localDatabase.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.daos.CardDao
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.daos.CardTypesDao
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.daos.DeckDao
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.daos.DueCardsDao
import com.belmontCrest.cardCrafter.localDatabase.dbInterface.daos.SavedCardDao
import com.belmontCrest.cardCrafter.localDatabase.tables.BasicCard
import com.belmontCrest.cardCrafter.localDatabase.tables.Card
import com.belmontCrest.cardCrafter.localDatabase.tables.CardInfo
import com.belmontCrest.cardCrafter.localDatabase.tables.NullableCustomCard
import com.belmontCrest.cardCrafter.localDatabase.tables.Deck
import com.belmontCrest.cardCrafter.localDatabase.tables.EncryptionConverter
import com.belmontCrest.cardCrafter.localDatabase.tables.HintCard
import com.belmontCrest.cardCrafter.localDatabase.tables.ImportedDeckInfo
import com.belmontCrest.cardCrafter.localDatabase.tables.ListStringConverter
import com.belmontCrest.cardCrafter.localDatabase.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.localDatabase.tables.NotationCard
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.ParamConverter
import com.belmontCrest.cardCrafter.localDatabase.tables.Pwd
import com.belmontCrest.cardCrafter.localDatabase.tables.QOrAConverter
import com.belmontCrest.cardCrafter.localDatabase.tables.SavedCard
import com.belmontCrest.cardCrafter.localDatabase.tables.SyncedDeckInfo
import com.belmontCrest.cardCrafter.localDatabase.tables.ThreeFieldCard
import com.belmontCrest.cardCrafter.localDatabase.tables.TimeConverter
import com.belmontCrest.cardCrafter.model.migrations.m11_m16.MIGRATION_10_11
import com.belmontCrest.cardCrafter.model.migrations.m11_m16.MIGRATION_11_12
import com.belmontCrest.cardCrafter.model.migrations.m11_m16.MIGRATION_12_13
import com.belmontCrest.cardCrafter.model.migrations.m11_m16.MIGRATION_13_14
import com.belmontCrest.cardCrafter.model.migrations.m11_m16.MIGRATION_14_15
import com.belmontCrest.cardCrafter.model.migrations.m11_m16.MIGRATION_15_16
import com.belmontCrest.cardCrafter.model.migrations.m16_m20.MIGRATION_16_17
import com.belmontCrest.cardCrafter.model.migrations.m16_m20.MIGRATION_17_18
import com.belmontCrest.cardCrafter.model.migrations.m16_m20.MIGRATION_18_19
import com.belmontCrest.cardCrafter.model.migrations.m16_m20.MIGRATION_19_20
import com.belmontCrest.cardCrafter.model.migrations.MIGRATION_20_21
import com.belmontCrest.cardCrafter.model.migrations.MIGRATION_21_22
import com.belmontCrest.cardCrafter.model.migrations.MIGRATION_22_23
import com.belmontCrest.cardCrafter.model.migrations.MIGRATION_23_24
import com.belmontCrest.cardCrafter.model.migrations.MIGRATION_24_25
import com.belmontCrest.cardCrafter.model.migrations.MIGRATION_25_26
import com.belmontCrest.cardCrafter.model.migrations.MIGRATION_26_27
import com.belmontCrest.cardCrafter.model.migrations.MIGRATION_27_28
import com.belmontCrest.cardCrafter.model.migrations.MIGRATION_28_29
import com.belmontCrest.cardCrafter.model.migrations.MIGRATION_29_30
import com.belmontCrest.cardCrafter.model.migrations.MIGRATION_30_31
import com.belmontCrest.cardCrafter.model.migrations.MIGRATION_31_32
import com.belmontCrest.cardCrafter.model.migrations.MIGRATION_32_33
import com.belmontCrest.cardCrafter.model.migrations.MIGRATION_33_34
import com.belmontCrest.cardCrafter.model.migrations.m1_m10.MIGRATION_3_5
import com.belmontCrest.cardCrafter.model.migrations.m1_m10.MIGRATION_5_6
import com.belmontCrest.cardCrafter.model.migrations.m1_m10.MIGRATION_6_7
import com.belmontCrest.cardCrafter.model.migrations.m1_m10.MIGRATION_7_8
import com.belmontCrest.cardCrafter.model.migrations.m1_m10.MIGRATION_8_9
import com.belmontCrest.cardCrafter.model.migrations.m1_m10.MIGRATION_9_10
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.daos.ExportToSBDao
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.daos.PwdDao
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.daos.ImportFromSBDao
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.daos.MergeDecksDao
import com.belmontCrest.cardCrafter.supabase.model.daoAndRepository.daos.SyncedDeckInfoDao
import kotlinx.coroutines.CoroutineScope

/** The database instance. */
@Database(
    entities = [
        Deck::class, Card::class, BasicCard::class, ThreeFieldCard::class,
        HintCard::class, MultiChoiceCard::class, NotationCard::class, SavedCard::class,
        ImportedDeckInfo::class, SyncedDeckInfo::class, Pwd::class, CardInfo::class,
        NullableCustomCard::class
    ],
    version = 34, exportSchema = true
)
@TypeConverters(
    TimeConverter::class, ListStringConverter::class, EncryptionConverter::class,
    QOrAConverter::class, ParamConverter::class
)
abstract class FlashCardDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun cardDao(): CardDao
    abstract fun cardTypes(): CardTypesDao
    abstract fun savedCardDao(): SavedCardDao
    abstract fun importFromSBDao(): ImportFromSBDao
    abstract fun pwdDao(): PwdDao
    abstract fun syncedDeckInfoDao(): SyncedDeckInfoDao
    abstract fun exportToSBDao(): ExportToSBDao
    abstract fun mergeDecksDao(): MergeDecksDao
    abstract fun dueCardsDao(): DueCardsDao

    companion object {
        @Volatile
        private var Instance: FlashCardDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FlashCardDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(context, FlashCardDatabase::class.java, "deck_database")
                        .addMigrations(
                            MIGRATION_3_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8,
                            MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12,
                            MIGRATION_12_13, MIGRATION_13_14, MIGRATION_14_15, MIGRATION_15_16,
                            MIGRATION_16_17, MIGRATION_17_18, MIGRATION_18_19, MIGRATION_19_20,
                            MIGRATION_20_21, MIGRATION_21_22, MIGRATION_22_23, MIGRATION_23_24,
                            MIGRATION_24_25, MIGRATION_25_26, MIGRATION_26_27, MIGRATION_27_28,
                            MIGRATION_28_29, MIGRATION_29_30, MIGRATION_30_31, MIGRATION_31_32,
                            MIGRATION_32_33, MIGRATION_33_34
                        )
                        .fallbackToDestructiveMigration()
                        .addCallback(FlashCardDatabaseCallback(scope))
                        // Add callback for population
                        .build().also { Instance = it }
                Instance = instance
                // return instance
                instance
            }
        }


        private class FlashCardDatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Populate the database in a coroutine
                /**Instance?.let { database ->
                scope.launch(Dispatchers.IO) {
                populateDatabase(
                database.deckDao(), database.cardDao(),
                database.basicCardDao(), database.multiChoiceCardDao(),
                database.threeCardDao()
                )
                }
                }*/
            }
        }
    }
}