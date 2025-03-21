package com.example.flashcards.model

import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.flashcards.model.daoFiles.allCardTypesDao.BasicCardDao
import com.example.flashcards.model.daoFiles.deckAndCardDao.CardDao
import com.example.flashcards.model.daoFiles.deckAndCardDao.CardTypesDao
import com.example.flashcards.model.daoFiles.deckAndCardDao.DeckDao
import com.example.flashcards.model.daoFiles.allCardTypesDao.HintCardDao
import com.example.flashcards.model.daoFiles.allCardTypesDao.NotationCardDao
import com.example.flashcards.model.daoFiles.allCardTypesDao.MultiChoiceCardDao
import com.example.flashcards.model.daoFiles.deckAndCardDao.SavedCardDao
import com.example.flashcards.model.daoFiles.allCardTypesDao.ThreeCardDao
import com.example.flashcards.model.migrations.MIGRATION_10_11
import com.example.flashcards.model.migrations.MIGRATION_11_12
import com.example.flashcards.model.migrations.MIGRATION_12_13
import com.example.flashcards.model.migrations.MIGRATION_13_14
import com.example.flashcards.model.migrations.MIGRATION_14_15
import com.example.flashcards.model.migrations.MIGRATION_15_16
import com.example.flashcards.model.migrations.MIGRATION_16_17
import com.example.flashcards.model.migrations.MIGRATION_17_18
import com.example.flashcards.model.migrations.MIGRATION_18_19
import com.example.flashcards.model.migrations.MIGRATION_19_20
import com.example.flashcards.model.migrations.MIGRATION_3_5
import com.example.flashcards.model.migrations.MIGRATION_5_6
import com.example.flashcards.model.migrations.MIGRATION_6_7
import com.example.flashcards.model.migrations.MIGRATION_7_8
import com.example.flashcards.model.migrations.MIGRATION_8_9
import com.example.flashcards.model.migrations.MIGRATION_9_10
import com.example.flashcards.model.tablesAndApplication.BasicCard
import com.example.flashcards.model.tablesAndApplication.Card
import com.example.flashcards.model.tablesAndApplication.HintCard
import com.example.flashcards.model.tablesAndApplication.ThreeFieldCard
import com.example.flashcards.model.tablesAndApplication.Deck
import com.example.flashcards.model.tablesAndApplication.NotationCard
import com.example.flashcards.model.tablesAndApplication.ListStringConverter
import com.example.flashcards.model.tablesAndApplication.MultiChoiceCard
import com.example.flashcards.model.tablesAndApplication.TimeConverter
import com.example.flashcards.model.tablesAndApplication.SavedCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

