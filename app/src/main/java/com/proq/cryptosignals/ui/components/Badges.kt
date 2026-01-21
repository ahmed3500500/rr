package com.proq.cryptosignals.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LiveBadge(isLive: Boolean, ageSeconds: Long) {
    val shape = RoundedCornerShape(999.dp)
    val bg = if (isLive) Color(0xFF0AD47A).copy(alpha = 0.18f) else Color(0xFFFFB020).copy(alpha = 0.18f)
    val fg = if (isLive) Color(0xFF74FFC1) else Color(0xFFFFD58A)
    val text = if (isLive) "مباشر" else "متأخر ${ageSeconds}ث"
    Text(
        text = text,
        color = fg,
        modifier = Modifier.background(bg, shape).padding(horizontal = 10.dp, vertical = 6.dp),
        style = MaterialTheme.typography.labelMedium
    )
}
