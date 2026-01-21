package com.proq.cryptosignals.data.binance

import com.proq.cryptosignals.domain.model.Candle
import kotlinx.coroutines.delay
import retrofit2.HttpException
import kotlin.random.Random

class BinanceRepository(private val api: BinanceApi) {

    // Reduce request bursts to avoid bans
    private val rateLimiter = RateLimiter(minIntervalMs = 240L, jitterMs = 160L)

    private var cachedExchangeInfo: ExchangeInfo? = null
    private var cachedExchangeInfoAt: Long = 0L

    private var cachedTopSymbols: List<String>? = null
    private var cachedTopSymbolsAt: Long = 0L

    suspend fun getLivePrice(symbol: String): Double = callWithRetry("tickerPrice") {
        api.tickerPrice(symbol).price.toDouble()
    }

    suspend fun getTopUsdtSymbols(limit: Int = 40): List<String> {
        // cache for 2 minutes to reduce traffic
        val now = System.currentTimeMillis()
        cachedTopSymbols?.let { cached ->
            if (now - cachedTopSymbolsAt <= 120_000L) return cached
        }

        val info = getExchangeInfoCached()
        val spotUsdt = info.symbols
            .asSequence()
            .filter { it.status == "TRADING" && it.quoteAsset == "USDT" && it.isSpotTradingAllowed }
            .map { it.symbol }
            .toSet()

        val tickers = callWithRetry("ticker24h") { api.ticker24h() }
            .asSequence()
            .filter { it.symbol in spotUsdt }
            .map { it.symbol to (it.quoteVolume?.toDoubleOrNull() ?: 0.0) }
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
            .toList()

        val majors = listOf("BTCUSDT", "ETHUSDT", "BNBUSDT", "SOLUSDT", "XRPUSDT")
        val out = (majors + tickers).distinct()
        cachedTopSymbols = out
        cachedTopSymbolsAt = now
        return out
    }

    suspend fun getCandles(symbol: String, interval: String, limit: Int = 500): List<Candle> {
        val raw = callWithRetry("klines") { api.klines(symbol, interval, limit) }
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

    private suspend fun getExchangeInfoCached(): ExchangeInfo {
        val now = System.currentTimeMillis()
        cachedExchangeInfo?.let { cached ->
            // cache for 12 hours
            if (now - cachedExchangeInfoAt <= 12 * 60 * 60_000L) return cached
        }
        val fresh = callWithRetry("exchangeInfo") { api.exchangeInfo() }
        cachedExchangeInfo = fresh
        cachedExchangeInfoAt = now
        return fresh
    }

    private suspend fun <T> callWithRetry(tag: String, block: suspend () -> T): T {
        var attempt = 0
        var backoffMs = 400L
        while (true) {
            attempt += 1
            try {
                // cooperative rate limiting (important!)
                rateLimiter.acquire()
                return block()
            } catch (e: HttpException) {
                val code = e.code()
                // 429/418: too many requests / banned
                if (code == 429 || code == 418) {
                    val jitter = Random.nextLong(0, 400)
                    delay(backoffMs + jitter)
                    backoffMs = (backoffMs * 2).coerceAtMost(12_000L)
                    if (attempt < 6) continue
                }
                throw e
            } catch (e: Exception) {
                // network hiccups
                if (attempt < 3) {
                    delay(300L + Random.nextLong(0, 250))
                    continue
                }
                throw e
            }
        }
    }
}
