package com.galacticai.networkpulse.util

import java.text.NumberFormat

fun Number.localized(): String =
    NumberFormat.getInstance().format(this)

fun Number.localizedDot(maxFractionDigits: Int): String =
    NumberFormat.getInstance()
        .apply { maximumFractionDigits = maxFractionDigits }
        .format(this)
