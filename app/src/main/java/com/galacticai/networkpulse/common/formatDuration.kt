package com.galacticai.networkpulse.common

import kotlin.time.Duration

open class DurationFormatSuffixes(
    val months: String,
    val days: String,
    val hours: String,
    val minutes: String,
    val seconds: String,
    val milliseconds: String,
) {
    data object Default : DurationFormatSuffixes(
        months = "mo",
        days = "d",
        hours = "h",
        minutes = "m",
        seconds = "s",
        milliseconds = "ms",
    )
}

fun Duration.format(
    suffixes: DurationFormatSuffixes = DurationFormatSuffixes.Default,
    joint: String = ", ",
    /** Max number of parts to return
     *
     * Example:
     * - 2 parts would return "2d, 1h"
     * - 3 parts would return "2d, 1h, 1m"*/
    partsCount: Int = Int.MAX_VALUE,
): String = listOf(
    inWholeDays / 30 to suffixes.months,
    inWholeDays % 30 to suffixes.days,
    inWholeHours % 24 to suffixes.hours,
    inWholeMinutes % 60 to suffixes.minutes,
    inWholeSeconds % 60 to suffixes.seconds,
    inWholeMilliseconds % 1000 to suffixes.milliseconds
)
    .filter { it.first > 0 }
    .take(partsCount)
    .joinToString(joint) {
        "${it.first}${it.second}"
    }
