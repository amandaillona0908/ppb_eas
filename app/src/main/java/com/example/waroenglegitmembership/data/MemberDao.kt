package com.example.waroenglegitmembership.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {

    @Query("SELECT * FROM members ORDER BY id DESC")
    fun getAllMembers(): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE id = :memberId")
    fun getMemberById(memberId: Int): Flow<Member?>

    @Query("SELECT * FROM members WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): Member?

    @Insert
    suspend fun insertMember(member: Member): Long

    @Update
    suspend fun updateMember(member: Member)

    @Delete
    suspend fun deleteMember(member: Member)

    @Query("UPDATE members SET points = :newPoints WHERE id = :memberId")
    suspend fun updatePoints(memberId: Int, newPoints: Int)
}