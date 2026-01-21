package com.proq.cryptosignals.data.binance

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.max
import kotlin.random.Random

/**
 * Simple cooperative rate limiter to reduce risk of Binance IP bans.
 * We keep a minimum delay between API requests and add small jitter.
 */
class RateLimiter(
    private val minIntervalMs: Long = 220L,
    private val jitterMs: Long = 120L
) {
    private val mu = Mutex()
    private var lastMs: Long = 0L

    suspend fun acquire() {
        mu.withLock {
            val now = System.currentTimeMillis()
            val wait = max(0L, (lastMs + minIntervalMs) - now) + Random.nextLong(0, jitterMs + 1)
            lastMs = now + wait
            if (wait > 0) delay(wait)
        }
    }
}
