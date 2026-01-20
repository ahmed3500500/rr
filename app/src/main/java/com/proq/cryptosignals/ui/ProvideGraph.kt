package com.proq.cryptosignals.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.proq.cryptosignals.di.AppGraph
import com.proq.cryptosignals.di.LocalAppGraph

@Composable
fun ProvideGraph(content: @Composable () -> Unit) {
    val ctx = LocalContext.current
    val graph = remember { AppGraph(ctx.applicationContext) }
    CompositionLocalProvider(LocalAppGraph provides graph) { content() }
}
