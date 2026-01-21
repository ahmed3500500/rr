package com.proq.cryptosignals.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.proq.cryptosignals.di.LocalAppGraph
import com.proq.cryptosignals.ui.components.AquaBackground
import com.proq.cryptosignals.ui.components.GlassCard
import com.proq.cryptosignals.data.repository.dashboardSummaryFlow

@Composable
fun DashboardScreen(
    onOpenSignals: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenBacktest: () -> Unit,
    onOpenWinners: () -> Unit
) {
    val graph = LocalAppGraph.current
    val repo = graph.signalRepository

    val summaryFlow = repo.dashboardSummaryFlow()
    val summary by summaryFlow.collectAsState(initial = com.proq.cryptosignals.data.repository.DashboardSummary())

    AquaBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("لوحة التحكم", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)

            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("النتائج", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text("الصفقات المفتوحة: ${summary.openTrades}", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
                    Text("الصفقات المغلقة: ${summary.closedTrades}", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
                    Text("إجمالي الربح/الخسارة: ${summary.totalPnlUsdt.format(2)} USDT", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
                    Text("نسبة الربح (تقريبية): ${summary.winRatePct.format(1)}%", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "تنبيه: هذا التطبيق يقدم معلومات لأغراض تعليمية فقط، وليس نصيحة مالية. التداول ينطوي على مخاطر وقد تخسر رأس مالك.",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = onOpenSignals, modifier = Modifier.weight(1f)) { Text("التوصيات") }
                Button(onClick = onOpenBacktest, modifier = Modifier.weight(1f)) { Text("باكتيست") }
            }
            Spacer(Modifier.height(2.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = onOpenWinners, modifier = Modifier.weight(1f)) { Text("الناجحة") }
                Button(onClick = onOpenSettings, modifier = Modifier.weight(1f)) { Text("الإعدادات") }
            }
        }
    }
}

private fun Double.format(d: Int): String = "%.${d}f".format(this)
