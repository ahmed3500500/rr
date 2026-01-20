package com.proq.cryptosignals.domain.indicators

import com.proq.cryptosignals.domain.model.Candle
import kotlin.math.abs
import kotlin.math.max

object Indicators {

    fun ema(values: List<Double>, period: Int): List<Double> {
        if (values.isEmpty()) return emptyList()
        val k = 2.0 / (period + 1.0)
        val out = MutableList(values.size) { 0.0 }
        var prev = values.first()
        out[0] = prev
        for (i in 1 until values.size) {
            val v = values[i]
            prev = v * k + prev * (1.0 - k)
            out[i] = prev
        }
        return out
    }

    fun rsi(closes: List<Double>, period: Int = 14): List<Double> {
        if (closes.size < 2) return List(closes.size) { 50.0 }
        val out = MutableList(closes.size) { 50.0 }

        var gain = 0.0
        var loss = 0.0
        val start = minOf(period, closes.size - 1)
        for (i in 1..start) {
            val diff = closes[i] - closes[i - 1]
            if (diff >= 0) gain += diff else loss += abs(diff)
        }
        var avgGain = gain / period
        var avgLoss = loss / period
        out[start] = if (avgLoss == 0.0) 100.0 else 100.0 - (100.0 / (1.0 + (avgGain / avgLoss)))

        for (i in start + 1 until closes.size) {
            val diff = closes[i] - closes[i - 1]
            val g = if (diff > 0) diff else 0.0
            val l = if (diff < 0) abs(diff) else 0.0
            avgGain = (avgGain * (period - 1) + g) / period
            avgLoss = (avgLoss * (period - 1) + l) / period
            out[i] = if (avgLoss == 0.0) 100.0 else 100.0 - (100.0 / (1.0 + (avgGain / avgLoss)))
        }
        return out
    }

    fun atr(candles: List<Candle>, period: Int = 14): List<Double> {
        if (candles.size < 2) return List(candles.size) { 0.0 }
        val tr = MutableList(candles.size) { 0.0 }
        tr[0] = candles[0].high - candles[0].low
        for (i in 1 until candles.size) {
            val c = candles[i]
            val prevClose = candles[i - 1].close
            val range1 = c.high - c.low
            val range2 = abs(c.high - prevClose)
            val range3 = abs(c.low - prevClose)
            tr[i] = max(range1, max(range2, range3))
        }
        return ema(tr, period)
    }
}
