package com.example.flashcards.supabase.controller

import at.favre.lib.crypto.bcrypt.BCrypt


/** Might Come in handy Later
fun hashPasswordBcrypt(password: String): String {
    return BCrypt.withDefaults()
        .hashToString(10, password.toCharArray())
}

fun comparePassword(password: String, hashedPassword: String): Boolean {
    return BCrypt.verifyer().verify(
        password.toCharArray(), hashedPassword
    ).verified
} */