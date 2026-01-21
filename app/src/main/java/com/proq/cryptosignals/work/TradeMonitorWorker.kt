package com.proq.cryptosignals.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.proq.cryptosignals.di.AppGraph
import com.proq.cryptosignals.util.SuccessLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TradeMonitorWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val graph = AppGraph(applicationContext)
        val repo = graph.signalRepository
        val binance = graph.binanceRepository
        val notifier = graph.notifier
        val successLogger = SuccessLogger(applicationContext)

        try {
            val active = repo.getActiveTrades()
            if (active.isEmpty()) return@withContext Result.success()

            for (t in active) {
                val price = binance.getLivePrice(t.symbol)
                val entry = t.entryPrice
                val pct = if (t.side == "LONG") ((price - entry) / entry) * 100.0 else ((entry - price) / entry) * 100.0
                val pnl = t.capitalUsdt * (pct / 100.0)

                val hitSl = if (t.side == "LONG") price <= t.sl else price >= t.sl
                val hitTp1 = if (t.side == "LONG") price >= t.tp1 else price <= t.tp1
                val hitTp2 = if (t.side == "LONG") price >= t.tp2 else price <= t.tp2

                if (hitTp2) {
                    repo.closeTrade(t.id, "TP2", pnl, pct)
                    notifier.notifyPnl("تحقق الهدف TP2 ✅ ${t.symbol}", "الربح التقريبي: ${fmt(pnl)} USDT (${fmt(pct)}%)\nالسعر: ${fmt(price)}")
                    if (pnl > 0) {
                        successLogger.logSuccessLine(
                            "${t.symbol} | ${t.side} | Entry=${fmt(t.entryPrice)} | TP2=${fmt(t.tp2)} | PNL=${fmt(pnl)}USDT | (${fmt(pct)}%)"
                        )
                    }
                } else if (hitSl) {
                    repo.closeTrade(t.id, "SL", pnl, pct)
                    notifier.notifyPnl("تم ضرب وقف الخسارة ⛔ ${t.symbol}", "النتيجة التقريبية: ${fmt(pnl)} USDT (${fmt(pct)}%)\nالسعر: ${fmt(price)}")
                } else if (hitTp1 && t.state == "RUNNING") {
                    notifier.notifyPnl("وصل TP1 🔔 ${t.symbol}", "حاليًا: ${fmt(pnl)} USDT (${fmt(pct)}%)\nالسعر: ${fmt(price)}")
                }
            }

            // Best-effort near-live monitor (قد يتأخر حسب قيود Android)
            graph.tradeMonitorScheduler.kick()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun fmt(v: Double): String = "%,.4f".format(v)
}
