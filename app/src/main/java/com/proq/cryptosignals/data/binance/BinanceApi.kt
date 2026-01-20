package com.proq.cryptosignals.data.binance

import retrofit2.http.GET
import retrofit2.http.Query

interface BinanceApi {
    @GET("api/v3/ticker/price")
    suspend fun tickerPrice(@Query("symbol") symbol: String): TickerPrice

    @GET("api/v3/ticker/24hr")
    suspend fun ticker24h(): List<Ticker24h>

    @GET("api/v3/exchangeInfo")
    suspend fun exchangeInfo(): ExchangeInfo

    @GET("api/v3/klines")
    suspend fun klines(
        @Query("symbol") symbol: String,
        @Query("interval") interval: String,
        @Query("limit") limit: Int = 500
    ): List<List<Any>>
}
