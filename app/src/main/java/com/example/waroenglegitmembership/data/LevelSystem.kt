package com.example.waroenglegitmembership.data

/**
 * Sistem level berbasis misi. Member naik level setelah menyelesaikan
 * SEMUA misi pada level saat ini. Level hanya naik (monotonic).
 */
object LevelSystem {

    const val BRONZE = "Bronze"
    const val SILVER = "Silver"
    const val GOLD = "Gold"

    /** Satu misi dalam checklist. */
    data class Mission(val title: String, val progress: String, val done: Boolean)

    /** Daftar misi untuk naik DARI [level]. Gold = level tertinggi (kosong). */
    fun missions(
        level: String,
        points: Int,
        purchases: Int,
        redeems: Int,
        passwordChanged: Boolean
    ): List<Mission> = when (level) {
        BRONZE -> listOf(
            Mission("Transaksi pertama", "${purchases.coerceAtMost(1)}/1 transaksi", purchases >= 1),
            Mission("Kumpulkan 50 poin", "${points.coerceAtMost(50)}/50 poin", points >= 50),
            Mission("Ganti password default", if (passwordChanged) "Selesai ✓" else "Belum", passwordChanged)
        )
        SILVER -> listOf(
            Mission("5 kali transaksi", "${purchases.coerceAtMost(5)}/5 transaksi", purchases >= 5),
            Mission("Kumpulkan 150 poin", "${points.coerceAtMost(150)}/150 poin", points >= 150),
            Mission("Tukar 1 reward", "${redeems.coerceAtMost(1)}/1 reward", redeems >= 1)
        )
        else -> emptyList()
    }

    /** Hitung level yang seharusnya, tidak pernah lebih rendah dari [currentLevel]. */
    fun computeLevel(
        currentLevel: String,
        points: Int,
        purchases: Int,
        redeems: Int,
        passwordChanged: Boolean
    ): String {
        var lvl = currentLevel.ifEmpty { BRONZE }
        if (rank(lvl) <= rank(BRONZE) &&
            missions(BRONZE, points, purchases, redeems, passwordChanged).all { it.done }
        ) lvl = SILVER
        if (rank(lvl) <= rank(SILVER) &&
            missions(SILVER, points, purchases, redeems, passwordChanged).all { it.done }
        ) lvl = GOLD
        return if (rank(lvl) >= rank(currentLevel)) lvl else currentLevel
    }

    /** Progress 0..1 dari daftar misi. */
    fun progress(missions: List<Mission>): Float =
        if (missions.isEmpty()) 1f else missions.count { it.done }.toFloat() / missions.size

    /** Nama level berikutnya, atau null jika sudah Gold. */
    fun nextLevel(level: String): String? = when (level) {
        BRONZE -> SILVER
        SILVER -> GOLD
        else -> null
    }

    private fun rank(level: String) = when (level) { SILVER -> 1; GOLD -> 2; else -> 0 }
}