package com.proq.cryptosignals.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun AquaBackground(content: @Composable () -> Unit) {
    val brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF08111A),
            Color(0xFF061E2C),
            Color(0xFF0D2B3B),
            Color(0xFF07131D)
        )
    )
    Box(modifier = Modifier.fillMaxSize().background(brush)) { content() }
}
