package com.example.waroenglegitmembership.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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

private enum class CustomerTab { HOME, RIWAYAT, PROFILE }
private enum class CustomerSub { CARD, REWARD }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDashboardScreen(
    memberId: Int,
    viewModel: MembershipViewModel,
    onLogout: () -> Unit
) {
    val member by viewModel.getMember(memberId).collectAsState()
    val transactions by viewModel.getTransactions(memberId).collectAsState()
    var tab by remember { mutableStateOf(CustomerTab.HOME) }
    var sub by remember { mutableStateOf<CustomerSub?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Tombol back HP: bertingkat — sub-screen & tab lain balik ke Home dulu,
    // baru di Home munculkan dialog konfirmasi keluar.
    BackHandler {
        when {
            sub != null -> sub = null
            tab != CustomerTab.HOME -> tab = CustomerTab.HOME
            else -> showLogoutDialog = true
        }
    }
    var notif by remember { mutableStateOf<String?>(null) }

    fun toast(msg: String) { notif = msg }

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
                title = { Text(currentTitle(tab, sub), fontWeight = FontWeight.Black) },
                navigationIcon = {
                    if (sub != null) {
                        IconButton(onClick = { sub = null }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                        }
                    }
                },
                actions = {}
            )
        },
        bottomBar = {
            if (sub == null) {
                NavigationBar(containerColor = WL.Surface) {
                    NavigationBarItem(
                        selected = tab == CustomerTab.HOME,
                        onClick = { tab = CustomerTab.HOME },
                        label = { Text("Home") },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) }
                    )
                    NavigationBarItem(
                        selected = tab == CustomerTab.RIWAYAT,
                        onClick = { tab = CustomerTab.RIWAYAT },
                        label = { Text("Riwayat") },
                        icon = { Text("🧾", fontSize = 18.sp) }
                    )
                    NavigationBarItem(
                        selected = tab == CustomerTab.PROFILE,
                        onClick = { tab = CustomerTab.PROFILE },
                        label = { Text("Profil") },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Konten utama
            member?.let { m ->
                when (sub) {
                    CustomerSub.CARD -> MembershipCardTab(m)
                    CustomerSub.REWARD -> RewardTab(m, viewModel.rewards) { reward ->
                        viewModel.redeemReward(m, reward) { success ->
                            toast(if (success) "Berhasil tukar ${reward.name}! 🎉" else "Poin tidak cukup 😔")
                        }
                    }
                    null -> when (tab) {
                        CustomerTab.HOME -> HomeTab(
                            member = m,
                            onOpenCard = { sub = CustomerSub.CARD },
                            onOpenRiwayat = { tab = CustomerTab.RIWAYAT },
                            onOpenReward = { sub = CustomerSub.REWARD }
                        )
                        CustomerTab.RIWAYAT -> HistoryTab(transactions)
                        CustomerTab.PROFILE -> ProfileTab(
                            member = m,
                            onSave = { name, email, phone ->
                                viewModel.updateProfile(m, name, email, phone)
                                toast("Profil diperbarui!")
                            },
                            onLogout = { showLogoutDialog = true }
                        )
                    }
                }
            }
            // Banner notifikasi (digambar paling akhir = tampil di atas konten)
            TopBanner(message = notif, onDismiss = { notif = null })
        }
    }
}

private fun currentTitle(tab: CustomerTab, sub: CustomerSub?): String = when {
    sub == CustomerSub.CARD -> "Kartu Member"
    sub == CustomerSub.REWARD -> "Tukar Reward"
    tab == CustomerTab.RIWAYAT -> "Riwayat Transaksi"
    tab == CustomerTab.PROFILE -> "Profil"
    else -> "Waroeng Legit"
}

// ---------- Tab Home ----------

@Composable
private fun HomeTab(
    member: Member,
    onOpenCard: () -> Unit,
    onOpenRiwayat: () -> Unit,
    onOpenReward: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Hai, ${member.name}! 👋", fontSize = 24.sp, fontWeight = FontWeight.Black, color = WL.Coklat)
            Text("Mau jajan apa hari ini?", color = WL.TeksRedup)
        }
        item { PointCard(member) }
        item {
            Text("Menu", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(top = 4.dp))
        }
        item { MenuRow("📇", "My Card", "Lihat kartu member digital", WL.GulaSoft, onOpenCard) }
        item { MenuRow("🧾", "Transaksi", "Riwayat transaksi kamu", WL.PandanSoft, onOpenRiwayat) }
        item { MenuRow("🎁", "Reward", "Tukar poin dengan jajan", WL.KleponSoft, onOpenReward) }
    }
}

@Composable
private fun PointCard(member: Member) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(WL.GulaMerah, WL.Klepon)))
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Poin", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                    Text("${member.points}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 44.sp)
                    Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.25f)) {
                        Text(
                            "${levelEmoji(member.level)} Level ${member.level}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                            color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text("🍡", fontSize = 52.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuRow(emoji: String, title: String, subtitle: String, bg: Color, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WL.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(16.dp), color = bg) {
                Text(emoji, fontSize = 28.sp, modifier = Modifier.padding(10.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, color = WL.TeksRedup, fontSize = 13.sp)
            }
        }
    }
}

// ---------- Tab Profil ----------

@Composable
private fun ProfileTab(member: Member, onSave: (String, String, String) -> Unit, onLogout: () -> Unit) {
    var name by remember(member.id) { mutableStateOf(member.name) }
    var email by remember(member.id) { mutableStateOf(member.email) }
    var phone by remember(member.id) { mutableStateOf(member.phone) }
    var editing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))
        MemberAvatar(member, size = 90)
        Spacer(Modifier.height(8.dp))
        LevelBadge(member.level)
        Text("ID: ${memberCode(member.id)}", color = WL.TeksRedup, fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp))
        Spacer(Modifier.height(24.dp))

        WhiteTextField(name, { name = it }, "Nama", enabled = editing)
        Spacer(Modifier.height(12.dp))
        WhiteTextField(email, { email = it }, "Email", enabled = editing, keyboardType = KeyboardType.Email)
        Spacer(Modifier.height(12.dp))
        WhiteTextField(phone, { phone = it.filter(Char::isDigit) }, "Nomor HP",
            enabled = editing, keyboardType = KeyboardType.Phone)
        Spacer(Modifier.height(24.dp))

        if (editing) {
            Button(
                onClick = { onSave(name, email, phone); editing = false },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = name.isNotBlank() && email.isNotBlank() && phone.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WL.Emas, contentColor = WL.Charcoal)
            ) { Text("Simpan Perubahan", fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    name = member.name; email = member.email; phone = member.phone; editing = false
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) { Text("Batal") }
        } else {
            Button(
                onClick = { editing = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WL.Emas, contentColor = WL.Charcoal)
            ) { Text("Edit Profil", fontWeight = FontWeight.Bold) }

            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = WL.GulaMerah
                )
                Spacer(Modifier.width(8.dp))
                Text("Keluar", color = WL.GulaMerah, fontWeight = FontWeight.Bold)
            }
        }
    }
}