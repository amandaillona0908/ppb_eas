package com.example.waroenglegitmembership.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
     * bisa memberitahukannya ke customer.
     */
    fun registerMember(
        name: String,
        email: String,
        phone: String,
        onResult: (defaultPassword: String, memberId: Int) -> Unit
    ) = viewModelScope.launch {
        // Simpan dulu untuk mendapat ID, baru set password default berbasis ID.
        val newId = repository.insertMember(
            Member(name = name, email = email, phone = phone, passwordHash = "")
        ).toInt()
        val defaultPass = PasswordHasher.defaultPassword(newId)
        repository.updateMember(
            Member(
                id = newId, name = name, email = email, phone = phone,
                passwordHash = PasswordHasher.hash(defaultPass)
            )
        )
        onResult(defaultPass, newId)
    }

    /** True jika member masih memakai password default (belum diganti). */
    fun isDefaultPassword(member: Member): Boolean =
        PasswordHasher.verify(PasswordHasher.defaultPassword(member.id), member.passwordHash)

    /** Verifikasi apakah [password] cocok dengan password member saat ini. */
    fun verifyPassword(member: Member, password: String): Boolean =
        PasswordHasher.verify(password, member.passwordHash)

    fun updateProfile(member: Member, name: String, email: String, phone: String) =
        viewModelScope.launch {
            repository.updateMember(member.copy(name = name, email = email, phone = phone))
        }

    /** Ganti password member. Password baru di-hash sebelum disimpan. */
    fun updatePassword(member: Member, newPassword: String) = viewModelScope.launch {
        repository.updateMember(member.copy(passwordHash = PasswordHasher.hash(newPassword)))
    }

    fun deleteMember(member: Member) = viewModelScope.launch {
        repository.deleteMember(member)
    }

    /**
     * Login customer: cari member by email lalu verifikasi password.
     * Hasil:
     *  - LoginResult.Success(member) jika email & password cocok
     *  - LoginResult.WrongPassword  jika email ada tapi password salah
     *  - LoginResult.NotFound       jika email tidak terdaftar
     */
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

    /** Catat pembelian + tambah poin (1 poin = Rp10.000). */
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
    }

    /** Tukar reward: kurangi poin + catat sebagai REDEEM. */
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
                onResult(true)
            } else {
                onResult(false)
            }
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