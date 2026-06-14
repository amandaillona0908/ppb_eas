package com.example.waroenglegitmembership.repository

import com.example.waroenglegitmembership.data.*
import kotlinx.coroutines.flow.Flow

// Repository = jembatan antara ViewModel dan Database (DAO).
// ViewModel tidak akses DAO langsung, tapi lewat repository ini.
// Tujuannya: memisahkan logika data dari logika UI (Repository Pattern).
class MembershipRepository(
    private val memberDao: MemberDao,
    private val transactionDao: TransactionDao
) {
    // ---- Member ----
    val allMembers: Flow<List<Member>> = memberDao.getAllMembers()

    fun getMemberById(id: Int): Flow<Member?> = memberDao.getMemberById(id)

    suspend fun insertMember(member: Member) = memberDao.insertMember(member)

    suspend fun deleteMember(member: Member) = memberDao.deleteMember(member)

    suspend fun updatePoints(memberId: Int, newPoints: Int) =
        memberDao.updatePoints(memberId, newPoints)

    // ---- Transaction ----
    fun getTransactions(memberId: Int): Flow<List<Transaction>> =
        transactionDao.getTransactionsByMember(memberId)

    suspend fun insertTransaction(transaction: Transaction) =
        transactionDao.insertTransaction(transaction)
}
