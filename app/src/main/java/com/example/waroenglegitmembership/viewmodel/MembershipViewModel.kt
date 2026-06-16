package com.example.waroenglegitmembership.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waroenglegitmembership.data.Member
import com.example.waroenglegitmembership.data.Transaction
import com.example.waroenglegitmembership.data.TxType
import com.example.waroenglegitmembership.repository.MembershipRepository
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

    fun registerMember(name: String, email: String, phone: String) = viewModelScope.launch {
        repository.insertMember(Member(name = name, email = email, phone = phone))
    }

    fun updateProfile(member: Member, name: String, email: String, phone: String) =
        viewModelScope.launch {
            repository.updateMember(member.copy(name = name, email = email, phone = phone))
        }

    fun deleteMember(member: Member) = viewModelScope.launch {
        repository.deleteMember(member)
    }

    fun loginByEmail(email: String, onResult: (Member?) -> Unit) = viewModelScope.launch {
        onResult(repository.findByEmail(email.trim()))
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