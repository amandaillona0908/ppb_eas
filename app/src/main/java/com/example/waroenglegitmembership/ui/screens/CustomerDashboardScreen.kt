package com.example.waroenglegitmembership.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.waroenglegitmembership.data.LevelSystem
import com.example.waroenglegitmembership.data.Member
import com.example.waroenglegitmembership.data.TxType
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
    var notif by remember { mutableStateOf<String?>(null) }

    fun toast(msg: String) { notif = msg }

    // Tombol back HP bertingkat: sub-screen & tab lain balik ke Home dulu,
    // baru di Home munculkan dialog konfirmasi keluar.
    BackHandler {
        when {
            sub != null -> sub = null
            tab != CustomerTab.HOME -> tab = CustomerTab.HOME
            else -> showLogoutDialog = true
        }
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
                title = { Text(currentTitle(tab, sub), fontWeight = FontWeight.Black) },
                navigationIcon = {
                    if (sub != null) {
                        IconButton(onClick = { sub = null }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                        }
                    }
                }
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
                            transactions = transactions,
                            usingDefaultPassword = viewModel.isDefaultPassword(m),
                            onOpenCard = { sub = CustomerSub.CARD },
                            onOpenRiwayat = { tab = CustomerTab.RIWAYAT },
                            onOpenReward = { sub = CustomerSub.REWARD },
                            onGoProfile = { tab = CustomerTab.PROFILE }
                        )
                        CustomerTab.RIWAYAT -> HistoryTab(transactions)
                        CustomerTab.PROFILE -> ProfileTab(
                            member = m,
                            usingDefaultPassword = viewModel.isDefaultPassword(m),
                            verifyCurrentPassword = { pass -> viewModel.verifyPassword(m, pass) },
                            onSave = { name, email, phone ->
                                viewModel.updateProfile(m, name, email, phone)
                                toast("Profil diperbarui!")
                            },
                            onChangePassword = { newPass ->
                                viewModel.updatePassword(m, newPass)
                                toast("Password berhasil diganti!")
                            },
                            onLogout = { showLogoutDialog = true }
                        )
                    }
                }
            }
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
    transactions: List<com.example.waroenglegitmembership.data.Transaction>,
    usingDefaultPassword: Boolean,
    onOpenCard: () -> Unit,
    onOpenRiwayat: () -> Unit,
    onOpenReward: () -> Unit,
    onGoProfile: () -> Unit
) {
    val purchases = transactions.count { it.type == TxType.PURCHASE }
    val redeems = transactions.count { it.type == TxType.REDEEM }
    val missions = LevelSystem.missions(member.level, member.points, purchases, redeems, !usingDefaultPassword)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Hai, ${member.name}! 👋", fontSize = 24.sp, fontWeight = FontWeight.Black, color = WL.Coklat)
            Text("Mau jajan apa hari ini?", color = WL.TeksRedup)
        }
        if (usingDefaultPassword) {
            item { ChangePasswordReminder(onGoProfile) }
        }
        item { PointCard(member) }
        item { MissionCard(member.level, missions) }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangePasswordReminder(onGoProfile: () -> Unit) {
    Card(
        onClick = onGoProfile,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WL.KunyitSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("🔒", fontSize = 22.sp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Ganti password kamu", fontWeight = FontWeight.Bold, color = WL.Coklat, fontSize = 14.sp)
                Text("Kamu masih pakai password default dari kasir. Tap untuk ganti.",
                    color = WL.TeksRedup, fontSize = 12.sp)
            }
            Text("›", fontSize = 24.sp, color = WL.TeksRedup)
        }
    }
}

@Composable
private fun MissionCard(level: String, missions: List<LevelSystem.Mission>) {
    val next = LevelSystem.nextLevel(level)
    val progress = LevelSystem.progress(missions)
    val doneCount = missions.count { it.done }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WL.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(levelEmoji(level), fontSize = 22.sp)
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    if (next != null) {
                        Text("Misi menuju $next", fontWeight = FontWeight.Black, color = WL.Coklat, fontSize = 16.sp)
                        Text("$doneCount/${missions.size} misi selesai", color = WL.TeksRedup, fontSize = 12.sp)
                    } else {
                        Text("Level Tertinggi! 🎉", fontWeight = FontWeight.Black, color = WL.Coklat, fontSize = 16.sp)
                        Text("Kamu sudah mencapai level Gold", color = WL.TeksRedup, fontSize = 12.sp)
                    }
                }
            }

            if (missions.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                // Progress bar
                Box(
                    modifier = Modifier.fillMaxWidth().height(8.dp)
                        .background(WL.GulaSoft, RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(progress).height(8.dp)
                            .background(WL.GulaMerah, RoundedCornerShape(4.dp))
                    )
                }
                Spacer(Modifier.height(14.dp))
                missions.forEach { m -> MissionRow(m) }
            }
        }
    }
}

@Composable
private fun MissionRow(mission: LevelSystem.Mission) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Tanda centang / kosong
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (mission.done) WL.PandanSoft else WL.Krem,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(if (mission.done) "✓" else "•",
                    fontWeight = FontWeight.Black,
                    color = if (mission.done) WL.Pandan else WL.TeksRedup,
                    fontSize = 16.sp)
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(mission.title, fontWeight = FontWeight.Bold, color = WL.Coklat, fontSize = 14.sp)
            Text(mission.progress, color = WL.TeksRedup, fontSize = 12.sp)
        }
    }
}

