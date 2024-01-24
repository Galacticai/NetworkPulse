package com.galacticai.networkpulse.common

fun assertPositive(vararg values: Number) {
    for (value in values) {
        if (value.toDouble() < 0)
            throw IllegalArgumentException("Value must be positive")
    }
}
