package com.example.waroenglegitmembership.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.waroenglegitmembership.ui.theme.WL
import com.example.waroenglegitmembership.viewmodel.MembershipViewModel

/** Layar barista untuk mencatat transaksi member tertentu (FR-04). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDetailScreen(
    memberId: Int,
    viewModel: MembershipViewModel,
    onBack: () -> Unit
) {
    val member by viewModel.getMember(memberId).collectAsState()
    var notif by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = WL.Krem,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = WL.Krem),
                title = { Text("Catat Transaksi", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            member?.let { m ->
                AddTransactionForm(
                    member = m,
                    onSubmit = { amount ->
                        viewModel.addTransaction(m, amount)
                        notif = "Transaksi tercatat! +${(amount / 10_000).toInt()} poin"
                    }
                )
            }
            TopBanner(message = notif, onDismiss = { notif = null })
        }
    }
}

@Composable
private fun AddTransactionForm(member: Member, onSubmit: (Double) -> Unit) {
    var amount by remember { mutableStateOf("") }
    val amountValue = amount.toDoubleOrNull() ?: 0.0
    val pointPreview = (amountValue / 10_000).toInt()

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MemberSummaryCard(member)

        Text("Formula: 1 poin = Rp10.000", color = WL.TeksRedup, fontSize = 13.sp)

        WhiteTextField(
            value = amount,
            onValueChange = { amount = it.filter(Char::isDigit) },
            label = "Nominal Pembelian (Rp)",
            keyboardType = KeyboardType.Number
        )

        if (amountValue > 0) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WL.PandanSoft)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✨", fontSize = 24.sp)
                    Spacer(Modifier.width(12.dp))
                    Text("Member akan dapat $pointPreview poin",
                        fontWeight = FontWeight.Bold, color = WL.Pandan)
                }
            }
        }

        Button(
            onClick = { onSubmit(amountValue); amount = "" },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = amountValue > 0,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = WL.Emas, contentColor = WL.Charcoal)
        ) {
            Text("Simpan Transaksi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun MemberSummaryCard(member: Member) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = WL.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MemberAvatar(member)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(member.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                LevelBadge(member.level)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${member.points}", fontWeight = FontWeight.Black, fontSize = 22.sp, color = WL.GulaMerah)
                Text("poin", color = WL.TeksRedup, fontSize = 11.sp)
            }
        }
    }
}

/** Kartu member digital (FR-03), dipakai di sisi customer. */
@Composable
fun MembershipCardTab(member: Member) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth().height(220.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(listOf(WL.GulaMerah, WL.Klepon)))
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("WAROENG LEGIT", color = Color.White,
                                fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("Member Card", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                        }
                        Text("🍡", fontSize = 32.sp)
                    }
                    Column {
                        Text(member.name, color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp)
                        Text("ID: ${memberCode(member.id)}",
                            color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    "${levelEmoji(member.level)} ${member.level}",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("TOTAL POIN", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                                Text("${member.points}", color = Color.White,
                                    fontWeight = FontWeight.Black, fontSize = 26.sp)
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
private fun InfoRow(label: String, value: String) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = WL.Surface),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = WL.TeksRedup)
            Text(value, fontWeight = FontWeight.SemiBold)
        }
    }
}