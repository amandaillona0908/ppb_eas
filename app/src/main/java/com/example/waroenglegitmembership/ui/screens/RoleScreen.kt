package com.example.waroenglegitmembership.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.waroenglegitmembership.ui.theme.WL

private const val BARISTA_CODE = "barista123"

@Composable
fun RoleScreen(
    onBaristaLogin: () -> Unit,
    onCustomerLogin: () -> Unit
) {
    var showBaristaDialog by remember { mutableStateOf(false) }

    if (showBaristaDialog) {
        BaristaLoginDialog(
            onDismiss = { showBaristaDialog = false },
            onSuccess = {
                showBaristaDialog = false
                onBaristaLogin()
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(WL.Krem, WL.GulaSoft)))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = WL.GulaMerah,
                modifier = Modifier.size(96.dp)
            ) {
                Box(contentAlignment = Alignment.Center) { Text("🍡", fontSize = 52.sp) }
            }
            Spacer(Modifier.height(16.dp))
            Text("Waroeng Legit", fontSize = 28.sp, fontWeight = FontWeight.Black, color = WL.Coklat)
            Text("Pilih cara kamu masuk", fontSize = 14.sp, color = WL.Coklat.copy(alpha = 0.6f))
            Spacer(Modifier.height(36.dp))

            RoleCard(
                emoji = "👨‍🍳",
                title = "Barista",
                desc = "Daftar member & catat transaksi",
                bg = WL.GulaMerah,
                onClick = { showBaristaDialog = true }
            )
            Spacer(Modifier.height(16.dp))
            RoleCard(
                emoji = "🙋",
                title = "Customer",
                desc = "Lihat poin & tukar reward",
                bg = WL.Pandan,
                onClick = onCustomerLogin
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoleCard(
    emoji: String,
    title: String,
    desc: String,
    bg: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White.copy(alpha = 0.25f),
                modifier = Modifier.size(60.dp)
            ) {
                Box(contentAlignment = Alignment.Center) { Text(emoji, fontSize = 32.sp) }
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color.White)
                Text(desc, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun BaristaLoginDialog(onDismiss: () -> Unit, onSuccess: () -> Unit) {
    var code by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Login Barista", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Masukkan kode akses untuk masuk sebagai Barista.")
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it; isError = false },
                    label = { Text("Kode Akses") },
                    visualTransformation = if (visible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { visible = !visible }) {
                            Text(if (visible) "🙈" else "👁️", fontSize = 18.sp)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = isError,
                    supportingText = { if (isError) Text("Kode salah!", color = Color.Red) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = { if (code == BARISTA_CODE) onSuccess() else isError = true }) {
                Text("Masuk")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}