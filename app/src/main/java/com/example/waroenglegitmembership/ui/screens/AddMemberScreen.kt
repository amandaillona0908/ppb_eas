package com.example.waroenglegitmembership.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
    onSave: (name: String, email: String, phone: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val emailValid = email.isNotBlank() && PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    val formValid = name.isNotBlank() && emailValid && phone.isNotBlank()

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
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🧑‍🍳", fontSize = 48.sp)
                Text("Daftarkan pelanggan baru", color = Color.Gray, fontSize = 14.sp)
            }

            WhiteTextField(
                value = name,
                onValueChange = { name = it },
                label = "Nama Lengkap"
            )
            WhiteTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                keyboardType = KeyboardType.Email,
                isError = email.isNotBlank() && !emailValid,
                errorText = "Format email tidak valid"
            )
            WhiteTextField(
                value = phone,
                onValueChange = { phone = it.filter(Char::isDigit) },
                label = "Nomor HP",
                keyboardType = KeyboardType.Phone
            )

            Button(
                onClick = { onSave(name, email, phone) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = formValid,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WL.GulaMerah)
            ) {
                Text("Simpan Member", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}