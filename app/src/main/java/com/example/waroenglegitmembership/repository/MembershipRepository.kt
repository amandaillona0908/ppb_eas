package com.example.waroenglegitmembership.repository

import com.example.waroenglegitmembership.data.*
import kotlinx.coroutines.flow.Flow

/** Jembatan antara ViewModel dan Room (Repository Pattern). */
class MembershipRepository(
    private val memberDao: MemberDao,
    private val transactionDao: TransactionDao
) {
    val allMembers: Flow<List<Member>> = memberDao.getAllMembers()

    fun getMemberById(id: Int): Flow<Member?> = memberDao.getMemberById(id)

    fun getTransactions(memberId: Int): Flow<List<Transaction>> =
        transactionDao.getTransactionsByMember(memberId)

    suspend fun findByEmail(email: String): Member? = memberDao.findByEmail(email)

    suspend fun insertMember(member: Member): Long = memberDao.insertMember(member)

    suspend fun updateMember(member: Member) = memberDao.updateMember(member)

    suspend fun deleteMember(member: Member) = memberDao.deleteMember(member)

    suspend fun updatePoints(memberId: Int, newPoints: Int) =
        memberDao.updatePoints(memberId, newPoints)

    suspend fun insertTransaction(transaction: Transaction) =
        transactionDao.insertTransaction(transaction)
}