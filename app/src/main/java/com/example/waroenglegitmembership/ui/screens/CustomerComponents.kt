package com.example.waroenglegitmembership.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.waroenglegitmembership.data.Member
import com.example.waroenglegitmembership.data.Transaction
import com.example.waroenglegitmembership.data.TxType
import com.example.waroenglegitmembership.ui.theme.WL
import com.example.waroenglegitmembership.viewmodel.Reward

/** Riwayat aktivitas poin: pembelian (+) dan redeem (-). */
@Composable
fun HistoryTab(transactions: List<Transaction>) {
    if (transactions.isEmpty()) {
        EmptyHint("🧾", "Belum ada aktivitas", "Transaksi & penukaran poin kamu akan muncul di sini.")
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(transactions) { trx -> TransactionRow(trx) }
    }
}

@Composable
private fun TransactionRow(trx: Transaction) {
    val isPurchase = trx.type == TxType.PURCHASE
    val icon = if (isPurchase) "🛒" else "🎁"
    val iconBg = if (isPurchase) WL.GulaSoft else WL.KleponSoft
    val pointColor = if (isPurchase) WL.Pandan else WL.GulaMerah
    val pointBg = if (isPurchase) WL.PandanSoft else WL.KleponSoft
    val sign = if (trx.pointChange >= 0) "+" else "" // negatif sudah punya tanda minus

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WL.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(12.dp), color = iconBg) {
                    Text(icon, fontSize = 22.sp, modifier = Modifier.padding(8.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(trx.title, fontWeight = FontWeight.Bold)
                    Text(trx.date, color = WL.TeksRedup, fontSize = 12.sp)
                }
            }
            Surface(shape = RoundedCornerShape(20.dp), color = pointBg) {
                Text(
                    "$sign${trx.pointChange} poin",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    color = pointColor, fontWeight = FontWeight.Bold, fontSize = 13.sp
                )
            }
        }
    }
}

/** Tukar reward dengan poin (FR-06). */
@Composable
fun RewardTab(member: Member, rewards: List<Reward>, onRedeem: (Reward) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { PointBanner(member.points) }
        items(rewards) { reward ->
            RewardRow(reward, affordable = member.points >= reward.cost, onRedeem = { onRedeem(reward) })
        }
    }
}

@Composable
private fun PointBanner(points: Int) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = WL.Kunyit)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Poin Kamu", color = WL.Coklat.copy(alpha = 0.7f), fontSize = 13.sp)
                Text("$points", fontWeight = FontWeight.Black, fontSize = 32.sp, color = WL.Coklat)
            }
            Text("🎁", fontSize = 44.sp)
        }
    }
}

@Composable
private fun RewardRow(reward: Reward, affordable: Boolean, onRedeem: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = WL.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(14.dp), color = WL.GulaSoft) {
                    Text(reward.emoji, fontSize = 28.sp, modifier = Modifier.padding(8.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(reward.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("${reward.cost} poin", color = WL.TeksRedup, fontSize = 13.sp)
                }
            }
            Button(
                onClick = onRedeem,
                enabled = affordable,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WL.Sage, contentColor = WL.Charcoal),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text(if (affordable) "Tukar" else "Kurang", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}