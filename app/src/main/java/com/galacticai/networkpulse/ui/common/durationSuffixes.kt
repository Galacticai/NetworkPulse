package com.galacticai.networkpulse.ui.common

import android.content.Context
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.DateSuffixes

fun durationSuffixes(context: Context) = DateSuffixes(
    months = context.getString(R.string.month_suffix),
    days = context.getString(R.string.day_suffix),
    hours = context.getString(R.string.hour_suffix),
    minutes = context.getString(R.string.minute_suffix),
    seconds = context.getString(R.string.second_suffix),
    milliseconds = context.getString(R.string.millisecond_suffix)
)