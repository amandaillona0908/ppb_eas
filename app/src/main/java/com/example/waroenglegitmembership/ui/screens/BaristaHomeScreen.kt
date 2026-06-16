package com.example.waroenglegitmembership.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.waroenglegitmembership.data.Member
import com.example.waroenglegitmembership.ui.theme.WL
import com.example.waroenglegitmembership.viewmodel.MembershipViewModel

private enum class BaristaTab { DASHBOARD, MEMBER }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaristaHomeScreen(
    viewModel: MembershipViewModel,
    onAddMember: () -> Unit,
    onMemberClick: (Int) -> Unit,
    onLogout: () -> Unit
) {
    val members by viewModel.members.collectAsState()
    var tab by remember { mutableStateOf(BaristaTab.DASHBOARD) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Tombol back HP: dari tab Member balik ke Dashboard,
    // di Dashboard munculkan dialog konfirmasi keluar.
    BackHandler {
        if (tab != BaristaTab.DASHBOARD) tab = BaristaTab.DASHBOARD
        else showLogoutDialog = true
    }

    if (showLogoutDialog) {
        LogoutConfirmDialog(
            onConfirm = { showLogoutDialog = false; onLogout() },
            onDismiss = { showLogoutDialog = false }
        )
    }

    Scaffold(
        containerColor = WL.Krem,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = WL.Krem),
                title = {
                    val title = if (tab == BaristaTab.DASHBOARD) "Dashboard Barista" else "Daftar Member"
                    Text(title, fontWeight = FontWeight.Black)
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Keluar")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = WL.Surface) {
                NavigationBarItem(
                    selected = tab == BaristaTab.DASHBOARD,
                    onClick = { tab = BaristaTab.DASHBOARD },
                    label = { Text("Dashboard") },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = tab == BaristaTab.MEMBER,
                    onClick = { tab = BaristaTab.MEMBER },
                    label = { Text("Member") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddMember,
                containerColor = WL.Emas,
                contentColor = WL.Charcoal
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Member Baru", fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (tab) {
                BaristaTab.DASHBOARD -> DashboardTab(members)
                BaristaTab.MEMBER -> MemberTab(members, onMemberClick)
            }
        }
    }
}

// ---------- Tab Dashboard ----------

@Composable
private fun DashboardTab(members: List<Member>) {
    val totalPoin = members.sumOf { it.points }
    val byLevel = members.groupingBy { it.level }.eachCount()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Halo, Barista! 👋", fontSize = 24.sp, fontWeight = FontWeight.Black, color = WL.Coklat)
            Text("Ringkasan Waroeng Legit hari ini", color = WL.TeksRedup)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                StatCard(Modifier.weight(1f), "👥", "${members.size}", "Total Member", WL.GulaMerah)
                StatCard(Modifier.weight(1f), "⭐", "$totalPoin", "Poin Beredar", WL.Pandan)
            }
        }
        item {
            Text("Sebaran Level Member", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                modifier = Modifier.padding(top = 4.dp))
        }
        item { LevelStatRow("🥉", "Bronze", byLevel["Bronze"] ?: 0, members.size, WL.GulaMerah) }
        item { LevelStatRow("🥈", "Silver", byLevel["Silver"] ?: 0, members.size, WL.Pandan) }
        item { LevelStatRow("🥇", "Gold", byLevel["Gold"] ?: 0, members.size, WL.Klepon) }

        if (members.isEmpty()) {
            item {
                EmptyHint("🍩", "Belum ada member",
                    "Tekan tombol Member Baru untuk mendaftarkan pelanggan pertama.")
            }
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, emoji: String, value: String, label: String, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = WL.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Surface(shape = RoundedCornerShape(14.dp), color = color.copy(alpha = 0.15f)) {
                Text(emoji, fontSize = 24.sp, modifier = Modifier.padding(8.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(value, fontWeight = FontWeight.Black, fontSize = 26.sp, color = color)
            Text(label, color = WL.TeksRedup, fontSize = 12.sp)
        }
    }
}

@Composable
private fun LevelStatRow(emoji: String, level: String, count: Int, total: Int, color: Color) {
    val fraction = if (total > 0) count.toFloat() / total else 0f
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = WL.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, fontSize = 26.sp)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(level, fontWeight = FontWeight.Bold, color = WL.Coklat)
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(8.dp)
                        .background(color.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(fraction).height(8.dp)
                            .background(color, RoundedCornerShape(4.dp))
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Text("$count", fontWeight = FontWeight.Black, fontSize = 20.sp, color = color)
        }
    }
}

// ---------- Tab Member (search + daftar) ----------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MemberTab(members: List<Member>, onMemberClick: (Int) -> Unit) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query, members) {
        members.filter {
            query.isBlank() ||
                    it.name.contains(query, ignoreCase = true) ||
                    it.email.contains(query, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Cari nama atau email member...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = WL.Surface,
                unfocusedContainerColor = WL.Surface
            )
        )
        when {
            filtered.isEmpty() && query.isBlank() ->
                EmptyHint("🍩", "Belum ada member", "Daftarkan member baru lewat tombol di bawah.")
            filtered.isEmpty() ->
                EmptyHint("🔍", "Member tidak ketemu", "Coba kata kunci lain ya.")
            else -> LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Tap member untuk catat transaksi",
                        color = WL.TeksRedup, fontSize = 13.sp, modifier = Modifier.padding(bottom = 4.dp))
                }
                items(filtered) { m -> MemberListCard(m) { onMemberClick(m.id) } }
            }
        }
    }
}