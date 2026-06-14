package com.example.waroenglegitmembership.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// Screen List PRD: Splash Screen — menampilkan logo.
@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // LaunchedEffect: pindah ke home setelah 2 detik.
    LaunchedEffect(Unit) {
        delay(2000)
        onTimeout()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🍡", fontSize = 80.sp)
        Spacer(Modifier.height(16.dp))
        Text("Waroeng Legit", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary)
        Text("Membership Card", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
    }
}
