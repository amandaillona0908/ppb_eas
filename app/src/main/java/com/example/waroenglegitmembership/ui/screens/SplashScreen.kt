package com.example.waroenglegitmembership.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.waroenglegitmembership.ui.theme.WL
import kotlinx.coroutines.delay

private const val SPLASH_DURATION_MS = 2000L

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var started by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (started) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    LaunchedEffect(Unit) {
        started = true
        delay(SPLASH_DURATION_MS)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(WL.GulaMerah, WL.Kunyit))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = Color.White,
                modifier = Modifier.size(120.dp).scale(scale)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🍡", fontSize = 64.sp)
                }
            }
            Spacer(Modifier.height(24.dp))
            Text("Waroeng Legit", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
            Spacer(Modifier.height(4.dp))
            Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.25f)) {
                Text(
                    text = "Membership Card",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}