package com.proq.cryptosignals.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.proq.cryptosignals.di.LocalAppGraph
import com.proq.cryptosignals.domain.backtest.BacktestEngine
import com.proq.cryptosignals.domain.strategy.StrategyParams
import com.proq.cryptosignals.ui.components.AquaBackground
import com.proq.cryptosignals.ui.components.GlassCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun BacktestScreen(onBack: () -> Unit) {
    val graph = LocalAppGraph.current
    val binance = graph.binanceRepository

    var symbol by remember { mutableStateOf("BTCUSDT") }
    var result by remember { mutableStateOf("جاهز") }
    var running by remember { mutableStateOf(false) }

    AquaBackground {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = onBack) { Text("رجوع") }
                Spacer(Modifier.weight(1f))
                Text("باكتيست داخل التطبيق", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = symbol,
                        onValueChange = { symbol = it.uppercase() },
                        label = { Text("Symbol (مثال: BTCUSDT)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        enabled = !running,
                        onClick = {
                            running = true
                            result = "جاري التحليل..."
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val engine = BacktestEngine(binance)
                                    val params = StrategyParams.default()
                                    val r = engine.run(symbol, params)
                                    result = "Trades=${r.trades} | WinRate=${r.winRatePct.format(1)}% | Net=${r.netPct.format(2)}% | MaxDD=${r.maxDrawdownPct.format(2)}%"
                                } catch (e: Exception) {
                                    result = "فشل: ${e.message}"
                                } finally {
                                    running = false
                                }
                            }
                        }
                    ) { Text("تشغيل باكتيست (آخر 500 شمعة 15m)") }

                    Text(result, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
                    Text("ملاحظة: الباكتيست تقريبى ويعتمد على بيانات تاريخية. لا يضمن نتائج مستقبلية.",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

private fun Double.format(d: Int): String = "%.${d}f".format(this)
