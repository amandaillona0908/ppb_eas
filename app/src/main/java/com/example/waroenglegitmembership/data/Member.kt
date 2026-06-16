package com.example.waroenglegitmembership.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity tabel "members". Level dihitung otomatis dari total poin.
 *  - Bronze : 0–49   (< Rp500rb)
 *  - Silver : 50–149 (Rp500rb–1,49jt)
 *  - Gold   : 150+   (Rp1,5jt+)
 *
 * passwordHash menyimpan hash SHA-256 dari password (bukan teks asli),
 * sehingga password tidak pernah tersimpan dalam bentuk yang bisa dibaca.
 */
@Entity(tableName = "members")
data class Member(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String,
    val passwordHash: String = "",
    val points: Int = 0
) {
    val level: String
        get() = when {
            points >= 150 -> "Gold"
            points >= 50 -> "Silver"
            else -> "Bronze"
        }
}