// ---------- Tab Profil ----------

@Composable
private fun ProfileTab(
    member: Member,
    usingDefaultPassword: Boolean,
    verifyCurrentPassword: (String) -> Boolean,
    onSave: (String, String, String) -> Unit,
    onChangePassword: (String) -> Unit,
    onLogout: () -> Unit
) {
    var editing by remember { mutableStateOf(false) }
    var name by remember(member.id, editing) { mutableStateOf(member.name) }
    var email by remember(member.id, editing) { mutableStateOf(member.email) }
    var phone by remember(member.id, editing) { mutableStateOf(member.phone) }
    var currentPass by remember(editing) { mutableStateOf("") }
    var newPass by remember(editing) { mutableStateOf("") }
    var confirmPass by remember(editing) { mutableStateOf("") }

    val wantChangePass = newPass.isNotEmpty() || confirmPass.isNotEmpty()
    val passTooShort = newPass.isNotEmpty() && newPass.length < 4
    val passMismatch = confirmPass.isNotEmpty() && newPass != confirmPass
    // Password lama wajib & benar HANYA jika member sudah pernah ganti dari default.
    val needCurrent = wantChangePass && !usingDefaultPassword
    val currentWrong = needCurrent && currentPass.isNotEmpty() && !verifyCurrentPassword(currentPass)
    val currentOk = !needCurrent || (currentPass.isNotEmpty() && verifyCurrentPassword(currentPass))
    val passOk = !wantChangePass || (newPass.length >= 4 && newPass == confirmPass && currentOk)
    val formValid = name.isNotBlank() && email.isNotBlank() && phone.isNotBlank() && passOk

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = WL.Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MemberAvatar(member, size = 84)
                Spacer(Modifier.height(12.dp))
                Text(member.name, fontWeight = FontWeight.Black, fontSize = 20.sp, color = WL.Coklat)
                Spacer(Modifier.height(6.dp))
                LevelBadge(member.level)
                Spacer(Modifier.height(8.dp))
                Surface(shape = RoundedCornerShape(12.dp), color = WL.Krem) {
                    Text(
                        "ID: ${memberCode(member.id)}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = WL.TeksRedup, fontSize = 13.sp, fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        if (editing) {
            // ---- Mode edit: semua dalam satu form ----
            WhiteTextField(name, { name = it }, "Nama")
            Spacer(Modifier.height(12.dp))
            WhiteTextField(email, { email = it }, "Email", keyboardType = KeyboardType.Email)
            Spacer(Modifier.height(12.dp))
            WhiteTextField(phone, { phone = it.filter(Char::isDigit) }, "Nomor HP", keyboardType = KeyboardType.Phone)

            Spacer(Modifier.height(20.dp))
            // Bagian ganti password (opsional)
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("🔑", fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Text("Ubah Password (opsional)", fontWeight = FontWeight.Bold, color = WL.Coklat, fontSize = 14.sp)
            }
            Text("Kosongkan jika tidak ingin mengganti.", color = WL.TeksRedup, fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 10.dp))
            // Password lama hanya diminta jika sudah pernah ganti dari default.
            if (!usingDefaultPassword) {
                WhiteTextField(
                    currentPass, { currentPass = it }, "Password Saat Ini", isPassword = true,
                    isError = currentWrong, errorText = "Password salah"
                )
                Spacer(Modifier.height(12.dp))
            }
            WhiteTextField(
                newPass, { newPass = it }, "Password Baru", isPassword = true,
                isError = passTooShort, errorText = "Minimal 4 karakter"
            )
            Spacer(Modifier.height(12.dp))
            WhiteTextField(
                confirmPass, { confirmPass = it }, "Konfirmasi Password", isPassword = true,
                isError = passMismatch, errorText = "Password tidak sama"
            )

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    onSave(name, email, phone)
                    if (wantChangePass) onChangePassword(newPass)
                    editing = false
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = formValid,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WL.GulaMerah, contentColor = Color.White)
            ) { Text("Simpan Perubahan", fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { editing = false },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) { Text("Batal") }
        } else {
            // ---- Mode lihat ----
            ProfileInfoRow("📧", "Email", member.email)
            Spacer(Modifier.height(10.dp))
            ProfileInfoRow("📱", "Nomor HP", member.phone)
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { editing = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WL.GulaMerah, contentColor = Color.White)
            ) { Text("Edit Profil", fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null,
                    modifier = Modifier.size(18.dp), tint = WL.GulaMerah)
                Spacer(Modifier.width(8.dp))
                Text("Keluar", color = WL.GulaMerah, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(emoji: String, label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WL.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(12.dp), color = WL.Krem) {
                Text(emoji, fontSize = 20.sp, modifier = Modifier.padding(8.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(label, color = WL.TeksRedup, fontSize = 12.sp)
                Text(value, fontWeight = FontWeight.SemiBold, color = WL.Coklat)
            }
        }
    }
}