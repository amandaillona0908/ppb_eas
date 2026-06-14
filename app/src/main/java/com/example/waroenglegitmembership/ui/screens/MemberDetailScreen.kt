package com.example.waroenglegitmembership.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.waroenglegitmembership.data.Member
import com.example.waroenglegitmembership.data.Transaction
import com.example.waroenglegitmembership.viewmodel.MembershipViewModel
import com.example.waroenglegitmembership.viewmodel.Reward
import kotlinx.coroutines.launch

// Member Detail = gabungan Membership Card, Transaction, History, Reward
// (sesuai Navigation Structure PRD).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDetailScreen(
    memberId: Int,
    viewModel: MembershipViewModel,
    onBack: () -> Unit
) {
    val member by viewModel.getMember(memberId).collectAsState()
    val transactions by viewModel.getTransactions(memberId).collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Kartu", "Transaksi", "Riwayat", "Reward")
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail Member", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Tab navigasi antar fungsi member.
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontSize = 13.sp) }
                    )
                }
            }

            // Tampilkan konten sesuai tab. member bisa null saat loading.
            member?.let { m ->
                when (selectedTab) {
                    0 -> MembershipCardTab(m)
                    1 -> TransactionTab(m, onAdd = { amount ->
                        viewModel.addTransaction(m, amount)
                        scope.launch { snackbarHostState.showSnackbar("Transaksi tercatat! +${(amount/10000).toInt()} poin") }
                    })
                    2 -> HistoryTab(transactions)
                    3 -> RewardTab(m, viewModel.rewards, onRedeem = { reward ->
                        viewModel.redeemReward(m, reward) { success ->
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (success) "Berhasil tukar ${reward.name}! 🎉"
                                    else "Poin tidak cukup 😔"
                                )
                            }
                        }
                    })
                }
            }
        }
    }
}

// ---- TAB 1: Membership Card (FR-03) ----
@Composable
fun MembershipCardTab(member: Member) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth().height(220.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.linearGradient(
                        listOf(Color(0xFFD4730A), Color(0xFFF5A623))
                    )
                ).padding(24.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween) {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("WAROENG LEGIT", color = Color.White,
                                fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("Member Card", color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp)
                        }
                        Text("🍡", fontSize = 32.sp)
                    }
                    Column {
                        Text(member.name, color = Color.White,
                            fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Text("ID: WL-${member.id.toString().padStart(4, '0')}",
                            color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom) {
                            Column {
                                Text("LEVEL", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                                Text(member.level, color = Color.White,
                                    fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("TOTAL POIN", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                                Text("${member.points}", color = Color.White,
                                    fontWeight = FontWeight.Black, fontSize = 24.sp)
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        InfoRow("Email", member.email)
        InfoRow("Nomor HP", member.phone)
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

// ---- TAB 2: Add Transaction (FR-04) ----
@Composable
fun TransactionTab(member: Member, onAdd: (Double) -> Unit) {
    var amount by remember { mutableStateOf("") }
    val amountValue = amount.toDoubleOrNull() ?: 0.0
    val pointPreview = (amountValue / 10000).toInt()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Catat Transaksi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Formula: 1 poin = Rp10.000", color = Color.Gray, fontSize = 13.sp)

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it.filter { c -> c.isDigit() } },
            label = { Text("Nominal Pembelian (Rp)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), singleLine = true
        )

        if (amountValue > 0) {
            Card(shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Text("Member akan dapat $pointPreview poin",
                    modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
            }
        }

        Button(
            onClick = { onAdd(amountValue); amount = "" },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = amountValue > 0,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Simpan Transaksi", fontWeight = FontWeight.Bold)
        }
    }
}

// ---- TAB 3: Transaction History (FR-05) ----
@Composable
fun HistoryTab(transactions: List<Transaction>) {
    if (transactions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Belum ada transaksi.", color = Color.Gray)
        }
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(transactions) { trx ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Rp ${formatRupiah(trx.amount.toLong())}",
                            fontWeight = FontWeight.Bold)
                        Text(trx.date, color = Color.Gray, fontSize = 12.sp)
                    }
                    Text("+${trx.pointEarned} poin",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ---- TAB 4: Redeem Reward (FR-06) ----
@Composable
fun RewardTab(member: Member, rewards: List<Reward>, onRedeem: (Reward) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Poin kamu:", fontWeight = FontWeight.Bold)
                    Text("${member.points} poin", fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        items(rewards) { reward ->
            val affordable = member.points >= reward.cost
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(reward.emoji, fontSize = 32.sp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(reward.name, fontWeight = FontWeight.Bold)
                            Text("${reward.cost} poin", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                    Button(
                        onClick = { onRedeem(reward) },
                        enabled = affordable,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(if (affordable) "Tukar" else "Kurang", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// Helper format Rupiah dengan titik ribuan.
fun formatRupiah(value: Long): String =
    "%,d".format(value).replace(',', '.')
