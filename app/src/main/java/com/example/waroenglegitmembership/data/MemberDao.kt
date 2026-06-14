package com.example.waroenglegitmembership.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// DAO (Data Access Object) = kumpulan fungsi untuk akses tabel members.
// Room otomatis bikin implementasinya, kita cukup deklarasi.
@Dao
interface MemberDao {

    // Flow = data otomatis update ke UI saat database berubah.
    @Query("SELECT * FROM members ORDER BY id DESC")
    fun getAllMembers(): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE id = :memberId")
    fun getMemberById(memberId: Int): Flow<Member?>

    // suspend = fungsi async, dijalankan di background (coroutine).
    @Insert
    suspend fun insertMember(member: Member)

    @Update
    suspend fun updateMember(member: Member)

    @Delete
    suspend fun deleteMember(member: Member)

    // Update poin member secara langsung (dipakai saat transaksi/redeem).
    @Query("UPDATE members SET points = :newPoints WHERE id = :memberId")
    suspend fun updatePoints(memberId: Int, newPoints: Int)
}
