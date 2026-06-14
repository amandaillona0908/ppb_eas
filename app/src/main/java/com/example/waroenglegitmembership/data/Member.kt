package com.example.waroenglegitmembership.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entity = representasi tabel di database.
// @Entity menandai class ini sebagai tabel bernama "members".
@Entity(tableName = "members")
data class Member(
    @PrimaryKey(autoGenerate = true) // id otomatis bertambah 1,2,3,...
    val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String,
    val points: Int = 0 // poin awal member = 0
) {
    // Level member dihitung dari total poin (tier system).
    // Ini bukan kolom database, tapi properti turunan.
    val level: String
        get() = when {
            points >= 500 -> "Gold"
            points >= 200 -> "Silver"
            else -> "Bronze"
        }
}
