@file:RequiresApi(Build.VERSION_CODES.O)

package com.belmontCrest.cardCrafter.localDatabase.tables

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


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

@Entity(
    tableName = "syncedDeckInfo"
)
data class SyncedDeckInfo(
    @PrimaryKey val uuid: String,
    val lastUpdatedOn: String
)

@Entity(
    tableName = "pwd"
)
data class Pwd(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val password: Encryption
)
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

        val kgen = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE
        )
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(false) // No need for biometrics screen.
            .build()
        kgen.init(spec)
        return kgen.generateKey()
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

private val pgFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSxx")


fun String.toInstant() = OffsetDateTime.parse(this, pgFmt).toInstant()!!
/** Example
fun thisFunc() {
// usage
val ts1 = "2025-04-09 23:48:34.411857+00".toInstant()
val ts2 = "2025-04-10 01:12:00.000000+00".toInstant()

val newer = ts1?.let { if (it > ts2) ts1 else ts2 }       // or ts1.isAfter(ts2)

} */