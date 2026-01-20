package com.proq.cryptosignals.di

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import com.proq.cryptosignals.data.binance.BinanceApiFactory
import com.proq.cryptosignals.data.binance.BinanceRepository
import com.proq.cryptosignals.data.db.AppDatabase
import com.proq.cryptosignals.data.repository.SignalRepository
import com.proq.cryptosignals.data.settings.SettingsRepository
import com.proq.cryptosignals.notifications.Notifier
import com.proq.cryptosignals.work.TradeMonitorScheduler

class AppGraph(context: Context) {
    private val appContext = context.applicationContext

    val settingsRepository = SettingsRepository(appContext)

    private val db = AppDatabase.build(appContext)
    private val api = BinanceApiFactory.create()

    val binanceRepository = BinanceRepository(api)
    val signalRepository = SignalRepository(db.signalDao(), db.tradeDao(), db.priceCacheDao())
    val notifier = Notifier(appContext)
    val tradeMonitorScheduler = TradeMonitorScheduler(appContext)
}

val LocalAppGraph = staticCompositionLocalOf<AppGraph> { error("AppGraph not provided") }
