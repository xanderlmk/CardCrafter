package com.belmontCrest.cardCrafter

/**
import androidx.room.migration.AutoMigrationSpec
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.belmontCrest.cardCrafter.localDatabase.database.FlashCardDatabase
import com.belmontCrest.cardCrafter.model.migrations.MIGRATION_27_28
import junit.framework.TestCase.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


 TODO
@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val testDB = "migration-test-db"

    @get:Rule
    val helper = getMigrationTestHelper()
    @Test
    @Throws(IOException::class)
    fun testUniqueIndexEnforcement_afterMigration() {
        // Create database with version 27 schema and insert valid initial record.
        var db: SupportSQLiteDatabase = helper.createDatabase(testDB, 27).apply {
            //execSQL("INSERT INTO cards_temp (id, deckId, deckUUID) VALUES (1, 100, 'uuid-123');")
            close()
        }
        // Migrate to version 28 using our migration object.
        db = helper.runMigrationsAndValidate(testDB, 28, true, MIGRATION_27_28)

        // Attempt to insert a duplicate deckUUID + cardDeckNumber, which should violate the unique index.
        try {

        } catch (e: Exception) {
            // Expected exception – the unique index is enforced properly.
        } finally {
            db.close()
        }
    }
}*/