package com.proq.cryptosignals.domain.strategy

data class StrategyParams(
    val emaFast: Int = 20,
    val emaSlow: Int = 50,
    val rsiPeriod: Int = 14,
    val rsiMinForLong: Double = 50.0,
    val rsiMaxForShort: Double = 50.0,
    val atrPeriod: Int = 14,
    val atrSlMult: Double = 1.7,
    val atrTp1Mult: Double = 2.0,
    val atrTp2Mult: Double = 3.0,
    val volumeFilterMinRatio: Double = 0.8,
    val shockMaxAtrRatio: Double = 2.8,
    val minScore: Int = 80
) {
    companion object { fun default() = StrategyParams() }
}
