package com.example.waroenglegitmembership.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.util.PatternsCompat
import com.example.waroenglegitmembership.ui.theme.WL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberScreen(
    onBack: () -> Unit,
    onSave: (name: String, email: String, phone: String,
             onResult: (defaultPassword: String, memberId: Int) -> Unit) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    // Info hasil pendaftaran: Pair(memberId, defaultPassword). null = belum daftar.
    var result by remember { mutableStateOf<Pair<Int, String>?>(null) }

    val emailValid = email.isNotBlank() && PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    val formValid = name.isNotBlank() && emailValid && phone.isNotBlank()

    // Dialog sukses: tampilkan password default agar barista beritahu ke customer.
    result?.let { (memberId, defaultPass) ->
        AlertDialog(
            onDismissRequest = {},
            icon = { Text("✅", fontSize = 28.sp) },
            title = { Text("Member Terdaftar!", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Beritahukan info login ini ke customer:")
                    Spacer(Modifier.height(12.dp))
                    Surface(shape = RoundedCornerShape(12.dp), color = WL.GulaSoft) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            InfoLine("ID Member", memberCode(memberId))
                            Spacer(Modifier.height(6.dp))
                            InfoLine("Email", email)
                            Spacer(Modifier.height(6.dp))
                            InfoLine("Password", defaultPass)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Customer bisa ganti password ini nanti di menu Profil.",
                        fontSize = 12.sp, color = WL.TeksRedup)
                }
            },
            confirmButton = {
                Button(
                    onClick = { result = null; onBack() },
                    colors = ButtonDefaults.buttonColors(containerColor = WL.GulaMerah, contentColor = Color.White)
                ) { Text("Selesai") }
            }
        )
    }

    Scaffold(
        containerColor = WL.Krem,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = WL.Krem),
                title = { Text("Member Baru", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🧑‍🍳", fontSize = 48.sp)
                Text("Daftarkan pelanggan baru", color = WL.TeksRedup, fontSize = 14.sp)
            }

            WhiteTextField(name, { name = it }, "Nama Lengkap")
            WhiteTextField(
                email, { email = it }, "Email",
                keyboardType = KeyboardType.Email,
                isError = email.isNotBlank() && !emailValid,
                errorText = "Format email tidak valid"
            )
            WhiteTextField(phone, { phone = it.filter(Char::isDigit) }, "Nomor HP",
                keyboardType = KeyboardType.Phone)

            // Info: password dibuat otomatis.
            Surface(shape = RoundedCornerShape(12.dp), color = WL.KunyitSoft, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🔑", fontSize = 18.sp)
                    Spacer(Modifier.width(10.dp))
                    Text("Password default dibuat otomatis & ditampilkan setelah simpan.",
                        fontSize = 13.sp, color = WL.Coklat)
                }
            }

            Button(
                onClick = { onSave(name, email, phone) { pass, id -> result = id to pass } },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = formValid,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WL.GulaMerah, contentColor = Color.White)
            ) {
                Text("Simpan Member", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = WL.TeksRedup, fontSize = 13.sp)
        Text(value, fontWeight = FontWeight.Bold, color = WL.Coklat, fontSize = 13.sp)
    }
}