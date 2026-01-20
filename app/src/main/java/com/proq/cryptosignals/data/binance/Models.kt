package com.proq.cryptosignals.data.binance

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TickerPrice(val symbol: String, val price: String)

@JsonClass(generateAdapter = true)
data class Ticker24h(
    val symbol: String,
    val quoteVolume: String? = null,
    val weightedAvgPrice: String? = null,
    val lastPrice: String? = null,
    val priceChangePercent: String? = null
)

@JsonClass(generateAdapter = true)
data class ExchangeInfo(val symbols: List<SymbolInfo>)

@JsonClass(generateAdapter = true)
data class SymbolInfo(
    val symbol: String,
    val status: String,
    val baseAsset: String,
    val quoteAsset: String,
    val isSpotTradingAllowed: Boolean
)
