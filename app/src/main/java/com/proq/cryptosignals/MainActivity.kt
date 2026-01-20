package com.proq.cryptosignals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.proq.cryptosignals.work.SignalScanWorker
import com.proq.cryptosignals.ui.AppRoot
import com.proq.cryptosignals.ui.ProvideGraph
import com.proq.cryptosignals.ui.theme.CryptoSignalsTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val scanReq = PeriodicWorkRequestBuilder<SignalScanWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "signal_scan_periodic",
            ExistingPeriodicWorkPolicy.UPDATE,
            scanReq
        )

        setContent {
            ProvideGraph {
                CryptoSignalsTheme { AppRoot() }
            }
        }
    }
}
