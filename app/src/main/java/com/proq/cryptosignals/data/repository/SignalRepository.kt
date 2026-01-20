package com.proq.cryptosignals.data.repository

import com.proq.cryptosignals.data.db.PriceCacheDao
import com.proq.cryptosignals.data.db.PriceCacheEntity
import com.proq.cryptosignals.data.db.SignalDao
import com.proq.cryptosignals.data.db.SignalEntity
import com.proq.cryptosignals.data.db.TradeDao
import com.proq.cryptosignals.data.db.TradeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class UiSignal(
    val id: Long,
    val symbol: String,
    val side: String,
    val entryPrice: String,
    val sl: String,
    val tp1: String,
    val tp2: String,
    val score: Int,
    val isLive: Boolean,
    val priceAgeSeconds: Long
)

data class DashboardSummary(
    val openTrades: Int = 0,
    val closedTrades: Int = 0,
    val totalPnlUsdt: Double = 0.0,
    val winRatePct: Double = 0.0
)

class SignalRepository(
    private val signalDao: SignalDao,
    private val tradeDao: TradeDao,
    private val priceCacheDao: PriceCacheDao
) {
    fun signalsFlow(): Flow<List<UiSignal>> =
        signalDao.flowSignals().map { list ->
            val now = System.currentTimeMillis()
            list.map { s ->
                UiSignal(
                    id = s.id,
                    symbol = s.symbol,
                    side = s.side,
                    entryPrice = s.entryPrice.format(6),
                    sl = s.sl.format(6),
                    tp1 = s.tp1.format(6),
                    tp2 = s.tp2.format(6),
                    score = s.score,
                    isLive = s.priceIsLive && (now - s.priceAsOfMs) <= 15_000,
                    priceAgeSeconds = ((now - s.priceAsOfMs) / 1000).coerceAtLeast(0)
                )
            }
        }

    suspend fun insertSignalAndTrade(signal: SignalEntity, trade: TradeEntity): Long {
        val id = signalDao.insert(signal)
        tradeDao.insert(trade.copy(signalId = id))
        return id
    }

    suspend fun isInCooldown(symbol: String, cooldownMinutes: Int): Boolean {
        val since = System.currentTimeMillis() - cooldownMinutes * 60_000L
        return signalDao.countRecent(symbol, since) > 0
    }

    suspend fun upsertPriceCache(symbol: String, price: Double, asOfMs: Long) {
        priceCacheDao.upsert(PriceCacheEntity(symbol, price, asOfMs))
    }

    suspend fun getCachedPrice(symbol: String): PriceCacheEntity? = priceCacheDao.get(symbol)

    suspend fun getActiveTrades(): List<TradeEntity> = tradeDao.getActiveTrades()

    suspend fun closeTrade(id: Long, state: String, pnlUsdt: Double, pct: Double) {
        tradeDao.closeTrade(id, state, System.currentTimeMillis(), pnlUsdt, pct)
    }

    suspend fun dashboardSummaryOnce(): DashboardSummary {
        val open = tradeDao.countOpen()
        val closed = tradeDao.countClosed()
        val pnl = tradeDao.sumPnl() ?: 0.0
        val wins = tradeDao.countWins() ?: 0
        val total = tradeDao.countClosedWithPnl().coerceAtLeast(1)
        val winRate = wins.toDouble() / total.toDouble() * 100.0
        return DashboardSummary(open, closed, pnl, winRate)
    }
}

private fun Double.format(d: Int): String = "%.${d}f".format(this)
