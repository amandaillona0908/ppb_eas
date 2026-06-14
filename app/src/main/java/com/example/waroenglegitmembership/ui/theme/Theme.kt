package com.example.waroenglegitmembership.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Warna brand Waroeng Legit — coklat oranye hangat khas jajan pasar.
private val WaroengColors = lightColorScheme(
    primary = Color(0xFFD4730A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDDB3),
    onPrimaryContainer = Color(0xFF2B1600),
    secondary = Color(0xFFF5A623),
    background = Color(0xFFFFF8F0),
    surface = Color(0xFFFFF8F0)
)

@Composable
fun WaroengLegitTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = WaroengColors, content = content)
}
