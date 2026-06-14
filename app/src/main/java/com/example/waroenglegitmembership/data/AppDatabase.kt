package com.example.waroenglegitmembership.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Kelas utama Room. Daftarkan semua entity & versi database di sini.
@Database(
    entities = [Member::class, Transaction::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Room menyediakan DAO lewat fungsi abstrak ini.
    abstract fun memberDao(): MemberDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Singleton: hanya ada 1 koneksi database di seluruh aplikasi.
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "waroeng_legit_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
