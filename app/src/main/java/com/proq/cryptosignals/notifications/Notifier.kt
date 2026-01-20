package com.proq.cryptosignals.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class Notifier(private val context: Context) {

    private val nm = NotificationManagerCompat.from(context)
    private val channelIdSignals = "signals"
    private val channelIdPnl = "pnl"

    init { createChannels() }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val m = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            m.createNotificationChannel(NotificationChannel(channelIdSignals, "Signals", NotificationManager.IMPORTANCE_DEFAULT))
            m.createNotificationChannel(NotificationChannel(channelIdPnl, "Profit/Loss", NotificationManager.IMPORTANCE_DEFAULT))
        }
    }

    fun notifySignal(title: String, body: String) {
        val n = NotificationCompat.Builder(context, channelIdSignals)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .build()
        nm.notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), n)
    }

    fun notifyPnl(title: String, body: String) {
        val n = NotificationCompat.Builder(context, channelIdPnl)
            .setSmallIcon(android.R.drawable.star_big_on)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .build()
        nm.notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), n)
    }
}
