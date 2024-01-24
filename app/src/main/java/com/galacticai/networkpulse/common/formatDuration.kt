package com.galacticai.networkpulse.common

import kotlin.time.Duration

fun Duration.format(abbreviated: Boolean = false, joint: String = ", "): String {
    val months = (inWholeDays / 30).toInt()
    val days = (inWholeDays % 30).toInt()
    val hours = (inWholeHours % 24).toInt()
    val minutes = (inWholeMinutes % 60).toInt()
    val seconds = (inWholeSeconds % 60).toInt()

    val parts = mutableListOf<String>()

    if (months > 0) {
        val suffix = if (abbreviated) "mo" else " month${if (months > 1) "s" else ""}"
        parts.add("$months$suffix")
    }

    if (days > 0) {
        val suffix = if (abbreviated) "d" else " day${if (days > 1) "s" else ""}"
        parts.add("$days$suffix")
    }

    if (hours > 0) {
        val suffix = if (abbreviated) "h" else " hour${if (hours > 1) "s" else ""}"
        parts.add("$hours$suffix")
    }

    if (minutes > 0) {
        val suffix = if (abbreviated) "m" else " minute${if (minutes > 1) "s" else ""}"
        parts.add("$minutes$suffix")
    }

    if (seconds > 0) {
        val suffix = if (abbreviated) "s" else " second${if (seconds > 1) "s" else ""}"
        parts.add("$seconds$suffix")
    }

    return parts.joinToString(joint)
}
