package com.proq.cryptosignals.domain.strategy

import com.proq.cryptosignals.domain.indicators.Indicators
import com.proq.cryptosignals.domain.model.Candle
import kotlin.math.abs

class StrategyEngine {

    fun generate(
        symbol: String,
        candles15m: List<Candle>,
        candles1h: List<Candle>,
        candles4h: List<Candle>,
        livePrice: Double,
        params: StrategyParams
    ): GeneratedSignal? {
        if (candles15m.size < 120 || candles1h.size < 120 || candles4h.size < 120) return null

        val closes15 = candles15m.map { it.close }
        val emaFast15 = Indicators.ema(closes15, params.emaFast)
        val emaSlow15 = Indicators.ema(closes15, params.emaSlow)
        val rsi15 = Indicators.rsi(closes15, params.rsiPeriod)
        val atr15 = Indicators.atr(candles15m, params.atrPeriod)

        val idx = closes15.lastIndex
        val fast = emaFast15[idx]
        val slow = emaSlow15[idx]
        val rsi = rsi15[idx]
        val atr = atr15[idx]

        val closes1h = candles1h.map { it.close }
        val closes4h = candles4h.map { it.close }
        val emaFast1h = Indicators.ema(closes1h, params.emaFast).last()
        val emaSlow1h = Indicators.ema(closes1h, params.emaSlow).last()
        val emaFast4h = Indicators.ema(closes4h, params.emaFast).last()
        val emaSlow4h = Indicators.ema(closes4h, params.emaSlow).last()

        val trendUp = emaFast1h >= emaSlow1h && emaFast4h >= emaSlow4h
        val trendDown = emaFast1h <= emaSlow1h && emaFast4h <= emaSlow4h

        val vols = candles15m.map { it.volume }
        val avgVol = vols.takeLast(40).average().coerceAtLeast(1e-9)
        val volRatio = vols.last() / avgVol

        val atrRecentAvg = atr15.takeLast(40).average().coerceAtLeast(1e-9)
        val atrRatio = atr / atrRecentAvg

        val longTrigger = fast > slow && trendUp && rsi >= params.rsiMinForLong
        val shortTrigger = fast < slow && trendDown && rsi <= params.rsiMaxForShort

        var score = 0
        val reasons = mutableListOf<String>()

        if (trendUp || trendDown) { score += 25; reasons += "Trend OK" }
        if (abs(fast - slow) / livePrice > 0.001) { score += 15; reasons += "EMA separation" }
        if (volRatio >= params.volumeFilterMinRatio) { score += 15; reasons += "Volume OK" }
        if (atrRatio <= params.shockMaxAtrRatio) { score += 10; reasons += "No shock" }
        if (rsi in 45.0..65.0) { score += 10; reasons += "RSI healthy" }
        if (longTrigger || shortTrigger) { score += 25; reasons += "Trigger" }

        score = score.coerceIn(0, 100)
        if (score < params.minScore) return null
        if (atr <= 0.0) return null

        val side = when {
            longTrigger -> "LONG"
            shortTrigger -> "SHORT"
            else -> return null
        }

        val entry = livePrice
        val sl = if (side == "LONG") entry - (atr * params.atrSlMult) else entry + (atr * params.atrSlMult)
        val tp1 = if (side == "LONG") entry + (atr * params.atrTp1Mult) else entry - (atr * params.atrTp1Mult)
        val tp2 = if (side == "LONG") entry + (atr * params.atrTp2Mult) else entry - (atr * params.atrTp2Mult)

        return GeneratedSignal(symbol, side, entry, sl, tp1, tp2, score, reasons.joinToString(" • "))
    }
}
