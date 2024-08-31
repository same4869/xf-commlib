package com.hfzq.xfn.framework.commlib

import java.text.SimpleDateFormat
import java.util.Locale

fun getValueAsDouble(value: String?): Double? {
    if (value == null) return null

    // Handle special cases
    return when {
        value == "--" -> 0.0
        value.endsWith("%") -> value.dropLast(1).toDoubleOrNull()?.let { it / 100.0 }
        value.endsWith("万") -> value.dropLast(1).toDoubleOrNull()?.let { it * 10000 }
        value.endsWith("亿") -> value.dropLast(1).toDoubleOrNull()?.let { it * 100000000 }
        value.matches(Regex("\\d{2}-\\d{2}-\\d{2}")) -> {
            // Convert date to timestamp
            val dateFormat = SimpleDateFormat("yy-MM-dd", Locale.getDefault())
            val date = dateFormat.parse(value)
            date?.time?.toDouble()
        }

        else -> value.toDoubleOrNull()
    }
}