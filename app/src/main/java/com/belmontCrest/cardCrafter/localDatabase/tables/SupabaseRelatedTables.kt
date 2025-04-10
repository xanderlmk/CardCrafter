@file:RequiresApi(Build.VERSION_CODES.O)
package com.belmontCrest.cardCrafter.localDatabase.tables

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


@Entity(
    tableName = "importedDeckInfo",
    foreignKeys = [
        ForeignKey(
            entity = Deck::class,
            parentColumns = ["uuid"],
            childColumns = ["uuid"],
            onDelete = CASCADE
        )
    ],
    indices = [Index(value = ["uuid"])]
)
data class ImportedDeckInfo(
    @PrimaryKey val uuid : String,
    val lastUpdatedOn : String,
)

@Entity(
    tableName = "syncedDeckInfo",
    foreignKeys = [
        ForeignKey(
            entity = Deck::class,
            parentColumns = ["uuid"],
            childColumns = ["uuid"],
            onDelete = CASCADE
        )
    ],
    indices = [Index(value = ["uuid"])]
)
data class SyncedDeckInfo(
    @PrimaryKey val uuid: String,
    val lastUpdatedOn: String
)

private val pgFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSxx")


fun String.toInstant() = OffsetDateTime.parse(this, pgFmt).toInstant()!!
/** Example
fun thisFunc() {
    // usage
    val ts1 = "2025-04-09 23:48:34.411857+00".toInstant()
    val ts2 = "2025-04-10 01:12:00.000000+00".toInstant()

    val newer = ts1?.let { if (it > ts2) ts1 else ts2 }       // or ts1.isAfter(ts2)

} */