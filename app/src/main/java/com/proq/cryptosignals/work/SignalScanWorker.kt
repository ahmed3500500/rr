package com.proq.cryptosignals.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.proq.cryptosignals.data.db.SignalEntity
import com.proq.cryptosignals.data.db.TradeEntity
import com.proq.cryptosignals.di.AppGraph
import com.proq.cryptosignals.domain.strategy.StrategyEngine
import com.proq.cryptosignals.domain.strategy.StrategyParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SignalScanWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val graph = AppGraph(applicationContext)
        val settings = graph.settingsRepository
        val repo = graph.signalRepository
        val binance = graph.binanceRepository
        val notifier = graph.notifier

        try {
            val minScore = settings.minScoreFlow.first()
            val capital = settings.capitalPerSignalFlow.first()

            val params = StrategyParams.default().copy(minScore = minScore)
            val engine = StrategyEngine()

            val symbols = binance.getTopUsdtSymbols(limit = 35)

            for (symbol in symbols) {
                if (repo.isInCooldown(symbol, 120)) continue

                val live = binance.getLivePrice(symbol)
                val now = System.currentTimeMillis()
                repo.upsertPriceCache(symbol, live, now)

                val c15 = binance.getCandles(symbol, "15m", 500)
                val c1h = binance.getCandles(symbol, "1h", 300)
                val c4h = binance.getCandles(symbol, "4h", 300)

                val sig = engine.generate(symbol, c15, c1h, c4h, live, params) ?: continue

                repo.insertSignalAndTrade(
                    signal = SignalEntity(
                        symbol = sig.symbol,
                        side = sig.side,
                        entryPrice = sig.entry,
                        sl = sig.sl,
                        tp1 = sig.tp1,
                        tp2 = sig.tp2,
                        score = sig.score,
                        reason = sig.reason,
                        createdAtMs = now,
                        priceAsOfMs = now,
                        priceIsLive = true
                    ),
                    trade = TradeEntity(
                        signalId = 0,
                        symbol = sig.symbol,
                        side = sig.side,
                        entryPrice = sig.entry,
                        sl = sig.sl,
                        tp1 = sig.tp1,
                        tp2 = sig.tp2,
                        state = "RUNNING",
                        capitalUsdt = capital,
                        openedAtMs = now
                    )
                )

                notifier.notifySignal(
                    title = "توصية ${sig.symbol} (${sig.side}) • Score ${sig.score}",
                    body = "Entry ${fmt(sig.entry)} | SL ${fmt(sig.sl)} | TP1 ${fmt(sig.tp1)} | TP2 ${fmt(sig.tp2)}\n\nإخلاء مسؤولية: ليست نصيحة مالية."
                )

                graph.tradeMonitorScheduler.kick()
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun fmt(v: Double): String = "%,.6f".format(v)
}
