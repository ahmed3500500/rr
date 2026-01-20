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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.proq.cryptosignals.di.LocalAppGraph
import com.proq.cryptosignals.ui.components.AquaBackground
import com.proq.cryptosignals.ui.components.GlassCard
import kotlinx.coroutines.runBlocking

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val graph = LocalAppGraph.current
    val settings = graph.settingsRepository

    val dark by settings.darkModeFlow.collectAsState(initial = true)
    val capital by settings.capitalPerSignalFlow.collectAsState(initial = 50.0)
    val minScore by settings.minScoreFlow.collectAsState(initial = 80)

    var capitalInput by remember { mutableStateOf(capital.toString()) }
    var minScoreInput by remember { mutableStateOf(minScore.toString()) }

    AquaBackground {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = onBack) { Text("رجوع") }
                Spacer(Modifier.weight(1f))
                Text("الإعدادات", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("الوضع الداكن", color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.weight(1f))
                        Switch(checked = dark, onCheckedChange = { runBlocking { settings.setDarkMode(it) } })
                    }

                    OutlinedTextField(
                        value = capitalInput,
                        onValueChange = { capitalInput = it },
                        label = { Text("رأس المال لكل توصية (USDT)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = minScoreInput,
                        onValueChange = { minScoreInput = it },
                        label = { Text("الحد الأدنى للـ Score (قليل لكن قوي)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(onClick = {
                        val c = capitalInput.toDoubleOrNull()
                        val s = minScoreInput.toIntOrNull()
                        runBlocking {
                            if (c != null && c > 0) settings.setCapitalPerSignal(c)
                            if (s != null && s in 0..100) settings.setMinScore(s)
                        }
                    }, modifier = Modifier.align(Alignment.End)) {
                        Text("حفظ")
                    }

                    Text(
                        "إخلاء مسؤولية: التطبيق يقدم معلومات تعليمية فقط وليس توصية استثمارية. السوق متقلب وقد تتعرض للخسارة.",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
