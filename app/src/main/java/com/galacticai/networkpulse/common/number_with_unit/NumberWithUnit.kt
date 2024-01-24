package com.galacticai.networkpulse.common.number_with_unit

import java.text.DecimalFormat
import kotlin.math.abs

class NumberWithUnit(val value: Double, val unit: NumberUnit) {

    companion object {

        /** @return The value in the base unit */
        fun getInBaseUnit(value: Double, currentUnit: NumberUnit): Double =
            value * currentUnit.baseMultiplier
    }

    /** @return The value in the base unit */
    val valueInBaseUnit: Double get() = value * unit.baseMultiplier

    /** @return Copy with base unit (using [valueInBaseUnit] and [NumberUnit.base]) */
    fun toBaseUnit(): NumberWithUnit = NumberWithUnit(valueInBaseUnit, unit.base)

    /** @return Nearest [NumberUnitPower] suitable for [valueInBaseUnit] (ex: 1000 b nearest unit is Kb) */
    fun getNearestPower(
        powers: List<NumberUnitPower> = unit.unitPower.getAll()
    ): NumberUnitPower {

        if (powers.isEmpty()) throw NoSuchElementException("List is empty.")
        var nearestUnit = powers[0]
        var minDifference = Double.MAX_VALUE
        for (power in powers) {
            val difference = abs(
                (valueInBaseUnit * unit.unitPower.baseMultiplier) - power.baseMultiplier
            )
            if (difference > minDifference) continue
            minDifference = difference
            nearestUnit = power
        }
        return nearestUnit
    }

    /** @return Copy with the nearest unit (ex: 1000b -> 1Kb) */
    fun toNearestUnit(powers: List<NumberUnitPower> = unit.unitPower.getAll()): NumberWithUnit {
        val nearestUnit = getNearestPower(powers)
        val convertedValue = valueInBaseUnit / nearestUnit.baseMultiplier
        return NumberWithUnit(convertedValue, NumberUnit(nearestUnit, unit.unitBase))
    }

    fun toUnit(unit: NumberUnit): NumberWithUnit =
        NumberWithUnit(valueInBaseUnit / unit.baseMultiplier, unit)

    fun toString(separator: String, formatter: DecimalFormat) =
        "${formatter.format(value)}$separator$unit"

    override fun toString(): String = toString(" ", DecimalFormat("0.0#"))

    operator fun plus(other: NumberWithUnit): NumberWithUnit =
        NumberWithUnit(value + other.value, unit)

    operator fun minus(other: NumberWithUnit): NumberWithUnit =
        NumberWithUnit(value - other.value, unit)

    operator fun times(other: Float): NumberWithUnit = NumberWithUnit(value * other, unit)
    operator fun div(other: Float): NumberWithUnit = NumberWithUnit(value / other, unit)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NumberWithUnit) return false
        return value == other.value && unit == other.unit
    }

    override fun hashCode(): Int = 31 * value.hashCode() + unit.hashCode()
}