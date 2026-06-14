package com.example.waroenglegitmembership.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.util.PatternsCompat

// Screen List PRD: Add Member Screen — form registrasi.
// FR-01: semua field wajib, email harus valid.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberScreen(
    onBack: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // Validasi email pakai pola bawaan Android.
    val emailValid = email.isNotBlank() &&
            PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    val formValid = name.isNotBlank() && emailValid && phone.isNotBlank()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Daftar Member", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Nama Lengkap") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true
            )
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true,
                isError = email.isNotBlank() && !emailValid,
                supportingText = {
                    if (email.isNotBlank() && !emailValid)
                        Text("Format email tidak valid", color = Color.Red)
                }
            )
            OutlinedTextField(
                value = phone, onValueChange = { phone = it.filter { c -> c.isDigit() } },
                label = { Text("Nomor HP") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true
            )
            Button(
                onClick = { onSave(name, email, phone) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = formValid, // tombol disabled kalau form belum valid
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Simpan Member", fontWeight = FontWeight.Bold)
            }
        }
    }
}
