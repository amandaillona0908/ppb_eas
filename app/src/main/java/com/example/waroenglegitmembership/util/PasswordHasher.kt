package com.example.waroenglegitmembership.util

import java.security.MessageDigest

/**
 * Utilitas hashing password dengan SHA-256.
 * Password asli tidak pernah disimpan — hanya hash-nya.
 *
 * Catatan: untuk aplikasi produksi sebaiknya pakai algoritma khusus password
 * seperti bcrypt/Argon2 dengan salt. SHA-256 dipakai di sini agar sederhana
 * dan tidak butuh library tambahan (sesuai konteks aplikasi lokal).
 */
object PasswordHasher {

    fun hash(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verify(password: String, hash: String): Boolean = hash(password) == hash

    /**
     * Password default saat member baru didaftarkan barista.
     * Contoh: member id 1 -> "waroeng1". Customer dianjurkan menggantinya.
     */
    fun defaultPassword(memberId: Int): String = "waroeng$memberId"
}