package com.example.waroenglegitmembership.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waroenglegitmembership.data.Member
import com.example.waroenglegitmembership.data.Transaction
import com.example.waroenglegitmembership.repository.MembershipRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// ViewModel = tempat business logic & state. UI hanya "menonton" state ini.
class MembershipViewModel(private val repository: MembershipRepository) : ViewModel() {

    // Daftar semua member, otomatis update saat database berubah.
    // stateIn = mengubah Flow biasa jadi StateFlow yang bisa dibaca UI.
    val members: StateFlow<List<Member>> = repository.allMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ---- Reward yang tersedia (adaptasi dari PRD coffee shop) ----
    val rewards = listOf(
        Reward("Klepon", 50, "🍡"),
        Reward("Onde-onde", 100, "🟤"),
        Reward("Es Dawet", 120, "🥤"),
        Reward("Tempe Mendoan", 80, "🍢"),
        Reward("Brownies Lumer", 150, "🍰")
    )

    // FR-01: Registrasi member baru.
    fun registerMember(name: String, email: String, phone: String) {
        viewModelScope.launch {
            repository.insertMember(Member(name = name, email = email, phone = phone))
        }
    }

    fun deleteMember(member: Member) {
        viewModelScope.launch { repository.deleteMember(member) }
    }

    // Ambil 1 member berdasarkan id (untuk detail/kartu member).
    fun getMember(id: Int): StateFlow<Member?> =
        repository.getMemberById(id)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Ambil riwayat transaksi 1 member.
    fun getTransactions(memberId: Int): StateFlow<List<Transaction>> =
        repository.getTransactions(memberId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // FR-04: Tambah transaksi + hitung poin otomatis.
    // Formula PRD: 1 Point = Rp10.000
    fun addTransaction(member: Member, amount: Double) {
        viewModelScope.launch {
            val pointEarned = (amount / 10000).toInt()
            val date = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id")).format(Date())

            repository.insertTransaction(
                Transaction(
                    memberId = member.id,
                    amount = amount,
                    pointEarned = pointEarned,
                    date = date
                )
            )
            // Tambah poin ke member.
            repository.updatePoints(member.id, member.points + pointEarned)
        }
    }

    // FR-06: Redeem reward. Mengurangi poin member.
    // Mengembalikan true jika berhasil, false jika poin kurang.
    fun redeemReward(member: Member, reward: Reward, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            if (member.points >= reward.cost) {
                repository.updatePoints(member.id, member.points - reward.cost)
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }
}

// Data class sederhana untuk reward (tidak masuk database).
data class Reward(val name: String, val cost: Int, val emoji: String)
