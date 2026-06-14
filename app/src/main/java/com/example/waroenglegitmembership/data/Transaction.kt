package com.example.waroenglegitmembership.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabel transactions — mencatat setiap pembelian member.
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val memberId: Int,        // menghubungkan transaksi ke member tertentu
    val amount: Double,       // nominal pembelian (Rupiah)
    val pointEarned: Int,     // poin yang didapat dari transaksi ini
    val date: String          // tanggal transaksi (disimpan sebagai teks)
)
