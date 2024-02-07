package com.galacticai.networkpulse.common.number_with_unit

import com.galacticai.networkpulse.common.models.Jsonable
import org.json.JSONObject
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow

open class NumberWithUnit(val value: Double, val unit: NumberUnit) : Jsonable {

    companion object {
        /** @return The value in the base unit */
        fun getInBaseUnit(value: Double, currentUnit: NumberUnit): Double =
            value * currentUnit.baseMultiplier

        fun fromJson(json: JSONObject): NumberWithUnit = NumberWithUnit(
            json.getDouble("value"),
            NumberUnit.fromJson(json.getJSONObject("unit"), NumberUnitSystem.Metric)
        )
    }

    override fun toJson(): JSONObject = JSONObject().apply {
        put("value", value)
        put("unit", unit.toJson())
    }

    /** @return The value in the base unit */
    val valueInBaseUnit: Double get() = value * unit.baseMultiplier

    /** @return Copy with base unit (using [valueInBaseUnit] and [NumberUnit.base]) */
    fun toBaseUnit(): NumberWithUnit = NumberWithUnit(valueInBaseUnit, unit.base)

    //TODO: fix nearest unit selection (ex: it selects a MiB instead of KiB)
    /** @return Nearest [NumberUnitPower] suitable for [valueInBaseUnit] (ex: 1000 b nearest unit is Kb) */
    fun getNearestPower(
        powers: List<NumberUnitPower> = unit.unitPower.getAll()
    ): NumberUnitPower {
        var closestPower = powers.first()
        var minDifference = Double.MAX_VALUE
        for (power in powers) {
            val difference = abs(log10(value) - log10(10.0.pow(power.exponent)))
            if (difference < minDifference) {
                minDifference = difference
                closestPower = power
            }
        }
        return closestPower
    }
    //    =
    //        powers.minByOrNull {
    //            valueInBaseUnit.pow(it.exponent)
    //        } ?: throw NoSuchElementException("List is empty after filtering.")

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
