package com.proq.cryptosignals.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.proq.cryptosignals.di.LocalAppGraph

private val DarkColors = darkColorScheme()
private val LightColors = lightColorScheme()

@Composable
fun CryptoSignalsTheme(content: @Composable () -> Unit) {
    val graph = LocalAppGraph.current
    val settings = graph.settingsRepository
    val isDark by settings.darkModeFlow.collectAsState(initial = true)

    MaterialTheme(
        colorScheme = if (isDark) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
