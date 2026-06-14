package com.example.waroenglegitmembership.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.waroenglegitmembership.data.Member
import com.example.waroenglegitmembership.viewmodel.MembershipViewModel

// Screen List PRD: Home Screen — total member, daftar member, tombol tambah.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MembershipViewModel,
    onAddMember: () -> Unit,
    onMemberClick: (Int) -> Unit
) {
    // collectAsState: "menonton" StateFlow dari ViewModel.
    // Setiap database berubah, UI otomatis ikut update.
    val members by viewModel.members.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Waroeng Legit", fontWeight = FontWeight.Bold) }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddMember,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Daftar Member")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Selamat datang! 👋", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                // Total member dari ukuran list.
                Text("Total member: ${members.size}", color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp))
            }

            if (members.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("👤", fontSize = 56.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("Belum ada member.\nYuk daftar member pertama!",
                            color = Color.Gray)
                    }
                }
            }

            items(members) { member ->
                MemberListCard(member = member, onClick = { onMemberClick(member.id) })
            }
        }
    }
}

@Composable
fun MemberListCard(member: Member, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Person, contentDescription = null,
                modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(member.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("ID: WL-${member.id.toString().padStart(4, '0')}",
                    color = Color.Gray, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${member.points} poin", fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary)
                LevelBadge(member.level)
            }
        }
    }
}

@Composable
fun LevelBadge(level: String) {
    val color = when (level) {
        "Gold" -> Color(0xFFFFB300)
        "Silver" -> Color(0xFF9E9E9E)
        else -> Color(0xFFA1664A)
    }
    Surface(shape = RoundedCornerShape(12.dp), color = color.copy(alpha = 0.15f)) {
        Text(level, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
