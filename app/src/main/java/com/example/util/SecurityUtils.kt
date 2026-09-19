package com.example.util

import java.security.MessageDigest

object SecurityUtils {
    private const val SALT = "ShajeenTravelAgencySecretSalt2026"

    fun hashPassword(password: String): String {
        val input = "$password:$SALT"
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(password: String, storedHash: String): Boolean {
        return hashPassword(password) == storedHash
    }
}
