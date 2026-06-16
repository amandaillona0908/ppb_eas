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
import com.example.waroenglegitmembership.ui.theme.WL
import com.example.waroenglegitmembership.viewmodel.LoginResult
import com.example.waroenglegitmembership.viewmodel.MembershipViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerLoginScreen(
    viewModel: MembershipViewModel,
    onLoginSuccess: (Int) -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = WL.Krem,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = WL.Krem),
                title = { Text("Login Customer", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Surface(shape = RoundedCornerShape(28.dp), color = WL.Pandan, modifier = Modifier.size(96.dp)) {
                Box(contentAlignment = Alignment.Center) { Text("🙋", fontSize = 52.sp) }
            }
            Spacer(Modifier.height(20.dp))
            Text("Masuk ke Akun Kamu", fontSize = 20.sp, fontWeight = FontWeight.Black, color = WL.Coklat)
            Text("Pakai email & password yang terdaftar.", color = WL.TeksRedup, fontSize = 14.sp)
            Spacer(Modifier.height(24.dp))

            WhiteTextField(
                value = email,
                onValueChange = { email = it; errorMsg = "" },
                label = "Email",
                keyboardType = KeyboardType.Email
            )
            Spacer(Modifier.height(12.dp))
            WhiteTextField(
                value = password,
                onValueChange = { password = it; errorMsg = "" },
                label = "Password",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                isError = errorMsg.isNotEmpty(),
                errorText = errorMsg
            )
            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    isLoading = true
                    viewModel.login(email, password) { result ->
                        isLoading = false
                        when (result) {
                            is LoginResult.Success -> onLoginSuccess(result.member.id)
                            LoginResult.WrongPassword -> errorMsg = "Password salah."
                            LoginResult.NotFound ->
                                errorMsg = "Email belum terdaftar. Minta barista daftarkan dulu ya!"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = email.isNotBlank() && password.isNotBlank() && !isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WL.Pandan, contentColor = WL.Charcoal)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Masuk", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}