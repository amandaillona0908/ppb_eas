package com.example.waroenglegitmembership.data

import android.content.Context
import androidx.room.*

/** Converter agar Room bisa menyimpan enum TxType sebagai teks. */
class Converters {
    @TypeConverter
    fun fromTxType(value: TxType): String = value.name

    @TypeConverter
    fun toTxType(value: String): TxType = TxType.valueOf(value)
}

@Database(
    entities = [Member::class, Transaction::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun memberDao(): MemberDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "waroeng_legit_db"
                )
                    .fallbackToDestructiveMigration() // reset data jika skema berubah
                    .build()
                    .also { INSTANCE = it }
            }
    }
}