package com.proq.cryptosignals.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.proq.cryptosignals.ui.components.LiveBadge

@Composable
fun SignalsScreen(onBack: () -> Unit) {
    val graph = LocalAppGraph.current
    val repo = graph.signalRepository
    val signals by repo.signalsFlow().collectAsState(initial = emptyList())

    AquaBackground {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = onBack) { Text("رجوع") }
                Spacer(Modifier.weight(1f))
                Button(onClick = { graph.tradeMonitorScheduler.kick() }) { Text("تحديث الأرباح") }
            }

            Spacer(Modifier.height(10.dp))
            Text("التوصيات (قليل لكن قوي)", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(10.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(signals) { s ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(s.symbol, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(Modifier.weight(1f))
                                LiveBadge(isLive = s.isLive, ageSeconds = s.priceAgeSeconds)
                            }
                            Text("إتجاه: ${s.side} | Score: ${s.score}", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
                            Text("Entry: ${s.entryPrice} | SL: ${s.sl} | TP1: ${s.tp1} | TP2: ${s.tp2}", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
                            Text(
                                "إخلاء مسؤولية: ليست نصيحة مالية. استخدم إدارة مخاطر ولا تدخل بأموال لا تتحمل خسارتها.",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
