package com.belmontCrest.cardCrafter.localDatabase.tables

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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
    @PrimaryKey val uuid: String,
    val lastUpdatedOn: String,
)

@Entity(tableName = "syncedDeckInfo")
data class SyncedDeckInfo(
    @PrimaryKey val uuid: String,
    val lastUpdatedOn: String
)

@Entity(
    tableName = "card_info",
    foreignKeys = [
        ForeignKey(
            entity = Card::class,
            parentColumns = ["cardIdentifier"],
            childColumns = ["card_identifier"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ],
    indices = [Index(value = ["card_identifier"])]
)
data class CardInfo(
    @ColumnInfo(name = "card_identifier")
    @PrimaryKey val cardIdentifier: String,
    @ColumnInfo(name = "is_local")
    val isLocal: Boolean
)

@Entity(tableName = "pwd")
data class Pwd(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val password: Encryption
)

fun String.toInstant(): Instant {
    return try {
        OffsetDateTime.parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant()
    } catch (e: DateTimeParseException) {
        try {
            Log.e("String.toInstant()", "Error: $e,trying 1")
            OffsetDateTime.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSZ")).toInstant()
        } catch (e: DateTimeParseException) {
            try {
                Log.e("String.toInstant()", "Error: $e,trying 2")

                OffsetDateTime.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSxxx")).toInstant()
            } catch (e: DateTimeParseException) {
                Log.e("String.toInstant()", "Error: $e,trying 3")
                Log.e("PersonalDeckSyncRepo", "Failed to parse timestamp: $this")
                Instant.now()
            }
        }
    }
}