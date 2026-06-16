package com.example.waroenglegitmembership.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity tabel "members".
 *
 * level disimpan (bukan dihitung dari poin) karena kenaikan level kini
 * ditentukan oleh penyelesaian misi (lihat LevelSystem). Level hanya naik,
 * tidak pernah turun meski poin berkurang. Member baru selalu mulai "Bronze".
 */
@Entity(tableName = "members")
data class Member(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String,
    val passwordHash: String = "",
    val points: Int = 0,
    val level: String = "Bronze"
)