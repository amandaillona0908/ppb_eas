package com.example.waroenglegitmembership.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions WHERE memberId = :memberId ORDER BY id DESC")
    fun getTransactionsByMember(memberId: Int): Flow<List<Transaction>>

    @Insert
    suspend fun insertTransaction(transaction: Transaction)
}