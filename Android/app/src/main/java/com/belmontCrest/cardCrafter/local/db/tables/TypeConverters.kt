package com.belmontCrest.cardCrafter.local.db.tables

import android.os.Parcelable
import androidx.room.TypeConverter
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.json.JSONArray
import java.util.Date

@Serializable(with = PartOfQorASerializer::class)
@Parcelize
sealed class PartOfQorA : Parcelable {
    data object Q : PartOfQorA()

    data object A : PartOfQorA()
}

object PartOfQorASerializer : KSerializer<PartOfQorA> {
    override val descriptor = PrimitiveSerialDescriptor("PartOfQorA", PrimitiveKind.BOOLEAN)

    override fun serialize(encoder: Encoder, value: PartOfQorA) {
        encoder.encodeBoolean(value is PartOfQorA.Q)
    }

    override fun deserialize(decoder: Decoder): PartOfQorA {
        return if (decoder.decodeBoolean()) PartOfQorA.Q else PartOfQorA.A
    }
}

class ListStringConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {

        if (value == "none") {
            return emptyList()
        }
        val jsonArray = JSONArray(value)
        val list = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getString(i))
        }
        return list
    }

    @TypeConverter
    fun listToString(listOfStrings: List<String>): String {
        if (listOfStrings.isEmpty()) {
            return "none"
        }
        return JSONArray(listOfStrings).toString()
    }
}

class QOrAConverter {
    @TypeConverter
    fun fromBoolean(value: Boolean): PartOfQorA {
        return if (value) PartOfQorA.Q else PartOfQorA.A
    }

    @TypeConverter
    fun toBoolean(value: PartOfQorA): Boolean {
        return value is PartOfQorA.Q
    }
}

class TimeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}