/** The database instance. */
@Database(
    entities = [
        Deck::class,
        Card::class,
        BasicCard::class,
        ThreeFieldCard::class,
        HintCard::class,
        MultiChoiceCard::class,
        NotationCard::class,
        SavedCard::class], version = 20
)
@TypeConverters(
    TimeConverter::class,
    ListStringConverter::class
)
abstract class FlashCardDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun cardDao(): CardDao
    abstract fun cardTypes(): CardTypesDao
    abstract fun basicCardDao(): BasicCardDao
    abstract fun hintCardDao(): HintCardDao
    abstract fun threeCardDao(): ThreeCardDao
    abstract fun multiChoiceCardDao(): MultiChoiceCardDao
    abstract fun notationCardDao() : NotationCardDao
    abstract fun savedCardDao(): SavedCardDao

    companion object {
        @Volatile
        private var Instance: FlashCardDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FlashCardDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(context, FlashCardDatabase::class.java, "deck_database")
                        .addMigrations(
                            MIGRATION_3_5,
                            MIGRATION_5_6,
                            MIGRATION_6_7,
                            MIGRATION_7_8,
                            MIGRATION_8_9,
                            MIGRATION_9_10,
                            MIGRATION_10_11,
                            MIGRATION_11_12,
                            MIGRATION_12_13,
                            MIGRATION_13_14,
                            MIGRATION_14_15,
                            MIGRATION_15_16,
                            MIGRATION_16_17,
                            MIGRATION_17_18,
                            MIGRATION_18_19,
                            MIGRATION_19_20
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
                Instance?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(
                            database.deckDao(), database.cardDao(),
                            database.basicCardDao(), database.multiChoiceCardDao(),
                            database.threeCardDao()
                        )
                    }
                }
            }
        }

        private suspend fun populateDatabase(
            deckDao: DeckDao, cardDao: CardDao,
            basicCardDao: BasicCardDao,
            multiChoiceCardDao: MultiChoiceCardDao,
            threeCardDao: ThreeCardDao
        ) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, -1) // Subtract 1 day
            val nextReviewDate = calendar.time
            // Create and insert a new Deck
            val historyDeck = Deck(
                name = "History",
                nextReview = nextReviewDate,
                cardsLeft = 20,
                lastUpdated = nextReviewDate
            )
            deckDao.insertDeck(historyDeck)
            val motorcycleDeck = Deck(
                name = "Motorcycle Written Exam",
                nextReview = nextReviewDate,
                cardsLeft = 20,
                lastUpdated = nextReviewDate
            )
            deckDao.insertDeck(motorcycleDeck)

            // Get the deck ID after insertion
            val deckId = historyDeck.id + 1// This will be auto-generated
            val uuid = historyDeck.uuid

            val mcDeck = motorcycleDeck.id + 2
            val mcUuid = motorcycleDeck.uuid


            val cards = List(21) {
                Card(
                    deckId = deckId,
                    deckUUID = uuid,
                    nextReview = nextReviewDate,
                    passes = 0,
                    prevSuccess = false,
                    totalPasses = 0,
                    type = "basic",
                    reviewsLeft = historyDeck.reviewAmount
                )
            }

            val basicCards = listOf(
                BasicCard(
                    cardId = 1,
                    question = "What year did World War II begin?",
                    answer = "1939"
                ),
                BasicCard(
                    cardId = 2,
                    question = "Who was the first President of the United States?",
                    answer = "George Washington"
                ),
                BasicCard(
                    cardId = 3,
                    question = "What ancient civilization built the pyramids?",
                    answer = "Egyptians"
                ),
                BasicCard(
                    cardId = 4,
                    question = "What year did the Titanic sink?",
                    answer = "1912"
                ),
                BasicCard(
                    cardId = 5,
                    question = "Who was the first man on the moon?",
                    answer = "Neil Armstrong"
                ),
                BasicCard(
                    cardId = 6,
                    question = "What wall divided East and West Berlin?",
                    answer = "Berlin Wall"
                ),
                BasicCard(
                    cardId = 7,
                    question = "What year did the Berlin Wall fall?",
                    answer = "1989"
                ),
                BasicCard(
                    cardId = 8,
                    question = "Who wrote the Declaration of Independence?",
                    answer = "Thomas Jefferson"
                ),
                BasicCard(
                    cardId = 9,
                    question = "In which year did the U.S. Civil War begin?",
                    answer = "1861"
                ),
                BasicCard(
                    cardId = 10,
                    question = "What was the main cause of World War I?",
                    answer = "Assassination of Archduke Franz Ferdinand"
                ),
                BasicCard(
                    cardId = 11,
                    question = "Who discovered America?",
                    answer = "Christopher Columbus"
                ),
                BasicCard(
                    cardId = 12,
                    question = "What event started the Great Depression?",
                    answer = "Stock Market Crash of 1929"
                ),
                BasicCard(
                    cardId = 13,
                    question = "Which country gifted the Statue of Liberty to the United States?",
                    answer = "France"
                ),
                BasicCard(
                    cardId = 14,
                    question = "Who was the British Prime Minister during World War II?",
                    answer = "Winston Churchill"
                ),
                BasicCard(
                    cardId = 15,
                    question = "What was the name of the ship that brought the Pilgrims to America?",
                    answer = "Mayflower"
                ),
                BasicCard(
                    cardId = 16,
                    question = "What year did the U.S. enter World War I?",
                    answer = "1917"
                ),
                BasicCard(
                    cardId = 17,
                    question = "Who was the first woman to fly solo across the Atlantic?",
                    answer = "Amelia Earhart"
                ),
                BasicCard(
                    cardId = 18,
                    question = "What ancient city is known for its hanging gardens?",
                    answer = "Babylon"
                ),
                BasicCard(
                    cardId = 19,
                    question = "Who was the first Emperor of Rome?",
                    answer = "Augustus"
                ),
                BasicCard(
                    cardId = 20,
                    question = "What year did the French Revolution begin?",
                    answer = "1789"
                ),
                BasicCard(
                    cardId = 21,
                    question = "What is the longest river in the world?",
                    answer = "Nile River"
                )
            )

            // Insert all cards into the database
            for (card in cards) {
                cardDao.insertCard(card)
            }
            for (basicCard in basicCards) {
                basicCardDao.insertBasicCard(basicCard)
            }

            val motorCycleCards = List(75) {

                Card(
                    deckId = mcDeck,
                    deckUUID = mcUuid,
                    nextReview = nextReviewDate,
                    passes = 0,
                    prevSuccess = false,
                    totalPasses = 0,
                    type = "multi",
                    reviewsLeft = motorcycleDeck.reviewAmount
                )
            }

            val multiChoiceCards = listOf(
                MultiChoiceCard(
                    cardId = 22,
                    question = "When should you check your motorcycle's tire pressure",
                    choiceA = "every time before you ride",
                    choiceB = "once a week",
                    choiceC = "once a month",
                    choiceD = "when it looks low",
                    correct = 'a'
                ),
                MultiChoiceCard(
                    cardId = 23,
                    question = "If you accidentally lock the front wheel when applying the front brakes, you should:",
                    choiceA = "keep it locked",
                    choiceB = "release and then reapply with less pressure",
                    choiceC = "apply more rear brake",
                    choiceD = "accelerate",
                    correct = 'b'
                ),
                MultiChoiceCard(
                    cardId = 24,
                    question = "Why should you park your motorcycle in 1st gear?",
                    choiceA = "to keep it from being stolen",
                    choiceB = "to keep it from rolling",
                    choiceC = "so it is easier to start",
                    choiceD = "all of the above",
                    correct = 'b'
                ),
                MultiChoiceCard(
                    cardId = 25,
                    question = "What is the rider's torso position during a swerve?",
                    choiceA = "upright, independent of motorcycle lean",
                    choiceB = "leaning in direction of swerve",
                    choiceC = "leaning away from the swerve",
                    choiceD = "none of the above",
                    correct = 'a'
                ),
                MultiChoiceCard(
                    cardId = 26,
                    question = "A rider must _____ when mounting and dismounting a motorcycle.",
                    choiceA = "squeeze the front brake",
                    choiceB = "squeeze the clutch",
                    choiceC = "shift gears",
                    choiceD = "roll on the throttle",
                    correct = 'a'
                ),
                MultiChoiceCard(
                    cardId = 27,
                    question = "Why is it good practice to squeeze the clutch before starting a motorcycle?",
                    choiceA = "in case you want to shift gears",
                    choiceB = "to keep the motorcycle from lurching forward if it is in gear",
                    choiceC = "so the engine will start",
                    choiceD = "to keep the engine from over-revving",
                    correct = 'b'
                ),
                MultiChoiceCard(
                    cardId = 28,
                    question = "When looking through a curve, what should the rider be looking for?",
                    choiceA = "surface conditions such as gravel, sand, etc.",
                    choiceB = "the tightness of the curve (increasing or decreasing radius)",
                    choiceC = "oncoming vehicles that might be in your lane",
                    choiceD = "any or all of the above",
                    correct = 'd'
                ),
                MultiChoiceCard(
                    cardId = 29,
                    question = "Is it ok not to dim your headlights if an oncoming driver does not dim theirs?",
                    choiceA = "yes, they need to know that they are blinding you",
                    choiceB = "yes, until they dim theirs",
                    choiceC = "no, now there are two blind drivers",
                    choiceD = "no, but you should honk your horn",
                    correct = 'c'
                ),
                MultiChoiceCard(
                    cardId = 30,
                    question = "Central, clear vision is a:",
                    choiceA = "3-degree cone",
                    choiceB = "10-degree cone",
                    choiceC = "45-degree cone",
                    choiceD = "180 degrees",
                    correct = 'a'
                ),
                MultiChoiceCard(
                    cardId = 31,
                    question = "Most motorcycle accidents occur at intersections and ____.",
                    choiceA = "stop lights",
                    choiceB = "bus stops",
                    choiceC = "curves",
                    choiceD = "hills",
                    correct = 'c'
                ),
                MultiChoiceCard(
                    cardId = 32,
                    question = "What are possible positions of a motorcycle fuel valve (on those equipped with a fuel valve)?",
                    choiceA = "on",
                    choiceB = "off",
                    choiceC = "reserve",
                    choiceD = "all of the above",
                    correct = 'd'
                ),
                MultiChoiceCard(
                    cardId = 33,
                    question = "To \"rev\" the engine means to:",
                    choiceA = "cause the engine rpm to decrease",
                    choiceB = "cause the engine rpm to increase",
                    choiceC = "shut off the engine",
                    choiceD = "start the engine",
                    correct = 'b'
                ),
                MultiChoiceCard(
                    cardId = 34,
                    question = "Gauntlet gloves:",
                    choiceA = "extend past the wrist",
                    choiceB = "prevent cold air from going up the sleeve while riding",
                    choiceC = "are typically worn in colder weather",
                    choiceD = "all of the above",
                    correct = 'd'
                ),
                MultiChoiceCard(
                    cardId = 35,
                    question = "The motorcycle throttle is operated by:",
                    choiceA = "pushing it in",
                    choiceB = "pulling it out",
                    choiceC = "twisting it toward the rider",
                    choiceD = "twisting it away from the rider",
                    correct = 'c'
                ),
                MultiChoiceCard(
                    cardId = 36,
                    question = "\"Squaring the Handlebars\" means:",
                    choiceA = "handlebars are centered and not turned left or right",
                    choiceB = "handlebars are turned right",
                    choiceC = "handlebars are turned left",
                    choiceD = "none of the above",
                    correct = 'a'
                ),
                MultiChoiceCard(
                    cardId = 37,
                    question = "The \"O\" in T-CLOCS means:",
                    choiceA = "objects",
                    choiceB = "observe",
                    choiceC = "oil",
                    choiceD = "OK",
                    correct = 'c'
                ),
                MultiChoiceCard(
                    cardId = 38,
                    question = "How can a rider reduce total stopping distance?",
                    choiceA = "covering the controls",
                    choiceB = "use both brakes progressively without skidding either wheel",
                    choiceC = "practice braking skills",
                    choiceD = "all of the above",
                    correct = 'd'
                ),
                MultiChoiceCard(
                    cardId = 39,
                    question = "The most common scenario in an intersection crash involving a motorcycle and another vehicle is:",
                    choiceA = "a right turning vehicle in front of the motorcycle",
                    choiceB = "a left turning vehicle in front of the motorcycle",
                    choiceC = "a vehicle hitting the motorcycle from behind",
                    choiceD = "a head-on collision involving the motorcycle and another vehicle",
                    correct = 'b'
                ),
                MultiChoiceCard(
                    cardId = 40,
                    question = "In a quick-stop on a surface with good traction, a rear-tire skid could result in:",
                    choiceA = "a high-side crash",
                    choiceB = "a low-side crash",
                    choiceC = "a stoppie",
                    choiceD = "a wheelie",
                    correct = 'a'
                ),
                MultiChoiceCard(
                    cardId = 41,
                    question = "How does riding gear make a rider more comfortable?",
                    choiceA = "allows for good airflow when needed",
                    choiceB = "keeps rider warm when needed",
                    choiceC = "keeps out rain, debris, and cold when needed",
                    choiceD = "All of the above",
                    correct = 'd'
                ),
                MultiChoiceCard(
                    cardId = 42,
                    question = "An important thing to remember when using your turn signal on a motorcycle is:",
                    choiceA = "to remember to cancel it",
                    choiceB = "to remember which direction to push the thumb",
                    choiceC = "to remember to use it",
                    choiceD = "where the switch is located",
                    correct = 'a'
                ),
                MultiChoiceCard(
                    cardId = 43,
                    question = "When should a rider begin to turn his/her head for cornering?",
                    choiceA = "at the beginning of the corner",
                    choiceB = "before he/she begins to lean",
                    choiceC = "halfway through the corner",
                    choiceD = "at the end of the turn",
                    correct = 'b'
                ),
                MultiChoiceCard(
                    cardId = 44,
                    question = "Where should a rider look while swerving?",
                    choiceA = "straight ahead",
                    choiceB = "at the obstacle he/she is trying to avoid",
                    choiceC = "through the escape path",
                    choiceD = "at the ground",
                    correct = 'c'
                ),
                MultiChoiceCard(
                    cardId = 45,
                    question = "What is the quickest way to stop in a curve?",
                    choiceA = "gradually apply both brakes while straightening the motorcycle",
                    choiceB = "straighten the motorcycle and then brake hard in a straight line",
                    choiceC = "only use the front brake",
                    choiceD = "only use the rear brake",
                    correct = 'b'
                ),
                MultiChoiceCard(
                    cardId = 46,
                    question = "What should you do if you see a deer on the side of the road?",
                    choiceA = "slow down immediately",
                    choiceB = "steer away from the deer",
                    choiceC = "steer toward the deer",
                    choiceD = "honk your horn",
                    correct = 'a'
                ),
                MultiChoiceCard(
                    cardId = 47,
                    question = "Which of the following safety standards designators might be found on motorcycle helmets?",
                    choiceA = "DOT",
                    choiceB = "Snell",
                    choiceC = "ECE",
                    choiceD = "all of the above",
                    correct = 'd'
                ),
                MultiChoiceCard(
                    cardId = 48,
                    question = "In a crash, the single biggest point of impact on a rider is the:",
                    choiceA = "forehead",
                    choiceB = "chin",
                    choiceC = "temple",
                    choiceD = "back of head",
                    correct = 'b'
                ),
                MultiChoiceCard(
                    cardId = 49,
                    question = "The primary purpose of the outer shell of a motorcycle helmet is:",
                    choiceA = "to provide comfort to the rider",
                    choiceB = "to resist penetration and disperse energy",
                    choiceC = "to make the helmet fit well",
                    choiceD = "to make a statement",
                    correct = 'b'
                ),
                MultiChoiceCard(
                    cardId = 50,
                    question = "At night or in times of poor visibility, it is important that your face shield or goggles are:",
                    choiceA = "tinted light",
                    choiceB = "dark tinted",
                    choiceC = "yellow or clear",
                    choiceD = "removed",
                    correct = 'c'
                ),
                MultiChoiceCard(
                    cardId = 51,
                    question = "The purpose of motorcycle gloves is:",
                    choiceA = "provide protection against debris",
                    choiceB = "provide warmth",
                    choiceC = "provide grip",
                    choiceD = "all of the above",
                    correct = 'd'
                ),
                MultiChoiceCard(
                    cardId = 52,
                    question = "When crossing an obstacle, the rider should look:",
                    choiceA = "down at the road in front of the tire",
                    choiceB = "at the obstacle",
                    choiceC = "straight ahead",
                    choiceD = "up",
                    correct = 'c'
                ),
                MultiChoiceCard(
                    cardId = 53,
                    question = "When riding in the rain, the rider should:",
                    choiceA = "predict less traction",
                    choiceB = "ride in the tracks of the vehicle in front",
                    choiceC = "reduce speed and increase following distance",
                    choiceD = "all of the above",
                    correct = 'd'
                ),
                MultiChoiceCard(
                    cardId = 54,
                    question = "Proper posture includes having the right wrist:",
                    choiceA = "higher than the knuckles",
                    choiceB = "flat or even with the knuckles",
                    choiceC = "lower than the knuckles",
                    choiceD = "at whatever position is comfortable",
                    correct = 'b'
                ),
                MultiChoiceCard(
                    cardId = 55,
                    question = "The front brake supplies about ___ of total stopping power on a cruiser-type motorcycle",
                    choiceA = "30%",
                    choiceB = "50%",
                    choiceC = "70%",
                    choiceD = "100%",
                    correct = 'c'
                ),
                MultiChoiceCard(
                    cardId = 56,
                    question = "On a sport bike, approximately ___ of total stopping power is provided by the rear brake",
                    choiceA = "10%",
                    choiceB = "30%",
                    choiceC = "50%",
                    choiceD = "80%",
                    correct = 'a'
                ),
                MultiChoiceCard(
                    cardId = 57,
                    question = "The tachometer on a motorcycle shows ___",
                    choiceA = "road speed in miles per hour",
                    choiceB = "gear position",
                    choiceC = "engine speed in rotations per minute",
                    choiceD = "miles driven",
                    correct = 'c'
                ),
                MultiChoiceCard(
                    cardId = 58,
                    question = "When riding within a group, the inexperienced driver is usually (should be) ___",
                    choiceA = "at the tail end of the group",
                    choiceB = "in front of the group",
                    choiceC = "in the middle of the group",
                    choiceD = "behind the leader of the group",
                    correct = 'd'
                ),
                MultiChoiceCard(
                    cardId = 59,
                    question = "The most effective way to stop quickly is to ______",
                    choiceA = "use both brakes simultaneously in a straight line",
                    choiceB = "use only the front brake",
                    choiceC = "use only the rear brake",
                    choiceD = "engine brake",
                    correct = 'a'
                ),
                MultiChoiceCard(
                    cardId = 60,
                    question = "Bright, reflective colors should be worn ___",
                    choiceA = "only at night",
                    choiceB = "during the day",
                    choiceC = "all the time when riding",
                    choiceD = "never",
                    correct = 'c'
                ),
                MultiChoiceCard(
                    cardId = 61,
                    question = "A good entry-speed for a curve is:",
                    choiceA = "a speed that allows you to roll on the throttle or maintain throttle at the beginning of the curve",
                    choiceB = "a speed that allows you to brake through the curve",
                    choiceC = "the fastest speed you can take the curve",
                    choiceD = "the slowest speed you can take the curve",
                    correct = 'a'
                ),
                MultiChoiceCard(
                    cardId = 62,
                    question = "The best place to find the correct pressure for your tires is:",
                    choiceA = "the sidewall of the tire",
                    choiceB = "the motorcycle's owner's manual",
                    choiceC = "the tire manufacturer's website",
                    choiceD = "the motorcycle dealer",
                    correct = 'b'
                ),
                MultiChoiceCard(
                    cardId = 63,
                    question = "When riding your motorcycle, where is the most likely place for colliding with another vehicle?",
                    choiceA = "curve",
                    choiceB = "steep hill",
                    choiceC = "an intersection",
                    choiceD = "none of the above",
                    correct = 'c'
                ),
                MultiChoiceCard(
                    cardId = 64,
                    question = "Where do most single-vehicle (motorcycle) crashes occur?",
                    choiceA = "curve",
                    choiceB = "steep hill",
                    choiceC = "an intersection",
                    choiceD = "none of the above",
                    correct = 'a'
                ),
                MultiChoiceCard(
                    cardId = 65,
                    question = "Stopping quickly in a curve requires the rider to:",
                    choiceA = "applying both brakes hard and fast while leaned",
                    choiceB = "leaning the opposite direction while braking",
                    choiceC = "progressively applying both brakes until the bike is upright",
                    choiceD = "only using the rear brake",
                    correct = 'c'
                ),
                MultiChoiceCard(
                    cardId = 66,
                    question = "How many escape paths should a rider keep open in a collision trap?",
                    choiceA = "1",
                    choiceB = "2",
                    choiceC = "3",
                    choiceD = "more than 1",
                    correct = 'd'
                ),
                MultiChoiceCard(
                    cardId = 67,
                    question = "When riding in a crosswind, the rider may have to:",
                    choiceA = "use more hand-grip into the wind",
                    choiceB = "use the rear brake",
                    choiceC = "go faster",
                    choiceD = "not carry a passenger",
                    correct = 'a'
                ),
                MultiChoiceCard(
                    cardId = 68,
                    question = "Why does a motorcycle have an engine cut-off switch?",
                    choiceA = "as an emergency backup to the key",
                    choiceB = "to offer another option for turning off the engine",
                    choiceC = "so the rider does not have to take hands off of the hand-grip to turn the engine off",
                    choiceD = "to make sure the engine is really off after turning off the key",
                    correct = 'c'
                ),
                MultiChoiceCard(
                    cardId = 69,
                    question = "What does the clutch lever do?",
                    choiceA = "shift gears",
                    choiceB = "pop a wheelie",
                    choiceC = "stop the motorcycle",
                    choiceD = "remove power from the rear wheel",
                    correct = 'd'
                ),
                MultiChoiceCard(
                    cardId = 70,
                    question = "Which gear should the rider be in when coming to a stop?",
                    choiceA = "1st",
                    choiceB = "2nd",
                    choiceC = "neutral",
                    choiceD = "any",
                    correct = 'a'
                ),
                MultiChoiceCard(
                    cardId = 71,
                    question = "What is 'engine braking'?",
                    choiceA = "braking while the engine is on",
                    choiceB = "using only the rear brake",
                    choiceC = "using only the front brake",
                    choiceD = "using the engine to slow you down by downshifting and easing out the clutch",
                    correct = 'd'
                ),
                MultiChoiceCard(
                    cardId = 72,
                    question = "Which type of helmet provides the most protection in case of a crash?",
                    choiceA = "half-shell helmet",
                    choiceB = "three-quarter helmet",
                    choiceC = "modular or flip-up helmet",
                    choiceD = "full-face helmet",
                    correct = 'd'
                ),
                MultiChoiceCard(
                    cardId = 73,
                    question = "Motorcycle helmets should be ____.",
                    choiceA = "OSHA compliant",
                    choiceB = "FDA compliant",
                    choiceC = "DOT compliant",
                    choiceD = "NFPA compliant",
                    correct = 'c'
                ),
                MultiChoiceCard(
                    cardId = 74,
                    question = "Counter-weighting is used...",
                    choiceA = "in low-speed tight turns",
                    choiceB = "in high-speed turns",
                    choiceC = "in every turn",
                    choiceD = "when braking",
                    correct = 'a'
                ),
                MultiChoiceCard(
                    cardId = 75,
                    question = "Why is it important to do a head check when changing lanes?",
                    choiceA = "it shows other drivers your intention",
                    choiceB = "someone might be in your blind spot",
                    choiceC = "it helps the motorcycle lean",
                    choiceD = "it slows you down",
                    correct = 'b'
                ),
                MultiChoiceCard(
                    cardId = 76,
                    question = "The 'friction zone' on a motorcycle is:",
                    choiceA = "where the motor starts putting power to the rear wheel through the clutch",
                    choiceB = "when the rear wheel spins, sliding on its side",
                    choiceC = "popping a wheelie",
                    choiceD = "none of the above",
                    correct = 'a'
                ),
                MultiChoiceCard(
                    cardId = 77,
                    question = "Why should you rise up slightly off of the seat when crossing over an obstacle?",
                    choiceA = "to keep the obstacle from moving",
                    choiceB = "to see better",
                    choiceC = "to use the legs and knees as shock absorbers",
                    choiceD = "to maintain momentum",
                    correct = 'c'
                ),
                MultiChoiceCard(
                    cardId = 78,
                    question = "Why is riding a motorcycle more dangerous than driving a car?",
                    choiceA = "less visibility",
                    choiceB = "less protection",
                    choiceC = "less stability",
                    choiceD = "all of the above",
                    correct = 'd'
                ),
                MultiChoiceCard(
                    cardId = 79,
                    question = "In seconds, what is a following distance that generally provides enough time to stop or swerve in an urgent situation?",
                    choiceA = "2 seconds",
                    choiceB = "4 seconds",
                    choiceC = "12 seconds",
                    choiceD = "20 seconds",
                    correct = 'b'
                ),
                MultiChoiceCard(
                    cardId = 80,
                    question = "How much more dangerous is riding a motorcycle compared to a car?",
                    choiceA = "about 4 times more dangerous",
                    choiceB = "about 30 times more dangerous",
                    choiceC = "it's less dangerous",
                    choiceD = "they have the same amount of danger",
                    correct = 'b'
                ),
                MultiChoiceCard(
                    cardId = 81,
                    question = "What are the requirements for wearing a helmet?",
                    choiceA = "passengers only are required to wear helmets.",
                    choiceB = "All motorcycle riders and passengers are required to wear helmets at all times.",
                    choiceC = "Helmets are not required while driving on city streets.",
                    choiceD = "e",
                    correct = 'b'
                ),
                MultiChoiceCard(
                    cardId = 82,
                    question = "Grabbing the front brake or jamming down on the rear brake:",
                    choiceA = "Can cause the brakes to lock.",
                    choiceB = "Is the best way to stop in an emergency.",
                    choiceC = "Is the best way to slow down when the streets are wet",
                    choiceD = "",
                    correct = 'a'
                ),
                MultiChoiceCard(
                    cardId = 83,
                    question = "Passengers on motorcycle should:",
                    choiceA = "Put their feet on the ground when the motorcycle is stopped.",
                    choiceB = "Not ride without a backrest.",
                    choiceC = "Should sit as far forward as possible without crowding you.",
                    choiceD = "",
                    correct = 'b'
                ),
                MultiChoiceCard(
                    cardId = 84,
                    question = "When riding with a group of motorcyclists, a staggered formation:",
                    choiceA = "Is recommended at all times.",
                    choiceB = "Should not be used when entering or exiting a highway.",
                    choiceC = "Should be used when riding on curves.",
                    choiceD = "",
                    correct = 'b'
                ),
                MultiChoiceCard(
                    cardId = 85,
                    question = "Eye protection:",
                    choiceA = "Is not needed if your motorcycle is equipped with a windshield.",
                    choiceB = "Is only needed when riding in bad weather.",
                    choiceC = "Should give a clear view to either side.",
                    choiceD = "",
                    correct = 'c'
                ),
                MultiChoiceCard(
                    cardId = 86,
                    question = "A primary cause of single vehicle motorcycle collisions is:",
                    choiceA = "Motorcyclists' tendency to drive too fast for weather conditions",
                    choiceB = "Motorcyclists running wide in a curve or turn and colliding with the roadway or a fixed object.",
                    choiceC = "Motorcyclists running off the road while trying to avoid a collision with another car",
                    choiceD = "",
                    correct = 'b'
                ),
                MultiChoiceCard(
                    cardId = 87,
                    question = "When riding at night you should:",
                    choiceA = "Move closer to the vehicle in front of you to use its lights to see farther down the road.",
                    choiceB = "Keep driving at your normal speed because slowing down would increase the chance of being struck from behind.",
                    choiceC = "Reduce your speed because it is harder to see something lying in the road.",
                    choiceD = "",
                    correct = 'c'
                ),
                MultiChoiceCard(
                    cardId = 88,
                    question = "You should operate the engine cut-off switch and pull in the clutch when:",
                    choiceA = "The throttle is stuck and you can't free it.",
                    choiceB = "You start to lose control in a curve.",
                    choiceC = "The motorcycle starts to wobble.",
                    choiceD = "",
                    correct = 'a'
                ),
                MultiChoiceCard(
                    cardId = 89,
                    question = "To operate a moped, you must have at least a ____ license:",
                    choiceA = "Class M1",
                    choiceB = "Class M2",
                    choiceC = "Class C",
                    choiceD = "",
                    correct = 'b'
                ),
                MultiChoiceCard(
                    cardId = 90,
                    question = "Up-shifting or downshifting in a curve:",
                    choiceA = "Should only be done if it can be done smoothly",
                    choiceB = "Is better than shifting before the curve",
                    choiceC = "Is the best way to control your speed",
                    choiceD = "",
                    correct = 'a'
                ),
                MultiChoiceCard(
                    cardId = 91,
                    question = "To avoid confusing other drivers you should:",
                    choiceA = "Increase the following distance between your motorcycle and the vehicle in front of you if you are being tailgated",
                    choiceB = "Make sure your turn signal turns off after you finish a turn",
                    choiceC = "Use your horn only in emergency situations",
                    choiceD = "",
                    correct = 'b'
                ),
                MultiChoiceCard(
                    cardId = 92,
                    question = "____ is a major factor in collisions caused by motorcycles:",
                    choiceA = "Following too closely",
                    choiceB = "Lane sharing",
                    choiceC = "Not being seen by other drivers",
                    choiceD = "",
                    correct = 'a'
                ),
                MultiChoiceCard(
                    cardId = 93,
                    question = "If you must carry a load, it should be:",
                    choiceA = "Either over or in front of the rear axle",
                    choiceB = "Carried on the gas tank in front of the driver",
                    choiceC = "Piled up against a sissy bar or frame on the back of the seat",
                    choiceD = "",
                    correct = 'a'
                ),
                MultiChoiceCard(
                    cardId = 94,
                    question = "What is the best way to stay out of trouble while riding a motorcycle?",
                    choiceA = "By avoiding high density traffic areas",
                    choiceB = "By increasing the following distance between your motorcycle and the vehicle in front of you",
                    choiceC = "To see a coming by looking well ahead",
                    choiceD = "",
                    correct = 'c'
                ),
                MultiChoiceCard(
                    cardId = 95,
                    question = "A motorcycle rider has an advantage over an automobile driver when passing parked vehicles because:",
                    choiceA = "A motorcycle can accelerate faster than a car",
                    choiceB = "A motorcycle rider can avoid the problems of opening doors and people stepping out from between vehicles like driving in the left part of the lane",
                    choiceC = "Motorcycles have a shorter stopping distance",
                    choiceD = "",
                    correct = 'b'
                ),
                MultiChoiceCard(
                    cardId = 96,
                    question = "To execute a turn safely, a motorcycle rider should always:",
                    choiceA = "Lean the motorcycle in the direction of the curve or turn",
                    choiceB = "Slow down in the turn",
                    choiceC = "Turn using the handlebars only",
                    choiceD = "",
                    correct = 'a'
                )
            )

            val threeFC = ThreeFieldCard(
                cardId = 97,
                question = "What should you do when you get a stuck throttle?",
                middle = "if the throttle cable is stuck: twist the throttle back and forth several times this may free it.",
                answer = "If that doesn't work: immediately operate the engine cutoff, switch and pull in the clutch at the same time.",
            )

            for (card in motorCycleCards) {
                cardDao.insertCard(card)
            }
            for (multiCard in multiChoiceCards) {
                multiChoiceCardDao.insertMultiChoiceCard(multiCard)
            }

            cardDao.insertCard(
                Card(
                    deckId = mcDeck,
                    deckUUID = mcUuid,
                    nextReview = nextReviewDate,
                    passes = 0,
                    prevSuccess = false,
                    totalPasses = 0,
                    type = "three",
                    reviewsLeft = motorcycleDeck.reviewAmount
                )
            )
            threeCardDao.insertThreeCard(threeFC)
        }
    }
}

