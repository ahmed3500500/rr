package com.proq.cryptosignals.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signals")
data class SignalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val side: String,
    val entryPrice: Double,
    val sl: Double,
    val tp1: Double,
    val tp2: Double,
    val score: Int,
    val reason: String,
    val createdAtMs: Long,
    val priceAsOfMs: Long,
    val priceIsLive: Boolean
)

@Entity(tableName = "trades")
data class TradeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val signalId: Long,
    val symbol: String,
    val side: String,
    val entryPrice: Double,
    val sl: Double,
    val tp1: Double,
    val tp2: Double,
    val state: String,
    val capitalUsdt: Double,
    val openedAtMs: Long,
    val closedAtMs: Long? = null,
    val realizedPnlUsdt: Double? = null,
    val realizedPct: Double? = null
)

@Entity(tableName = "price_cache")
data class PriceCacheEntity(
    @PrimaryKey val symbol: String,
    val price: Double,
    val asOfMs: Long
)
