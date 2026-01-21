package com.proq.cryptosignals.util

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SuccessLogger(private val context: Context) {

    private val fmt = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.US)

    fun logSuccessLine(line: String) {
        try {
            val dir = File(context.getExternalFilesDir(null), "successful_recommendations")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "successful_${fmt.format(Date())}.txt")
            file.appendText(line + "\n")
        } catch (_: Exception) {
        }
    }
}
