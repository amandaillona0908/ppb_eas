package com.example.waroenglegitmembership.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waroenglegitmembership.data.LevelSystem
import com.example.waroenglegitmembership.data.Member
import com.example.waroenglegitmembership.data.Transaction
import com.example.waroenglegitmembership.data.TxType
import com.example.waroenglegitmembership.repository.MembershipRepository
import com.example.waroenglegitmembership.util.PasswordHasher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/** Reward yang bisa ditukar dengan poin. */
data class Reward(val name: String, val cost: Int, val emoji: String)

class MembershipViewModel(private val repository: MembershipRepository) : ViewModel() {

    companion object {
        private const val RUPIAH_PER_POINT = 10_000
    }

    val members: StateFlow<List<Member>> = repository.allMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rewards = listOf(
        Reward("Klepon", 10, "🍡"),
        Reward("Tempe Mendoan", 15, "🍢"),
        Reward("Onde-onde", 20, "🟤"),
        Reward("Es Dawet", 25, "🥤"),
        Reward("Brownies Lumer", 40, "🍰")
    )

    fun getMember(id: Int): StateFlow<Member?> =
        repository.getMemberById(id)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun getTransactions(memberId: Int): StateFlow<List<Transaction>> =
        repository.getTransactions(memberId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Daftarkan member baru. Password default dibuat otomatis dari ID
     * (mis. "waroeng1"), lalu dikirim balik lewat onResult agar barista
     * bisa memberitahukannya ke customer. Level awal selalu Bronze.
     */
    fun registerMember(
        name: String,
        email: String,
        phone: String,
        onResult: (defaultPassword: String, memberId: Int) -> Unit
    ) = viewModelScope.launch {
        val newId = repository.insertMember(
            Member(name = name, email = email, phone = phone, passwordHash = "")
        ).toInt()
        val defaultPass = PasswordHasher.defaultPassword(newId)
        repository.updatePasswordHash(newId, PasswordHasher.hash(defaultPass))
        onResult(defaultPass, newId)
    }

    fun updateProfile(member: Member, name: String, email: String, phone: String) =
        viewModelScope.launch {
            repository.updateMember(member.copy(name = name, email = email, phone = phone))
        }

    /** Ganti password member, lalu cek apakah misi level terpenuhi. */
    fun updatePassword(member: Member, newPassword: String) = viewModelScope.launch {
        repository.updatePasswordHash(member.id, PasswordHasher.hash(newPassword))
        refreshLevel(member.id)
    }

    fun deleteMember(member: Member) = viewModelScope.launch {
        repository.deleteMember(member)
    }

    fun login(email: String, password: String, onResult: (LoginResult) -> Unit) =
        viewModelScope.launch {
            val member = repository.findByEmail(email.trim())
            val result = when {
                member == null -> LoginResult.NotFound
                PasswordHasher.verify(password, member.passwordHash) -> LoginResult.Success(member)
                else -> LoginResult.WrongPassword
            }
            onResult(result)
        }

    fun isDefaultPassword(member: Member): Boolean =
        PasswordHasher.verify(PasswordHasher.defaultPassword(member.id), member.passwordHash)

    fun verifyPassword(member: Member, password: String): Boolean =
        PasswordHasher.verify(password, member.passwordHash)

    /** Catat pembelian + tambah poin, lalu cek kenaikan level. */
    fun addTransaction(member: Member, amount: Double) = viewModelScope.launch {
        val pointEarned = (amount / RUPIAH_PER_POINT).toInt()
        repository.insertTransaction(
            Transaction(
                memberId = member.id,
                type = TxType.PURCHASE,
                title = "Rp ${formatThousands(amount.toLong())}",
                pointChange = pointEarned,
                date = now()
            )
        )
        repository.updatePoints(member.id, member.points + pointEarned)
        refreshLevel(member.id)
    }

    /** Tukar reward: kurangi poin + catat REDEEM, lalu cek level. */
    fun redeemReward(member: Member, reward: Reward, onResult: (Boolean) -> Unit) =
        viewModelScope.launch {
            if (member.points >= reward.cost) {
                repository.insertTransaction(
                    Transaction(
                        memberId = member.id,
                        type = TxType.REDEEM,
                        title = "Tukar ${reward.name}",
                        pointChange = -reward.cost,
                        date = now()
                    )
                )
                repository.updatePoints(member.id, member.points - reward.cost)
                refreshLevel(member.id)
                onResult(true)
            } else {
                onResult(false)
            }
        }

    /**
     * Hitung ulang level member berdasar misi yang sudah diselesaikan,
     * lalu simpan jika naik. Dipanggil setelah transaksi/redeem/ganti password.
     */
    private suspend fun refreshLevel(memberId: Int) {
        val m = repository.getMemberOnce(memberId) ?: return
        val purchases = repository.countPurchases(memberId)
        val redeems = repository.countRedeems(memberId)
        val passwordChanged =
            !PasswordHasher.verify(PasswordHasher.defaultPassword(m.id), m.passwordHash)
        val newLevel = LevelSystem.computeLevel(m.level, m.points, purchases, redeems, passwordChanged)
        if (newLevel != m.level) repository.updateLevel(memberId, newLevel)
    }

    private fun now(): String =
        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id")).format(Date())

    private fun formatThousands(value: Long): String =
        "%,d".format(value).replace(',', '.')
}

/** Hasil percobaan login customer. */
sealed interface LoginResult {
    data class Success(val member: com.example.waroenglegitmembership.data.Member) : LoginResult
    data object WrongPassword : LoginResult
    data object NotFound : LoginResult
}