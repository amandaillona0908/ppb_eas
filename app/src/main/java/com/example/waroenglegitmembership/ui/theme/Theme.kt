package com.example.waroenglegitmembership.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Palet warna Waroeng Legit — coklat oranye hangat khas jajan pasar.
 * Warna utama dipertahankan dari desain asli, dilengkapi warna pendukung
 * yang selaras untuk komponen seperti badge level dan dashboard.
 */
object WL {
    // Warna inti (dari desain asli)
    val GulaMerah = Color(0xFFD4730A)   // oranye coklat — warna utama/brand
    val Krem = Color(0xFFFFF8F0)        // krem hangat — background
    val Coklat = Color(0xFF2B1600)      // coklat gelap — teks
    val Kunyit = Color(0xFFF5A623)      // kuning keemasan — sekunder

    // Warna pendukung (selaras, tidak mencolok)
    val Pandan = Color(0xFF6BA368)      // hijau daun hangat — aksen segar
    val Klepon = Color(0xFFB85C38)      // terracotta — level Gold

    // Versi lembut untuk container & latar kartu
    val GulaSoft = Color(0xFFFFDDB3)    // krem oranye lembut
    val KunyitSoft = Color(0xFFFCEFD6)  // kuning sangat lembut
    val PandanSoft = Color(0xFFE8F2E3)  // hijau muda lembut
    val KleponSoft = Color(0xFFF6E1D6)  // terracotta lembut
    val TeksRedup = Color(0xFF8A8175)   // teks sekunder (coklat keabuan)

    // Alias kompatibilitas (nama dari iterasi tema sebelumnya)
    val Emas = GulaMerah
    val Sage = Pandan
    val Charcoal = Coklat
    val Surface = Color.White
    val EmasSoft = GulaSoft
}

private val WaroengColors = lightColorScheme(
    primary = WL.GulaMerah,
    onPrimary = Color.White,
    primaryContainer = WL.GulaSoft,
    onPrimaryContainer = WL.Coklat,
    secondary = WL.Kunyit,
    onSecondary = Color.White,
    secondaryContainer = WL.KunyitSoft,
    tertiary = WL.Klepon,
    background = WL.Krem,
    surface = Color.White,
    onSurface = WL.Coklat,
    onBackground = WL.Coklat
)

@Composable
fun WaroengLegitTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = WaroengColors, content = content)
}