package com.galacticai.networkpulse.common

import java.util.Calendar

/** Get the unix time at the start of the day in milliseconds ([Long]) (00:00:00 of the same day) */
fun Long.atStartOfDayMS(): Long {
    return Calendar.getInstance().apply {
        timeInMillis = this@atStartOfDayMS
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}