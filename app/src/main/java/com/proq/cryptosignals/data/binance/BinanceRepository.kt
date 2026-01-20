package com.proq.cryptosignals.data.binance

import com.proq.cryptosignals.domain.model.Candle

class BinanceRepository(private val api: BinanceApi) {

    suspend fun getLivePrice(symbol: String): Double =
        api.tickerPrice(symbol).price.toDouble()

    suspend fun getTopUsdtSymbols(limit: Int = 40): List<String> {
        val info = api.exchangeInfo()
        val spotUsdt = info.symbols
            .asSequence()
            .filter { it.status == "TRADING" && it.quoteAsset == "USDT" && it.isSpotTradingAllowed }
            .map { it.symbol }
            .toSet()

        val tickers = api.ticker24h()
            .asSequence()
            .filter { it.symbol in spotUsdt }
            .map { it.symbol to (it.quoteVolume?.toDoubleOrNull() ?: 0.0) }
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
            .toList()

        val majors = listOf("BTCUSDT", "ETHUSDT", "BNBUSDT", "SOLUSDT", "XRPUSDT")
        return (majors + tickers).distinct()
    }

    suspend fun getCandles(symbol: String, interval: String, limit: Int = 500): List<Candle> {
        val raw = api.klines(symbol, interval, limit)
        return raw.mapNotNull { row ->
            try {
                Candle(
                    openTime = (row[0] as Double).toLong(),
                    open = row[1].toString().toDouble(),
                    high = row[2].toString().toDouble(),
                    low = row[3].toString().toDouble(),
                    close = row[4].toString().toDouble(),
                    volume = row[5].toString().toDouble(),
                    closeTime = (row[6] as Double).toLong()
                )
            } catch (_: Exception) { null }
        }
    }
}
