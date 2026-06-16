package com.example.waroenglegitmembership.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Jenis aktivitas poin. */
enum class TxType { PURCHASE, REDEEM }

/**
 * Entity tabel "transactions". Mencatat aktivitas poin member:
 *  - PURCHASE: pembelian, poin bertambah (pointChange positif)
 *  - REDEEM  : tukar reward, poin berkurang (pointChange negatif)
 */
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val memberId: Int,
    val type: TxType,
    val title: String,        // "Rp 25.000" atau "Tukar Klepon"
    val pointChange: Int,     // +2 (purchase) atau -10 (redeem)
    val date: String
)