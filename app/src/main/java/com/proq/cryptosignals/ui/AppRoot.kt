package com.proq.cryptosignals.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.proq.cryptosignals.ui.screens.BacktestScreen
import com.proq.cryptosignals.ui.screens.DashboardScreen
import com.proq.cryptosignals.ui.screens.SettingsScreen
import com.proq.cryptosignals.ui.screens.SignalsScreen

@Composable
fun AppRoot() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(
                onOpenSignals = { nav.navigate("signals") },
                onOpenSettings = { nav.navigate("settings") },
                onOpenBacktest = { nav.navigate("backtest") }
            )
        }
        composable("signals") { SignalsScreen(onBack = { nav.popBackStack() }) }
        composable("settings") { SettingsScreen(onBack = { nav.popBackStack() }) }
        composable("backtest") { BacktestScreen(onBack = { nav.popBackStack() }) }
    }
}
