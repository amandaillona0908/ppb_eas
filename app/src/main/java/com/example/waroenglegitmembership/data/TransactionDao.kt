package com.example.waroenglegitmembership.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions WHERE memberId = :memberId ORDER BY id DESC")
    fun getTransactionsByMember(memberId: Int): Flow<List<Transaction>>

    @Query("SELECT COUNT(*) FROM transactions WHERE memberId = :memberId AND type = 'PURCHASE'")
    suspend fun countPurchases(memberId: Int): Int

    @Query("SELECT COUNT(*) FROM transactions WHERE memberId = :memberId AND type = 'REDEEM'")
    suspend fun countRedeems(memberId: Int): Int

    @Insert
    suspend fun insertTransaction(transaction: Transaction)
}