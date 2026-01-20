package com.proq.cryptosignals.domain.strategy

data class GeneratedSignal(
    val symbol: String,
    val side: String, // LONG / SHORT
    val entry: Double,
    val sl: Double,
    val tp1: Double,
    val tp2: Double,
    val score: Int,
    val reason: String
)
