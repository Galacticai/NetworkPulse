package com.galacticai.networkpulse.common.number_with_unit

data class NumberUnit(
    val unitPower: NumberUnitPower,
    val unitBase: NumberUnitBase,
) {

    override fun toString(): String = "$unitPower$unitBase"

    /** The amount a value should be multiplied by to get the value in the base unit */
    val baseMultiplier get() = unitPower.baseMultiplier

    /** The base unit of this (with [unitPower] set to [NumberUnitPower.base]) */
    val base get() = if (isBaseUnit) this else NumberUnit(unitPower.base, unitBase)

    val isBaseUnit get() = unitBase.isBaseUnit && unitPower.power == 0
}