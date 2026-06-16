package com.example.waroenglegitmembership.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.waroenglegitmembership.data.Member
import com.example.waroenglegitmembership.ui.theme.WL

// ---------- Helper level ----------

fun levelColor(level: String): Color = when (level) {
    "Gold" -> WL.Klepon
    "Silver" -> WL.Pandan
    else -> WL.GulaMerah
}

fun levelEmoji(level: String): String = when (level) {
    "Gold" -> "🥇"
    "Silver" -> "🥈"
    else -> "🥉"
}

fun formatRupiah(value: Long): String = "%,d".format(value).replace(',', '.')

fun memberCode(id: Int): String = "WL-${id.toString().padStart(4, '0')}"

// ---------- Komponen bersama ----------

/** Avatar inisial nama dengan warna sesuai level. */
@Composable
fun MemberAvatar(member: Member, size: Int = 48) {
    Box(modifier = Modifier.size(size.dp), contentAlignment = Alignment.Center) {
        Surface(
            shape = RoundedCornerShape((size / 3.4).dp),
            color = levelColor(member.level).copy(alpha = 0.15f),
            modifier = Modifier.fillMaxSize()
        ) {}
        Text(
            text = member.name.take(1).uppercase(),
            fontWeight = FontWeight.Black,
            fontSize = (size * 0.42).sp,
            color = levelColor(member.level)
        )
    }
}

@Composable
fun LevelBadge(level: String) {
    val color = levelColor(level)
    Surface(shape = RoundedCornerShape(20.dp), color = color.copy(alpha = 0.15f)) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(levelEmoji(level), fontSize = 11.sp)
            Spacer(Modifier.width(4.dp))
            Text(level, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/** Kartu member di daftar (barista). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberListCard(member: Member, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WL.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MemberAvatar(member)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(member.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("ID: ${memberCode(member.id)}", color = WL.TeksRedup, fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))
                LevelBadge(member.level)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${member.points}", fontWeight = FontWeight.Black,
                    fontSize = 22.sp, color = WL.GulaMerah)
                Text("poin", color = WL.TeksRedup, fontSize = 11.sp)
            }
        }
    }
}

/** Tampilan kosong dengan emoji + pesan. */
@Composable
fun EmptyHint(emoji: String, title: String, subtitle: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 56.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 60.sp)
        Spacer(Modifier.height(12.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = WL.Coklat)
        Spacer(Modifier.height(4.dp))
        Text(
            text = subtitle,
            color = WL.TeksRedup,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

/** OutlinedTextField dengan background putih, dipakai di banyak form. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhiteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: androidx.compose.ui.text.input.KeyboardType =
        androidx.compose.ui.text.input.KeyboardType.Text,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorText: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = enabled,
        singleLine = true,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
        isError = isError,
        supportingText = { if (isError && errorText.isNotEmpty()) Text(errorText, color = Color.Red) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = WL.Surface,
            unfocusedContainerColor = WL.Surface,
            disabledContainerColor = WL.Surface
        )
    )
}

/** Dialog konfirmasi sebelum keluar / ganti role. */
@Composable
fun LogoutConfirmDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Text("🚪", fontSize = 28.sp) },
        title = { Text("Keluar?", fontWeight = FontWeight.Bold) },
        text = { Text("Kamu yakin mau keluar dan kembali ke halaman pilih role?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = WL.GulaMerah,
                    contentColor = androidx.compose.ui.graphics.Color.White
                )
            ) { Text("Ya, Keluar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

/**
 * Banner notifikasi yang turun dari atas layar, lalu hilang otomatis.
 * Pakai bersama Box: letakкан paling akhir agar tampil di atas konten.
 *
 * Contoh:
 *   var notif by remember { mutableStateOf<String?>(null) }
 *   ...
 *   TopBanner(message = notif, onDismiss = { notif = null })
 */
@Composable
fun TopBanner(
    message: String?,
    onDismiss: () -> Unit,
    autoDismissMillis: Long = 2500
) {
    // Hilangkan otomatis setelah beberapa detik.
    androidx.compose.runtime.LaunchedEffect(message) {
        if (message != null) {
            kotlinx.coroutines.delay(autoDismissMillis)
            onDismiss()
        }
    }

    androidx.compose.animation.AnimatedVisibility(
        visible = message != null,
        enter = androidx.compose.animation.slideInVertically(initialOffsetY = { -it }) +
                androidx.compose.animation.fadeIn(),
        exit = androidx.compose.animation.slideOutVertically(targetOffsetY = { -it }) +
                androidx.compose.animation.fadeOut(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WL.GulaMerah),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔔", fontSize = 20.sp)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = message ?: "",
                        color = androidx.compose.ui.graphics.Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}