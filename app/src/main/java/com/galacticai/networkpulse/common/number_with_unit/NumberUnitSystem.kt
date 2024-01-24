package com.galacticai.networkpulse.common.number_with_unit

open class NumberUnitSystem(val multiplier: Double) {
    object Binary : NumberUnitSystem(1024.0)
    object Metric : NumberUnitSystem(1000.0)
}