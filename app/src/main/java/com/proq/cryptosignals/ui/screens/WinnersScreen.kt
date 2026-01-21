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

@Composable
fun WinnersScreen(onBack: () -> Unit) {
    val graph = LocalAppGraph.current
    val winners by graph.signalRepository.successfulTradesFlow().collectAsState(initial = emptyList())

    AquaBackground {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = onBack) { Text("رجوع") }
                Spacer(Modifier.weight(1f))
            }

            Spacer(Modifier.height(10.dp))
            Text("التوصيات الناجحة ✅", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(10.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(winners) { t ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(t.symbol, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                            Text("النتيجة: ${t.state} • ربح: ${fmt(t.realizedPnlUsdt ?: 0.0)} USDT (${fmt(t.realizedPct ?: 0.0)}%)",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
                            Text("Entry: ${fmt(t.entryPrice)} | TP2: ${fmt(t.tp2)} | SL: ${fmt(t.sl)}",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

private fun fmt(v: Double): String = "%,.6f".format(v)
