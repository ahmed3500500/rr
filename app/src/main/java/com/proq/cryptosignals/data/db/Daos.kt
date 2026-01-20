package com.proq.cryptosignals.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SignalDao {
    @Query("SELECT * FROM signals ORDER BY createdAtMs DESC LIMIT 200")
    fun flowSignals(): Flow<List<SignalEntity>>

    @Insert
    suspend fun insert(signal: SignalEntity): Long

    @Query("SELECT COUNT(*) FROM signals WHERE symbol = :symbol AND createdAtMs > :sinceMs")
    suspend fun countRecent(symbol: String, sinceMs: Long): Int
}

@Dao
interface TradeDao {
    @Query("SELECT * FROM trades WHERE state IN ('RUNNING','TP1') ORDER BY openedAtMs DESC")
    suspend fun getActiveTrades(): List<TradeEntity>

    @Query("SELECT * FROM trades ORDER BY openedAtMs DESC LIMIT 200")
    fun flowTrades(): Flow<List<TradeEntity>>

    @Query("SELECT COUNT(*) FROM trades WHERE state IN ('RUNNING','TP1')")
    suspend fun countOpen(): Int

    @Query("SELECT COUNT(*) FROM trades WHERE state IN ('TP2','SL','CLOSED')")
    suspend fun countClosed(): Int

    @Query("SELECT SUM(realizedPnlUsdt) FROM trades WHERE realizedPnlUsdt IS NOT NULL")
    suspend fun sumPnl(): Double?

    @Query("SELECT SUM(CASE WHEN realizedPnlUsdt > 0 THEN 1 ELSE 0 END) FROM trades WHERE realizedPnlUsdt IS NOT NULL")
    suspend fun countWins(): Int?

    @Query("SELECT COUNT(*) FROM trades WHERE realizedPnlUsdt IS NOT NULL")
    suspend fun countClosedWithPnl(): Int

    @Insert
    suspend fun insert(trade: TradeEntity): Long

    @Query("UPDATE trades SET state = :state, closedAtMs = :closedAtMs, realizedPnlUsdt = :pnlUsdt, realizedPct = :pct WHERE id = :id")
    suspend fun closeTrade(id: Long, state: String, closedAtMs: Long, pnlUsdt: Double, pct: Double)
}

@Dao
interface PriceCacheDao {
    @Query("SELECT * FROM price_cache WHERE symbol = :symbol LIMIT 1")
    suspend fun get(symbol: String): PriceCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(p: PriceCacheEntity)
}
