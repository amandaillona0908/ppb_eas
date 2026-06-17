# Waroeng Legit Membership

Aplikasi membership & loyalty card untuk **Waroeng Legit** — warung lokal berbasis kue dan jajanan tradisional Indonesia. Dibangun sebagai proyek akhir semester mata kuliah **Pemrograman Perangkat Bergerak (Semester 8)**.

---

## Deskripsi

Waroeng Legit Membership adalah aplikasi Android offline-first yang mengelola program loyalitas pelanggan. Barista dapat mendaftarkan member baru dan mencatat transaksi, sementara pelanggan bisa memantau poin, naik level, dan menukarkan reward.

---

## Fitur Utama

### Barista
- Login dengan kode akses barista
- Dashboard statistik: total member, total poin beredar, distribusi level
- Daftar member dengan fitur pencarian (nama/email)
- Registrasi member baru (auto-generate password default)
- Catat transaksi pembelian → otomatis konversi ke poin (Rp 10.000 = 1 poin)

### Pelanggan
- Login dengan email & password
- Dashboard pribadi dengan poin & level saat ini
- **Sistem level gamifikasi** berbasis misi (bukan hanya akumulasi poin):
  - 🥉 **Bronze** → Misi: Transaksi pertama, kumpulkan 50 poin, ganti password
  - 🥈 **Silver** → Misi: 5 total transaksi, kumpulkan 150 poin, tukar 1 reward
  - 🥇 **Gold** → Level tertinggi
- Progress misi real-time dengan progress bar & checklist
- Kartu membership digital (nama, ID, level, poin)
- Riwayat transaksi (pembelian & penukaran reward)
- Tukar poin dengan reward:
  | Reward | Poin |
  |---|---|
  | 🍡 Klepon | 10 |
  | 🍢 Tempe Mendoan | 15 |
  | 🟤 Onde-onde | 20 |
  | 🥤 Es Dawet | 25 |
  | 🍰 Brownies Lumer | 40 |
- Edit profil (nama, email, nomor telepon)
- Ganti password dengan verifikasi password lama

---

## Tech Stack

| Kategori | Teknologi |
|---|---|
| Bahasa | Kotlin 2.1.10 |
| UI Framework | Jetpack Compose (BOM 2024.12.01) |
| Design System | Material 3 |
| Navigasi | Navigation Compose 2.8.5 |
| State Management | ViewModel + StateFlow |
| Database | Room 2.6.1 (SQLite) |
| Arsitektur | MVVM + Repository Pattern |
| Code Gen | KSP 2.1.10-1.0.30 |
| Security | SHA-256 password hashing |
| QR Code | ZXing Core 3.5.3 |
| Min SDK | API 34 (Android 14) |
| Target SDK | API 35 (Android 15) |

---

## Arsitektur

```
Single Activity (MainActivity)
        │
        └── NavHost (Compose Navigation)
                │
                ├── SplashScreen
                ├── RoleScreen
                ├── BaristaHomeScreen
                │     ├── Dashboard Tab
                │     └── Member List Tab
                ├── AddMemberScreen
                ├── MemberDetailScreen (barista view)
                ├── CustomerLoginScreen
                └── CustomerDashboardScreen
                      ├── Home Tab (poin, misi, menu)
                      ├── Membership Card Tab
                      ├── History Tab
                      ├── Reward Tab
                      └── Profile Tab
```

**Pattern:** MVVM (Model–View–ViewModel) dengan Repository pattern untuk abstraksi data.

---

## Struktur Folder

```
ppb_eas/
├── app/src/main/java/com/example/waroenglegitmembership/
│   ├── MainActivity.kt                  # Entry point, setup navigasi
│   ├── data/
│   │   ├── AppDatabase.kt               # Room DB singleton (v4)
│   │   ├── Member.kt                    # Entity: member
│   │   ├── Transaction.kt               # Entity: transaksi
│   │   ├── TxType.kt                    # Enum: PURCHASE / REDEEM
│   │   ├── LevelSystem.kt               # Logika level & misi
│   │   ├── MemberDao.kt                 # DAO: operasi member
│   │   └── TransactionDao.kt            # DAO: operasi transaksi
│   ├── repository/
│   │   └── MembershipRepository.kt      # Abstraksi layer data
│   ├── ui/
│   │   ├── screens/
│   │   │   ├── SplashScreen.kt
│   │   │   ├── RoleScreen.kt
│   │   │   ├── BaristaHomeScreen.kt
│   │   │   ├── AddMemberScreen.kt
│   │   │   ├── MemberDetailScreen.kt
│   │   │   ├── CustomerLoginScreen.kt
│   │   │   ├── CustomerDashboardScreen.kt
│   │   │   ├── CustomerComponents.kt    # HistoryTab, RewardTab
│   │   │   └── SharedComponents.kt      # Komponen reusable
│   │   └── theme/
│   │       └── Theme.kt                 # Palet warna kustom (13 warna)
│   ├── viewmodel/
│   │   ├── MembershipViewModel.kt       # State & business logic
│   │   ├── ViewModelFactory.kt          # Factory ViewModel
│   │   └── LoginResult.kt              # Sealed interface login state
│   └── util/
│       └── PasswordHasher.kt            # SHA-256 hashing utility
├── gradle/
│   └── libs.versions.toml               # Version catalog terpusat
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## Cara Menjalankan

### Prasyarat
- Android Studio Ladybug (2024.2+) atau lebih baru
- JDK 21
- Android SDK API 34+
- Emulator atau perangkat fisik Android 14+

### Langkah

```bash
# Clone repository
git clone <repo-url>
cd ppb_eas

# Buka di Android Studio, tunggu Gradle sync selesai
# Klik Run (▶) atau:
./gradlew assembleDebug
```

### Akun Default

| Role | Kredensial |
|---|---|
| Barista | Kode akses: `barista123` |
| Pelanggan | Email & password yang didaftarkan barista |
| Password default member | `waroeng{memberId}` (contoh: `waroeng1`) |

---

## Catatan

- Aplikasi berjalan **sepenuhnya offline** — tidak ada backend/API eksternal
- Data tersimpan lokal di SQLite via Room
- Migrasi database bersifat destruktif (data hilang saat update schema)
- Password menggunakan SHA-256 (disederhanakan untuk kebutuhan lokal)

---
