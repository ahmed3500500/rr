package com.proq.cryptosignals.work

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class TradeMonitorScheduler(private val context: Context) {
    fun kick() {
        val req = OneTimeWorkRequestBuilder<TradeMonitorWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "trade_monitor_once",
            ExistingWorkPolicy.REPLACE,
            req
        )
    }
}
