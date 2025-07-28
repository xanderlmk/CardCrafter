package com.belmontCrest.cardCrafter.localDatabase.tables

import android.os.Parcelable
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
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
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


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

data class Encryption(val pd: String)

class EncryptionConverter {
    @TypeConverter
    fun encrypt(enc: Encryption): String {
        return Encryptor.encryptString(enc.pd)
    }

    @TypeConverter
    fun decrypt(str: String): Encryption {
        return Encryption(Encryptor.decryptString(str))
    }
}

@OptIn(ExperimentalEncodingApi::class)
object Encryptor {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "pwd_key"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val IV_LEN = 12        // 96‑bit IV for GCM
    private const val TAG_LEN = 128      // authentication tag length in bits

    /** Returns (creates if missing) the AES key that lives in Keystore */
    private fun getKey(): SecretKey {
        val ks = java.security.KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        ks.getKey(KEY_ALIAS, null)?.let { return it as SecretKey }

        val kGen = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE
        )
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(false) // No need for biometrics screen.
            .build()
        kGen.init(spec)
        return kGen.generateKey()
    }

    @TypeConverter
    fun encryptString(plain: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        val iv = cipher.iv                     // 12‑byte random IV auto‑generated
        val cipherText = cipher.doFinal(plain.toByteArray())
        // store iv + ciphertext together:  IV || C
        val combined = ByteArray(iv.size + cipherText.size).apply {
            System.arraycopy(iv, 0, this, 0, iv.size)
            System.arraycopy(cipherText, 0, this, iv.size, cipherText.size)
        }
        return Base64.encode(combined)
    }

    @TypeConverter
    fun decryptString(encoded: String): String {
        val combined = Base64.decode(encoded)
        val iv = combined.copyOfRange(0, IV_LEN)
        val cipherText = combined.copyOfRange(IV_LEN, combined.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(TAG_LEN, iv)
        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)
        val plain = cipher.doFinal(cipherText)
        return String(plain)
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