package com.proq.cryptosignals.domain.backtest

import com.proq.cryptosignals.data.binance.BinanceRepository
import com.proq.cryptosignals.domain.strategy.StrategyEngine
import com.proq.cryptosignals.domain.strategy.StrategyParams
import kotlin.math.max

data class BacktestResult(
    val trades: Int,
    val wins: Int,
    val losses: Int,
    val winRatePct: Double,
    val netPct: Double,
    val maxDrawdownPct: Double
)

class BacktestEngine(private val binance: BinanceRepository) {

    suspend fun run(symbol: String, params: StrategyParams): BacktestResult {
        val candles = binance.getCandles(symbol, "15m", 500)
        if (candles.size < 200) return BacktestResult(0,0,0,0.0,0.0,0.0)

        val engine = StrategyEngine()
        var inTrade = false
        var side = ""
        var entry = 0.0
        var sl = 0.0
        var tp2 = 0.0

        var equity = 100.0
        var peak = 100.0
        var maxDd = 0.0

        var trades = 0
        var wins = 0
        var losses = 0

        for (i in 200 until candles.size) {
            val slice = candles.subList(0, i + 1)
            val live = slice.last().close

            if (!inTrade) {
                val sig = engine.generate(symbol, slice, slice, slice, live, params)
                if (sig != null) {
                    inTrade = true
                    side = sig.side
                    entry = sig.entry
                    sl = sig.sl
                    tp2 = sig.tp2
                    trades += 1
                }
            } else {
                val c = slice.last()
                val hitSl = if (side == "LONG") c.low <= sl else c.high >= sl
                val hitTp = if (side == "LONG") c.high >= tp2 else c.low <= tp2

                if (hitSl || hitTp) {
                    val retPct = if (hitTp) 1.5 else -1.0 // simplified proxy
                    equity *= (1.0 + retPct / 100.0)
                    if (hitTp) wins++ else losses++
                    inTrade = false

                    peak = max(peak, equity)
                    val dd = (peak - equity) / peak * 100.0
                    maxDd = max(maxDd, dd)
                }
            }
        }

        val winRate = if (trades == 0) 0.0 else (wins.toDouble() / trades.toDouble()) * 100.0
        val net = equity - 100.0
        return BacktestResult(trades, wins, losses, winRate, net, maxDd)
    }
}
