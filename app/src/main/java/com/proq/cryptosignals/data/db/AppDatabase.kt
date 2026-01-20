package com.proq.cryptosignals.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SignalEntity::class, TradeEntity::class, PriceCacheEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun signalDao(): SignalDao
    abstract fun tradeDao(): TradeDao
    abstract fun priceCacheDao(): PriceCacheDao

    companion object {
        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "crypto_signals.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}
